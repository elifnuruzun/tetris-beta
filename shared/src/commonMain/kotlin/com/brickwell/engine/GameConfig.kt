// engine/GameConfig.kt
package com.brickwell.engine

object GameConfig {
    // ═══════════════════════════════════════════════════════════════
    // GRID DIMENSIONS
    // ═══════════════════════════════════════════════════════════════
    const val GRID_COLUMNS = 8
    const val GRID_ROWS = 24
    const val VISIBLE_ROWS = 16

    // ═══════════════════════════════════════════════════════════════
    // CYLINDER GEOMETRY (for 3D coordinate conversion)
    // ═══════════════════════════════════════════════════════════════
    const val CYLINDER_RADIUS = 3.0f
    const val CHANNEL_ARC = 1.885f              // ~108° in radians (π * 0.6)
    const val CHANNEL_START_ANGLE = -0.942f     // Centered on front (-π * 0.3)

    // ═══════════════════════════════════════════════════════════════
    // BRICK DIMENSIONS
    // ═══════════════════════════════════════════════════════════════
    const val BRICK_WIDTH = 0.48f
    const val BRICK_HEIGHT = 0.48f
    const val BRICK_DEPTH = 0.25f

    // ═══════════════════════════════════════════════════════════════
    // TIMING (in milliseconds)
    // ═══════════════════════════════════════════════════════════════
    const val INITIAL_DROP_INTERVAL_MS = 1000L
    const val MIN_DROP_INTERVAL_MS = 150L
    const val DROP_SPEED_REDUCTION_PER_LEVEL = 80L
    const val DESCENT_SPEED = 0.003f            // Units per millisecond
    const val LINE_DESTRUCTION_DURATION_MS = 300L  // Time for line clear animation

    // ═══════════════════════════════════════════════════════════════
    // SCORING
    // ═══════════════════════════════════════════════════════════════
    const val POINTS_SINGLE = 100
    const val POINTS_DOUBLE = 300
    const val POINTS_TRIPLE = 500
    const val POINTS_TETRIS = 800
    const val LINES_PER_LEVEL = 10

    // ═══════════════════════════════════════════════════════════════
    // SPAWN POSITION
    // ═══════════════════════════════════════════════════════════════
    const val SPAWN_COLUMN = (GRID_COLUMNS - 2) / 2
    const val SPAWN_ROW = GRID_ROWS - 4
}