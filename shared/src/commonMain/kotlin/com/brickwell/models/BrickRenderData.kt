// models/BrickRenderData.kt
package com.brickwell.models

/**
 * All data needed to render a single brick.
 * iOS/Android uses this to create and position a 3D brick node.
 */
data class BrickRenderData(
    /** 3D position on cylinder surface */
    val worldPosition: WorldPosition,
    
    /** Y-axis rotation to face outward from cylinder center */
    val yRotation: Float,
    
    /** True if this brick is part of the active falling piece */
    val isActive: Boolean = false,
    
    /** True if this brick is part of the ghost piece (landing preview) */
    val isGhost: Boolean = false
)