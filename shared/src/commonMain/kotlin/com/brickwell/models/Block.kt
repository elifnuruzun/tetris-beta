// models/Block.kt
package com.brickwell.models

/**
 * A single block position within a piece or grid.
 * Uses column (x) and row (y) coordinates.
 */
data class Block(
    val col: Int,
    val row: Int
) {
    /**
     * Create a new block offset from this one.
     */
    fun offset(dCol: Int, dRow: Int): Block {
        return Block(col + dCol, row + dRow)
    }
    
    /**
     * Create a copy at a new position.
     */
    fun moveTo(newCol: Int, newRow: Int): Block {
        return Block(newCol, newRow)
    }
}