package de.advent.days.d08TreetopTreeHouse.part2

import de.advent.utils.output
import de.advent.utils.setUpInput
import kotlin.streams.toList

private fun readForest(): List<List<Int>> = buildList {
    while (true) {
        val line = readlnOrNull() ?: return@buildList
        add(line.chars().toList())
    }
}

private fun countScenicScore(w: Int, h: Int, grid: List<List<Int>>): Int {
    val width = grid.first().size
    val height = grid.size
    val self = grid[h][w]

    val leftShort = (0 until w).reversed().takeWhile { grid[h][it] < self }
    val leftEq = (leftShort.minOrNull() ?: w).let { if (it > 0) 1 else 0 }
    val left = leftShort.count() + leftEq

    val rightShort = (w + 1 until width).takeWhile { grid[h][it] < self }
    val rightEq = (rightShort.maxOrNull() ?: w).let { if (it < width - 1) 1 else 0 }
    val right = rightShort.count() + rightEq

    val upShort = (0 until h).reversed().takeWhile { grid[it][w] < self }
    val upEq = (upShort.minOrNull() ?: h).let { if (it > 0) 1 else 0 }
    val up = upShort.count() + upEq

    val downShort = (h + 1 until height).takeWhile { grid[it][w] < self }
    val downEq = (downShort.maxOrNull() ?: h).let { if (it < height - 1) 1 else 0 }
    val down = downShort.count() + downEq

    return left * right * up * down
}

fun main() {
    setUpInput()
    val grid = readForest()
    val maxScenicScore = grid.indices.maxOf { h ->
        grid.first().indices.maxOf { w -> countScenicScore(w, h, grid) }
    }
    output(maxScenicScore)
}