// systems/ScoreManager.kt
package com.brickwell.system

import com.brickwell.engine.GameConfig

/**
 * Calculates score and level progression.
 */
class ScoreManager {

    /**
     * Calculate points for clearing lines.
     * 
     * @param linesCleared Number of lines cleared simultaneously
     * @param level Current game level
     * @return Points earned
     */
    fun calculatePoints(linesCleared: Int, level: Int): Int {
        val basePoints = when (linesCleared) {
            1 -> GameConfig.POINTS_SINGLE      // 100
            2 -> GameConfig.POINTS_DOUBLE      // 300
            3 -> GameConfig.POINTS_TRIPLE      // 500
            4 -> GameConfig.POINTS_TETRIS      // 800
            else -> linesCleared * GameConfig.POINTS_SINGLE
        }

        // Points are multiplied by level
        return basePoints * level
    }

    /**
     * Calculate level based on total lines cleared.
     * 
     * @param totalLinesCleared Total lines cleared in the game
     * @return Current level (starts at 1)
     */
    fun calculateLevel(totalLinesCleared: Int): Int {
        return (totalLinesCleared / GameConfig.LINES_PER_LEVEL) + 1
    }

    /**
     * Calculate lines needed to reach the next level.
     * 
     * @param totalLinesCleared Total lines cleared so far
     * @return Lines remaining until next level
     */
    fun linesToNextLevel(totalLinesCleared: Int): Int {
        val currentLevel = calculateLevel(totalLinesCleared)
        val linesForNextLevel = currentLevel * GameConfig.LINES_PER_LEVEL
        return linesForNextLevel - totalLinesCleared
    }

    /**
     * Get the name for a line clear type.
     */
    fun getLineClearName(linesCleared: Int): String {
        return when (linesCleared) {
            1 -> "Single"
            2 -> "Double"
            3 -> "Triple"
            4 -> "Tetris"
            else -> "$linesCleared Lines"
        }
    }
}