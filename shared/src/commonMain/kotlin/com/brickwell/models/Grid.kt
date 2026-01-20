// models/Grid.kt
package com.brickwell.models

import com.brickwell.engine.GameConfig

/**
 * The game grid - a 2D array of cells that can be occupied or empty.
 */
class Grid {

    // Grid storage: cells[row][col] = true if occupied
    private val cells: Array<BooleanArray> = Array(GameConfig.GRID_ROWS) {
        BooleanArray(GameConfig.GRID_COLUMNS) { false }
    }

    // ═══════════════════════════════════════════════════════════════
    // QUERY METHODS
    // ═══════════════════════════════════════════════════════════════

    /**
     * Check if a cell is occupied.
     * Returns true for out-of-bounds positions (walls and floor).
     */
    fun isOccupied(col: Int, row: Int): Boolean {
        // Out of bounds on sides = occupied (wall)
        if (col < 0 || col >= GameConfig.GRID_COLUMNS) return true
        // Below grid = occupied (floor)
        if (row < 0) return true
        // Above grid = not occupied (can place)
        if (row >= GameConfig.GRID_ROWS) return false

        return cells[row][col]
    }

    /**
     * Check if a cell is empty.
     */
    fun isEmpty(col: Int, row: Int): Boolean = !isOccupied(col, row)

    /**
     * Check if an entire row is complete (all cells occupied).
     */
    fun isRowComplete(row: Int): Boolean {
        if (row < 0 || row >= GameConfig.GRID_ROWS) return false
        return cells[row].all { it }
    }

    /**
     * Check if a row is completely empty.
     */
    fun isRowEmpty(row: Int): Boolean {
        if (row < 0 || row >= GameConfig.GRID_ROWS) return true
        return cells[row].none { it }
    }

    /**
     * Get the highest row that has any occupied cells.
     * Returns -1 if grid is empty.
     */
    fun highestOccupiedRow(): Int {
        for (row in (0 until GameConfig.GRID_ROWS).reversed()) {
            if (cells[row].any { it }) {
                return row
            }
        }
        return -1
    }

    /**
     * Get all occupied cell positions.
     * Used for rendering.
     */
    fun getOccupiedCells(): List<Pair<Int, Int>> {
        val result = mutableListOf<Pair<Int, Int>>()
        for (row in 0 until GameConfig.GRID_ROWS) {
            for (col in 0 until GameConfig.GRID_COLUMNS) {
                if (cells[row][col]) {
                    result.add(col to row)
                }
            }
        }
        return result
    }

    // ═══════════════════════════════════════════════════════════════
    // MODIFICATION METHODS
    // ═══════════════════════════════════════════════════════════════

    /**
     * Set a cell's occupied state.
     */
    fun setCell(col: Int, row: Int, occupied: Boolean) {
        if (col in 0 until GameConfig.GRID_COLUMNS && row in 0 until GameConfig.GRID_ROWS) {
            cells[row][col] = occupied
        }
    }

    /**
     * Lock a piece into the grid.
     */
    fun lockPiece(piece: Piece, pieceCol: Int, pieceRow: Int) {
        for (block in piece.getBlocks()) {
            val col = pieceCol + block.col
            val row = pieceRow + block.row
            setCell(col, row, true)
        }
    }

    /**
     * Clear a row and shift all rows above it down.
     */
    fun clearRow(row: Int) {
        if (row < 0 || row >= GameConfig.GRID_ROWS) return

        // Shift all rows above down by one
        for (r in row until GameConfig.GRID_ROWS - 1) {
            cells[r] = cells[r + 1].copyOf()
        }

        // Clear top row
        cells[GameConfig.GRID_ROWS - 1] = BooleanArray(GameConfig.GRID_COLUMNS) { false }
    }

    /**
     * Clear the entire grid.
     */
    fun clear() {
        for (row in 0 until GameConfig.GRID_ROWS) {
            for (col in 0 until GameConfig.GRID_COLUMNS) {
                cells[row][col] = false
            }
        }
    }

    // ═══════════════════════════════════════════════════════════════
    // DEBUG
    // ═══════════════════════════════════════════════════════════════

    /**
     * String representation for debugging.
     */
    override fun toString(): String {
        val sb = StringBuilder()
        for (row in (0 until GameConfig.GRID_ROWS).reversed()) {
            sb.append("|")
            for (col in 0 until GameConfig.GRID_COLUMNS) {
                sb.append(if (cells[row][col]) "█" else " ")
            }
            sb.append("|\n")
        }
        sb.append("+")
        repeat(GameConfig.GRID_COLUMNS) { sb.append("-") }
        sb.append("+")
        return sb.toString()
    }
}