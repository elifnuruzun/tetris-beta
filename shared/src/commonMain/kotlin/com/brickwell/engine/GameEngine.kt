// engine/GameEngine.kt
package com.brickwell.engine

import com.brickwell.models.*
import com.brickwell.systems.*
import com.brickwell.events.*

/**
 * Main game controller - THE BRAIN.
 *
 * Owns all state. Returns RenderState every frame.
 * iOS/Android only renders what this tells them to render.
 *
 * ## Required Dependencies (for teammates implementing these)
 *
 * ### Piece (com.brickwell.models.Piece)
 * ```
 * class Piece {
 *     fun getBlocks(): List<Block>           // Returns blocks with col/row offsets from piece origin
 *     fun getRotatedBlocks(): List<Block>    // Returns blocks after 90° clockwise rotation
 *     fun applyRotation()                    // Mutates piece to rotated state
 *     fun copy(): Piece                      // Creates a copy for collision testing
 *     fun setBlocks(blocks: List<Block>)     // Sets blocks (used on copied piece for testing)
 *     companion object { fun random(): Piece } // Creates random piece type
 * }
 * data class Block(val col: Int, val row: Int)
 * ```
 *
 * ### Grid (com.brickwell.systems.Grid)
 * ```
 * class Grid {
 *     fun lockPiece(piece: Piece, col: Int, row: Int)  // Locks piece blocks into grid
 *     fun getOccupiedCells(): List<Pair<Int, Int>>     // Returns all (col, row) with bricks
 *     fun highestOccupiedRow(): Int                     // Returns highest row with a brick (-1 if empty)
 * }
 * ```
 *
 * ### CollisionSystem (com.brickwell.systems.CollisionSystem)
 * ```
 * class CollisionSystem {
 *     fun canPlace(grid: Grid, piece: Piece, col: Int, row: Int): Boolean
 *     // Returns true if piece can be placed at (col, row) without collision
 *     // Must check: grid bounds, occupied cells, piece blocks
 * }
 * ```
 *
 * ### LineDetector (com.brickwell.systems.LineDetector)
 * ```
 * class LineDetector {
 *     fun findCompleteLines(grid: Grid): List<Int>     // Returns row indices that are full
 *     fun clearLines(grid: Grid, rows: List<Int>)      // Removes rows and shifts above rows down
 * }
 * ```
 *
 * ### ScoreManager (com.brickwell.systems.ScoreManager)
 * ```
 * class ScoreManager {
 *     fun calculatePoints(lineCount: Int, level: Int): Int  // Points for clearing lines
 *     fun calculateLevel(totalLinesCleared: Int): Int       // Level based on total lines
 * }
 * ```
 *
 * ### GridToWorld (com.brickwell.systems.GridToWorld)
 * ```
 * object GridToWorld {
 *     fun convert(col: Int, row: Int): WorldPosition    // Grid coords to 3D cylinder position
 *     fun getYRotation(col: Int): Float                 // Y-axis rotation for brick at column
 *     fun convertForPreview(col: Int, row: Int): WorldPosition  // For next piece preview
 * }
 * ```
 *
 * ### InputEvent (com.brickwell.events.InputEvent)
 * ```
 * sealed class InputEvent {
 *     object MoveLeft : InputEvent()
 *     object MoveRight : InputEvent()
 *     object Rotate : InputEvent()
 *     object SoftDrop : InputEvent()
 *     object HardDrop : InputEvent()
 *     object Pause : InputEvent()
 *     object Resume : InputEvent()
 * }
 * ```
 *
 * ### GameEventListener (com.brickwell.events.GameEventListener)
 * ```
 * interface GameEventListener {
 *     fun onGameStarted()
 *     fun onPieceMoved()
 *     fun onPieceRotated()
 *     fun onSoftDrop()
 *     fun onHardDrop()
 *     fun onPieceLocked()
 *     fun onLinesCleared(rows: List<Int>)
 *     fun onLevelUp(newLevel: Int)
 *     fun onGameOver(finalScore: Int)
 * }
 * ```
 *
 * ### BrickRenderData (com.brickwell.models.BrickRenderData)
 * ```
 * data class BrickRenderData(
 *     val worldPosition: WorldPosition,
 *     val yRotation: Float,
 *     val isActive: Boolean,
 *     val isGhost: Boolean
 * )
 * ```
 *
 * ### WorldPosition (com.brickwell.models.WorldPosition)
 * ```
 * data class WorldPosition(val x: Float, val y: Float, val z: Float)
 * ```
 */
