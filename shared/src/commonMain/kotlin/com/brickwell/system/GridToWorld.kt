// systems/GridToWorld.kt
package com.brickwell.system

import com.brickwell.models.WorldPosition
import com.brickwell.engine.GameConfig
import kotlin.math.cos
import kotlin.math.sin

/**
 * Converts 2D grid coordinates to 3D world positions on the cylinder surface.
 * 
 * The play area is a "pizza slice" channel on the front of a cylinder.
 * This maps grid (col, row) to (x, y, z) positions on that curved surface.
 */
object GridToWorld {

    /**
     * Convert grid coordinates to 3D world position on cylinder surface.
     * 
     * @param col Grid column (0 to GRID_COLUMNS-1)
     * @param row Grid row (0 = bottom, GRID_ROWS-1 = top)
     * @return 3D position on the cylinder surface
     */
    fun convert(col: Int, row: Int): WorldPosition {
        // Map column to angle within the channel arc
        // col 0 = left edge of channel
        // col GRID_COLUMNS-1 = right edge of channel
        val t = col.toFloat() / (GameConfig.GRID_COLUMNS - 1).toFloat()
        val angle = GameConfig.CHANNEL_START_ANGLE + t * GameConfig.CHANNEL_ARC

        // Calculate position on cylinder surface
        val x = sin(angle) * GameConfig.CYLINDER_RADIUS
        val z = cos(angle) * GameConfig.CYLINDER_RADIUS
        val y = row.toFloat() * GameConfig.BRICK_HEIGHT

        return WorldPosition(x, y, z)
    }

    /**
     * Get the Y-axis rotation for a brick at the given column.
     * This makes the brick face outward from the cylinder center.
     * 
     * @param col Grid column
     * @return Rotation angle in radians
     */
    fun getYRotation(col: Int): Float {
        val t = col.toFloat() / (GameConfig.GRID_COLUMNS - 1).toFloat()
        val angle = GameConfig.CHANNEL_START_ANGLE + t * GameConfig.CHANNEL_ARC
        return -angle  // Negative so brick faces outward
    }

    /**
     * Convert for the "next piece" preview display.
     * Positions piece in the top-right corner of the screen (flat, not on cylinder).
     * 
     * @param col Block column within piece (0-3)
     * @param row Block row within piece (0-1)
     * @return 3D position for preview display
     */
    fun convertForPreview(col: Int, row: Int): WorldPosition {
        return WorldPosition(
            x = 2.5f + col * GameConfig.BRICK_WIDTH,
            y = 12f + row * GameConfig.BRICK_HEIGHT,
            z = 0f
        )
    }

    /**
     * Convert world X/Z position back to a grid column (inverse of convert).
     * Useful for debugging or advanced features.
     * 
     * @param x World X position
     * @param z World Z position
     * @return Approximate grid column
     */
    fun getColumnFromWorld(x: Float, z: Float): Int {
        val angle = kotlin.math.atan2(x, z)
        val t = (angle - GameConfig.CHANNEL_START_ANGLE) / GameConfig.CHANNEL_ARC
        return (t * (GameConfig.GRID_COLUMNS - 1))
            .toInt()
            .coerceIn(0, GameConfig.GRID_COLUMNS - 1)
    }

    /**
     * Convert world Y position back to a grid row (inverse of convert).
     * 
     * @param y World Y position
     * @return Approximate grid row
     */
    fun getRowFromWorld(y: Float): Int {
        return (y / GameConfig.BRICK_HEIGHT).toInt()
    }
}