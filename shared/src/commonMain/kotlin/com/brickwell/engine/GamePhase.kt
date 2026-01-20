// engine/GamePhase.kt
package com.brickwell.engine

enum class GamePhase {
    /** Game not yet started */
    READY,
    
    /** Active gameplay */
    PLAYING,
    
    /** Paused by user */
    PAUSED,
    
    /** Game ended - bricks reached top */
    GAME_OVER
}