class GameEngine {

    // ═══════════════════════════════════════════════════════════════
    // GAME STATE
    // ═══════════════════════════════════════════════════════════════
    
    private var phase = GamePhase.READY
    private var grid = Grid()
    private var activePiece: Piece? = null
    private var nextPiece: Piece? = null
    private var pieceCol = GameConfig.SPAWN_COLUMN
    private var pieceRow = GameConfig.SPAWN_ROW
    private var score = 0
    private var level = 1
    private var totalLinesCleared = 0
    private var descentOffset = 0f
    private var lastDropTime = 0L
    private var linesToDestroy: List<Int> = emptyList()
    private var destructionTimeRemainingMs = 0L

    // ═══════════════════════════════════════════════════════════════
    // SYSTEMS
    // ═══════════════════════════════════════════════════════════════
    
    private val collisionSystem = CollisionSystem()
    private val lineDetector = LineDetector()
    private val scoreManager = ScoreManager()

    // ═══════════════════════════════════════════════════════════════
    // EVENT LISTENERS
    // ═══════════════════════════════════════════════════════════════
    
    private val listeners = mutableListOf<GameEventListener>()

    fun addListener(listener: GameEventListener) {
        listeners.add(listener)
    }

    fun removeListener(listener: GameEventListener) {
        listeners.remove(listener)
    }

    // ═══════════════════════════════════════════════════════════════
    // PUBLIC API
    // ═══════════════════════════════════════════════════════════════

    /**
     * Start a new game. Resets all state.
     */
    fun start() {
        phase = GamePhase.PLAYING
        grid = Grid()
        activePiece = Piece.random()
        nextPiece = Piece.random()
        pieceCol = GameConfig.SPAWN_COLUMN
        pieceRow = GameConfig.SPAWN_ROW
        score = 0
        level = 1
        totalLinesCleared = 0
        descentOffset = 0f
        lastDropTime = 0L
        linesToDestroy = emptyList()
        destructionTimeRemainingMs = 0L

        listeners.forEach { it.onGameStarted() }
    }

    /**
     * Main update loop. Called 60 times per second by iOS/Android.
     *
     * Safe to call before start() or after game over - returns appropriate RenderState.
     * Game logic (descent, drops) only updates in PLAYING phase.
     * Line destruction animations continue during all phases.
     *
     * @param currentTimeMs Current timestamp in milliseconds
     * @param deltaMs Time since last frame in milliseconds
     * @return RenderState containing everything needed to render the frame
     */
    fun update(currentTimeMs: Long, deltaMs: Long): RenderState {
        // Handle line destruction animation timer
        // Note: Animation continues during pause for smoother UX
        if (destructionTimeRemainingMs > 0) {
            destructionTimeRemainingMs -= deltaMs
            if (destructionTimeRemainingMs <= 0) {
                // Animation complete - clear the list
                linesToDestroy = emptyList()
                destructionTimeRemainingMs = 0L
            }
        }

        // Only update game logic if playing
        if (phase != GamePhase.PLAYING) {
            return buildRenderState()
        }

        // Initialize lastDropTime on first frame
        if (lastDropTime == 0L) {
            lastDropTime = currentTimeMs
        }

        // 1. Update descent (time pressure)
        descentOffset += GameConfig.DESCENT_SPEED * deltaMs

        // 2. Check game over (bricks above visible area)
        if (checkGameOver()) {
            phase = GamePhase.GAME_OVER
            listeners.forEach { it.onGameOver(score) }
            return buildRenderState()
        }

        // 3. Auto-drop piece based on timer
        val dropInterval = calculateDropInterval()
        if (currentTimeMs - lastDropTime >= dropInterval) {
            dropPiece()
            lastDropTime = currentTimeMs
        }

        return buildRenderState()
    }

