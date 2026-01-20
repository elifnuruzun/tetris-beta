// systems/LineDetector.kt
package com.brickwell.system

import com.brickwell.models.Grid
import com.brickwell.engine.GameConfig

/**
 * Detects and clears complete lines in the grid.
 */
class LineDetector {

    /**
     * Find all complete lines in the grid.
     * 
     * @param grid The game grid
     * @return List of row indices that are complete (sorted ascending)
     */
    fun findCompleteLines(grid: Grid): List<Int> {
        val completeLines = mutableListOf<Int>()

        for (row in 0 until GameConfig.GRID_ROWS) {
            if (grid.isRowComplete(row)) {
                completeLines.add(row)
            }
        }

        return completeLines
    }

    /**
     * Clear the specified lines from the grid.
     * Lines are cleared from top to bottom to avoid index shifting issues.
     * 
     * @param grid The game grid
     * @param lines List of row indices to clear
     */
    fun clearLines(grid: Grid, lines: List<Int>) {
        // Must clear from top to bottom (descending order)
        // so that row indices remain valid as we clear
        lines.sortedDescending().forEach { row ->
            grid.clearRow(row)
        }
    }

    /**
     * Count how many lines would be cleared if checked now.
     */
    fun countCompleteLines(grid: Grid): Int {
        return findCompleteLines(grid).size
    }

    /**
     * Check if a specific row is complete.
     */
    fun isLineComplete(grid: Grid, row: Int): Boolean {
        return grid.isRowComplete(row)
    }
}