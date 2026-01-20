// events/GameEventListener.kt
package com.brickwell.event

/**
 * Callback interface for game events.
 * 
 * iOS/Android implements this to receive notifications for:
 * - Haptic feedback (piece moved, line cleared, etc.)
 * - Sound effects
 * - Achievements/analytics
 */
interface GameEventListener {
    
    /** Called when a new game starts */
    fun onGameStarted()
    
    /** Called when piece moves left or right */
    fun onPieceMoved()
    
    /** Called when piece rotates */
    fun onPieceRotated()
    
    /** Called when piece locks into the grid */
    fun onPieceLocked()
    
    /** Called on soft drop (piece moves down faster) */
    fun onSoftDrop()
    
    /** Called on hard drop (piece instantly drops) */
    fun onHardDrop()
    
    /** Called when one or more lines are cleared */
    fun onLinesCleared(rows: List<Int>)
    
    /** Called when player advances to a new level */
    fun onLevelUp(newLevel: Int)
    
    /** Called when the game ends */
    fun onGameOver(finalScore: Int)
}

/**
 * Empty implementation for convenience.
 * Extend this if you only need to handle some events.
 */
open class GameEventListenerAdapter : GameEventListener {
    override fun onGameStarted() {}
    override fun onPieceMoved() {}
    override fun onPieceRotated() {}
    override fun onPieceLocked() {}
    override fun onSoftDrop() {}
    override fun onHardDrop() {}
    override fun onLinesCleared(rows: List<Int>) {}
    override fun onLevelUp(newLevel: Int) {}
    override fun onGameOver(finalScore: Int) {}
}