    /**
     * Handle player input.
     *
     * Safe to call in any game phase - invalid inputs for current phase are ignored.
     * Pause/Resume work in PLAYING/PAUSED states respectively.
     * Movement inputs only work in PLAYING state.
     *
     * @param event The input event from iOS/Android
     */
    fun handleInput(event: InputEvent) {
        when (event) {
            is InputEvent.MoveLeft -> moveLeft()
            is InputEvent.MoveRight -> moveRight()
            is InputEvent.Rotate -> rotate()
            is InputEvent.SoftDrop -> softDrop()
            is InputEvent.HardDrop -> hardDrop()
            is InputEvent.Pause -> pause()
            is InputEvent.Resume -> resume()
        }
    }

    // ═══════════════════════════════════════════════════════════════
    // INPUT HANDLERS
    // ═══════════════════════════════════════════════════════════════

    private fun moveLeft() {
        if (phase != GamePhase.PLAYING) return
        val piece = activePiece ?: return

        if (collisionSystem.canPlace(grid, piece, pieceCol - 1, pieceRow)) {
            pieceCol -= 1
            listeners.forEach { it.onPieceMoved() }
        }
    }

    private fun moveRight() {
        if (phase != GamePhase.PLAYING) return
        val piece = activePiece ?: return

        if (collisionSystem.canPlace(grid, piece, pieceCol + 1, pieceRow)) {
            pieceCol += 1
            listeners.forEach { it.onPieceMoved() }
        }
    }

    private fun rotate() {
        if (phase != GamePhase.PLAYING) return
        val piece = activePiece ?: return

        // Create test piece with rotation applied for collision checking
        val rotatedBlocks = piece.getRotatedBlocks()
        val testPiece = piece.copy().apply { setBlocks(rotatedBlocks) }

        // Try rotation at current position, then wall kicks (left, right)
        val kickOffsets = listOf(0, -1, 1)

        for (kickOffset in kickOffsets) {
            if (collisionSystem.canPlace(grid, testPiece, pieceCol + kickOffset, pieceRow)) {
                piece.applyRotation()
                pieceCol += kickOffset
                listeners.forEach { it.onPieceRotated() }
                return
            }
        }
        // Rotation not possible - do nothing
    }

    private fun softDrop() {
        if (phase != GamePhase.PLAYING) return
        dropPiece()
        listeners.forEach { it.onSoftDrop() }
    }

    private fun hardDrop() {
        if (phase != GamePhase.PLAYING) return
        val piece = activePiece ?: return

        // Find lowest valid position
        var targetRow = pieceRow
        while (collisionSystem.canPlace(grid, piece, pieceCol, targetRow - 1)) {
            targetRow -= 1
        }

        pieceRow = targetRow
        listeners.forEach { it.onHardDrop() }
        lockPiece()
    }

    private fun pause() {
        if (phase == GamePhase.PLAYING) {
            phase = GamePhase.PAUSED
        }
    }

    private fun resume() {
        if (phase == GamePhase.PAUSED) {
            phase = GamePhase.PLAYING
            // Reset drop timer to prevent immediate drop after long pause
            lastDropTime = 0L
        }
    }

    // ═══════════════════════════════════════════════════════════════
    // GAME LOGIC
    // ═══════════════════════════════════════════════════════════════

    private fun dropPiece() {
        val piece = activePiece ?: return

        if (collisionSystem.canPlace(grid, piece, pieceCol, pieceRow - 1)) {
            pieceRow -= 1
        } else {
            lockPiece()
        }
    }

    private fun lockPiece() {
        val piece = activePiece ?: return

        // 1. Lock piece to grid
        grid.lockPiece(piece, pieceCol, pieceRow)
        listeners.forEach { it.onPieceLocked() }

        // 2. Check for complete lines
        val completedLines = lineDetector.findCompleteLines(grid)

        if (completedLines.isNotEmpty()) {
            // Calculate score
            val points = scoreManager.calculatePoints(completedLines.size, level)
            score += points
            totalLinesCleared += completedLines.size

            // Check level up
            val newLevel = scoreManager.calculateLevel(totalLinesCleared)
            if (newLevel > level) {
                level = newLevel
                listeners.forEach { it.onLevelUp(level) }
            }

            // Store for destruction animation and start timer
            linesToDestroy = completedLines
            destructionTimeRemainingMs = GameConfig.LINE_DESTRUCTION_DURATION_MS

            // Notify listeners
            listeners.forEach { it.onLinesCleared(completedLines) }

            // Clear lines from grid (visual destruction handled by iOS during timer)
            lineDetector.clearLines(grid, completedLines)
        }

        // 3. Spawn next piece
        spawnNextPiece()
    }

