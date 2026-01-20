// models/Piece.kt
package com.brickwell.models

/**
 * A Tetris piece (tetromino) with position and rotation.
 */
class Piece(val type: PieceType) {

    private var blocks: List<Block> = type.getBaseBlocks()
    private var rotationState: Int = 0

    /**
     * Get current block positions (relative to piece origin).
     */
    fun getBlocks(): List<Block> = blocks.toList()

    /**
     * Set blocks directly (used for rotation).
     */
    fun setBlocks(newBlocks: List<Block>) {
        blocks = newBlocks.toList()
    }

    /**
     * Calculate what the blocks would look like after a 90° clockwise rotation.
     * Does not modify the piece - use applyRotation() to actually rotate.
     */
    fun getRotatedBlocks(): List<Block> {
        // O piece doesn't rotate
        if (type == PieceType.O) {
            return blocks
        }

        // Calculate center of rotation
        val centerCol = blocks.map { it.col.toDouble() }.average()
        val centerRow = blocks.map { it.row.toDouble() }.average()

        // Rotate 90° clockwise around center
        val rotated = blocks.map { block ->
            val relCol = block.col.toDouble() - centerCol
            val relRow = block.row.toDouble() - centerRow

            // Clockwise rotation: (x, y) -> (y, -x)
            val newRelCol = relRow
            val newRelRow = -relCol

            Block(
                col = kotlin.math.round(centerCol + newRelCol).toInt(),
                row = kotlin.math.round(centerRow + newRelRow).toInt()
            )
        }

        // Normalize to ensure no negative coordinates
        val minCol = rotated.minOf { it.col }
        val minRow = rotated.minOf { it.row }

        return rotated.map { it.offset(-minCol, -minRow) }
    }

    /**
     * Apply rotation - actually modifies the piece.
     */
    fun applyRotation() {
        blocks = getRotatedBlocks()
        rotationState = (rotationState + 1) % 4
    }

    /**
     * Get current rotation state (0-3).
     */
    fun getRotationState(): Int = rotationState

    /**
     * Create a copy of this piece.
     */
    fun copy(): Piece {
        val newPiece = Piece(type)
        newPiece.blocks = this.blocks.toList()
        newPiece.rotationState = this.rotationState
        return newPiece
    }

    companion object {
        /**
         * Create a random piece.
         */
        fun random(): Piece = Piece(PieceType.random())
    }
}