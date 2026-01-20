// engine/RenderState.kt
package com.brickwell.engine

import com.brickwell.models.BrickRenderData

/**
 * Complete frame data for iOS/Android to render.
 * 
 * This is THE CONTRACT between KMP and platform renderers.
 * iOS receives this every frame and renders exactly what it describes.
 */
data class RenderState(
    // ═══════════════════════════════════════════════════════════════
    // BRICK DATA
    // ═══════════════════════════════════════════════════════════════
    
    /** All locked bricks on the grid */
    val gridBricks: List<BrickRenderData>,
    
    /** The currently falling piece (4 bricks) */
    val activePieceBricks: List<BrickRenderData>,
    
    /** Ghost piece showing where active piece will land */
    val ghostPieceBricks: List<BrickRenderData>,
    
    /** Next piece preview */
    val nextPieceBricks: List<BrickRenderData>,

    // ═══════════════════════════════════════════════════════════════
    // CAMERA
    // ═══════════════════════════════════════════════════════════════
    
    /** How far the camera has descended (time pressure mechanic) */
    val cameraYOffset: Float,

    // ═══════════════════════════════════════════════════════════════
    // UI DATA
    // ═══════════════════════════════════════════════════════════════
    
    /** Current score */
    val score: Int,
    
    /** Current level */
    val level: Int,
    
    /** Total lines cleared */
    val linesCleared: Int,
    
    /** Descent progress for UI bar (0.0 to 1.0) */
    val descentProgress: Float,

    // ═══════════════════════════════════════════════════════════════
    // STATE
    // ═══════════════════════════════════════════════════════════════
    
    /** Current game phase */
    val phase: GamePhase,
    
    /** Rows that just completed (trigger destruction animation) */
    val linesToDestroy: List<Int>
) {
    // ═══════════════════════════════════════════════════════════════
    // CONVENIENCE PROPERTIES
    // ═══════════════════════════════════════════════════════════════
    
    val isPlaying: Boolean 
        get() = phase == GamePhase.PLAYING
    
    val isPaused: Boolean 
        get() = phase == GamePhase.PAUSED
    
    val isGameOver: Boolean 
        get() = phase == GamePhase.GAME_OVER
    
    val isReady: Boolean 
        get() = phase == GamePhase.READY
    
    val hasLinesToDestroy: Boolean 
        get() = linesToDestroy.isNotEmpty()

    companion object {
        /** Empty state for initialization */
        fun empty(): RenderState = RenderState(
            gridBricks = emptyList(),
            activePieceBricks = emptyList(),
            ghostPieceBricks = emptyList(),
            nextPieceBricks = emptyList(),
            cameraYOffset = 0f,
            score = 0,
            level = 1,
            linesCleared = 0,
            descentProgress = 0f,
            phase = GamePhase.READY,
            linesToDestroy = emptyList()
        )
    }
}