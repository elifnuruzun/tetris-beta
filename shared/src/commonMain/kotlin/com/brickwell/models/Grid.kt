package com.brickwell.models
class Grid {
    // 20 Rows, 10 Columns. 
    // null = Empty, PieceType = Filled with that color block
    private val cells = Array(20) { Array<PieceType?>(10) { null } }

    fun isCellEmpty(x: Int, y: Int): Boolean {
        if (x !in 0 until 10 || y !in 0 until 20) return false // Out of bounds is "not empty"
        return cells[y][x] == null
    }

    fun fillCell(x: Int, y: Int, type: PieceType) {
        if (x in 0 until 10 && y in 0 until 20) {
            cells[y][x] = type
        }
    }

    fun clearRow(row: Int) {
        for (x in 0 until 10) {
            cells[row][x] = null
        }
    }
    
    // Helper for UI to get data
    fun getRow(y: Int): Array<PieceType?> {
        return cells[y]
    }
}