package com.brickwell.models
enum class PieceType {
    I, J, L, O, S, T, Z;

    // Define the shape for each rotation (4x4 grid concept)
    // 0 = empty, 1 = filled
    fun getBaseShape(): List<List<Int>> {
        return when (this) {
            I -> listOf(
                listOf(0, 0, 0, 0),
                listOf(1, 1, 1, 1),
                listOf(0, 0, 0, 0),
                listOf(0, 0, 0, 0)
            )
            O -> listOf(
                listOf(0, 1, 1, 0),
                listOf(0, 1, 1, 0),
                listOf(0, 0, 0, 0),
                listOf(0, 0, 0, 0)
            )
            T -> listOf(
                listOf(0, 1, 0, 0),
                listOf(1, 1, 1, 0),
                listOf(0, 0, 0, 0),
                listOf(0, 0, 0, 0)
            )
            // ... (Dev K1 needs to fill L, J, S, Z patterns)
            else -> listOf(listOf(0)) // Placeholder
        }
    }
}