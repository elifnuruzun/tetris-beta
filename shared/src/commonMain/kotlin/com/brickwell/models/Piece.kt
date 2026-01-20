package com.brickwell.models
data class Piece(
    val type: PieceType,
    var x: Int = 3, // Start near middle
    var y: Int = 0, // Start at top
    var rotation: Int = 0 // 0, 90, 180, 270
) {
    fun move(dx: Int, dy: Int) {
        x += dx
        y += dy
    }

    fun rotate() {
        rotation = (rotation + 90) % 360
    }
}