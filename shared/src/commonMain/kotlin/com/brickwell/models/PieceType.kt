// models/PieceType.kt
package com.brickwell.models

/**
 * The 7 standard Tetris pieces.
 * Each defines its shape as a list of (col, row) offsets.
 */
enum class PieceType {
    I, O, T, S, Z, J, L;

    /**
     * Get the block positions for this piece type.
     * Positions are relative to the piece's origin (0,0).
     */
    fun getBaseBlocks(): List<Block> = when (this) {
        I -> listOf(
            Block(0, 0),
            Block(1, 0),
            Block(2, 0),
            Block(3, 0)
        )
        O -> listOf(
            Block(0, 0),
            Block(1, 0),
            Block(0, 1),
            Block(1, 1)
        )
        T -> listOf(
            Block(0, 0),
            Block(1, 0),
            Block(2, 0),
            Block(1, 1)
        )
        S -> listOf(
            Block(1, 0),
            Block(2, 0),
            Block(0, 1),
            Block(1, 1)
        )
        Z -> listOf(
            Block(0, 0),
            Block(1, 0),
            Block(1, 1),
            Block(2, 1)
        )
        J -> listOf(
            Block(0, 0),
            Block(0, 1),
            Block(1, 1),
            Block(2, 1)
        )
        L -> listOf(
            Block(2, 0),
            Block(0, 1),
            Block(1, 1),
            Block(2, 1)
        )
    }

    /**
     * Width of this piece in blocks.
     */
    fun getWidth(): Int = when (this) {
        I -> 4
        O -> 2
        else -> 3
    }

    /**
     * Height of this piece in blocks.
     */
    fun getHeight(): Int = when (this) {
        I -> 1
        O -> 2
        else -> 2
    }

    companion object {
        private val VALUES = entries.toTypedArray()

        /**
         * Get a random piece type.
         */
        fun random(): PieceType = VALUES.random()
    }
}