// events/InputEvent.kt
package com.brickwell.event

/**
 * Input events from iOS/Android.
 * 
 * The platform layer translates touch gestures into these events,
 * then sends them to GameEngine.handleInput().
 */
sealed class InputEvent {
    
    /** Move piece one column to the left */
    object MoveLeft : InputEvent()
    
    /** Move piece one column to the right */
    object MoveRight : InputEvent()
    
    /** Rotate piece 90° clockwise */
    object Rotate : InputEvent()
    
    /** Move piece down one row (soft drop) */
    object SoftDrop : InputEvent()
    
    /** Instantly drop piece to lowest valid position (hard drop) */
    object HardDrop : InputEvent()
    
    /** Pause the game */
    object Pause : InputEvent()
    
    /** Resume from pause */
    object Resume : InputEvent()

    override fun toString(): String = this::class.simpleName ?: "InputEvent"
}