    private fun spawnNextPiece() {
        activePiece = nextPiece
        nextPiece = Piece.random()
        pieceCol = GameConfig.SPAWN_COLUMN
        pieceRow = GameConfig.SPAWN_ROW

        // Check if new piece can be placed (instant game over if not)
        val piece = activePiece
        if (piece != null && !collisionSystem.canPlace(grid, piece, pieceCol, pieceRow)) {
            phase = GamePhase.GAME_OVER
            listeners.forEach { it.onGameOver(score) }
        }
    }

    private fun checkGameOver(): Boolean {
        val visibleTopRow = (descentOffset / GameConfig.BRICK_HEIGHT).toInt() + GameConfig.VISIBLE_ROWS
        return grid.highestOccupiedRow() >= visibleTopRow - 2
    }

    private fun calculateDropInterval(): Long {
        val reduction = (level - 1) * GameConfig.DROP_SPEED_REDUCTION_PER_LEVEL
        return maxOf(
            GameConfig.MIN_DROP_INTERVAL_MS,
            GameConfig.INITIAL_DROP_INTERVAL_MS - reduction
        )
    }

    // ═══════════════════════════════════════════════════════════════
    // GHOST PIECE (preview of where piece will land)
    // ═══════════════════════════════════════════════════════════════

    private fun calculateGhostRow(): Int {
        val piece = activePiece ?: return pieceRow

        var ghostRow = pieceRow
        while (collisionSystem.canPlace(grid, piece, pieceCol, ghostRow - 1)) {
            ghostRow -= 1
        }
        return ghostRow
    }

    // ═══════════════════════════════════════════════════════════════
    // BUILD RENDER STATE
    // ═══════════════════════════════════════════════════════════════

    private fun buildRenderState(): RenderState {
        // Grid bricks
        val gridBricks = grid.getOccupiedCells().map { (col, row) ->
            BrickRenderData(
                worldPosition = GridToWorld.convert(col, row),
                yRotation = GridToWorld.getYRotation(col),
                isActive = false,
                isGhost = false
            )
        }

        // Active piece bricks
        val activeBricks = activePiece?.getBlocks()?.map { block ->
            val col = pieceCol + block.col
            val row = pieceRow + block.row
            BrickRenderData(
                worldPosition = GridToWorld.convert(col, row),
                yRotation = GridToWorld.getYRotation(col),
                isActive = true,
                isGhost = false
            )
        } ?: emptyList()

        // Ghost piece bricks
        val ghostRow = calculateGhostRow()
        val ghostBricks = if (ghostRow != pieceRow) {
            activePiece?.getBlocks()?.map { block ->
                val col = pieceCol + block.col
                val row = ghostRow + block.row
                BrickRenderData(
                    worldPosition = GridToWorld.convert(col, row),
                    yRotation = GridToWorld.getYRotation(col),
                    isActive = false,
                    isGhost = true
                )
            } ?: emptyList()
        } else {
            emptyList()
        }

        // Next piece preview
        val nextBricks = nextPiece?.getBlocks()?.map { block ->
            BrickRenderData(
                worldPosition = GridToWorld.convertForPreview(block.col, block.row),
                yRotation = 0f,
                isActive = false,
                isGhost = false
            )
        } ?: emptyList()

        // Calculate descent progress
        val maxDescent = GameConfig.GRID_ROWS * GameConfig.BRICK_HEIGHT
        val progress = (descentOffset / maxDescent).coerceIn(0f, 1f)

        return RenderState(
            gridBricks = gridBricks,
            activePieceBricks = activeBricks,
            ghostPieceBricks = ghostBricks,
            nextPieceBricks = nextBricks,
            cameraYOffset = descentOffset,
            score = score,
            level = level,
            linesCleared = totalLinesCleared,
            descentProgress = progress,
            phase = phase,
            linesToDestroy = linesToDestroy
        )
    }

    // ═══════════════════════════════════════════════════════════════
    // GETTERS (for debugging/testing)
    // ═══════════════════════════════════════════════════════════════

    fun getScore(): Int = score
    fun getLevel(): Int = level
    fun getPhase(): GamePhase = phase
    fun getLinesCleared(): Int = totalLinesCleared
}