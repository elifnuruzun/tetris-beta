// models/WorldPosition.kt
package com.brickwell.models

/**
 * 3D position in world space.
 * Used for placing bricks on the cylinder surface.
 */
data class WorldPosition(
    val x: Float,
    val y: Float,
    val z: Float
) {
    companion object {
        val ZERO = WorldPosition(0f, 0f, 0f)
    }
    
    operator fun plus(other: WorldPosition): WorldPosition {
        return WorldPosition(x + other.x, y + other.y, z + other.z)
    }
    
    operator fun minus(other: WorldPosition): WorldPosition {
        return WorldPosition(x - other.x, y - other.y, z - other.z)
    }
    
    operator fun times(scalar: Float): WorldPosition {
        return WorldPosition(x * scalar, y * scalar, z * scalar)
    }
}