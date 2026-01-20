// systems/CollisionSystem.kt
package com.brickwell.system

import com.brickwell.models.Grid
import com.brickwell.models.Piece

/**
 * Handles collision detection between pieces and the grid.
 */
class CollisionSystem {

    /**
     * Check if a piece can be placed at the given position.
     * 
     * @param grid The game grid
     * @param piece The piece to check
     * @param pieceCol Column position of piece origin
     * @param pieceRow Row position of piece origin
     * @return true if the piece can be placed without collision
     */
    fun canPlace(grid: Grid, piece: Piece, pieceCol: Int, pieceRow: Int): Boolean {
        return piece.getBlocks().all { block ->
            val col = pieceCol + block.col
            val row = pieceRow + block.row
            !grid.isOccupied(col, row)
        }
    }

    /**
     * Check if piece would collide if moved down one row.
     */
    fun wouldCollideBelow(grid: Grid, piece: Piece, pieceCol: Int, pieceRow: Int): Boolean {
        return !canPlace(grid, piece, pieceCol, pieceRow - 1)
    }

    /**
     * Check if piece would collide if moved left one column.
     */
    fun wouldCollideLeft(grid: Grid, piece: Piece, pieceCol: Int, pieceRow: Int): Boolean {
        return !canPlace(grid, piece, pieceCol - 1, pieceRow)
    }

    /**
     * Check if piece would collide if moved right one column.
     */
    fun wouldCollideRight(grid: Grid, piece: Piece, pieceCol: Int, pieceRow: Int): Boolean {
        return !canPlace(grid, piece, pieceCol + 1, pieceRow)
    }

    /**
     * Find the lowest row a piece can drop to (for hard drop).
     */
    fun findDropRow(grid: Grid, piece: Piece, pieceCol: Int, pieceRow: Int): Int {
        var targetRow = pieceRow
        while (canPlace(grid, piece, pieceCol, targetRow - 1)) {
            targetRow -= 1
        }
        return targetRow
    }
}