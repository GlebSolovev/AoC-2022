package de.advent.days.d08TreetopTreeHouse.part1

import de.advent.utils.output
import de.advent.utils.setUpInput
import kotlin.streams.toList

private fun readForest(): List<List<Int>> = buildList {
    while (true) {
        val line = readlnOrNull() ?: return@buildList
        add(line.chars().toList())
    }
}

private fun isVisible(w: Int, h: Int, grid: List<List<Int>>): Boolean {
    val width = grid.first().size
    val height = grid.size
    val self = grid[h][w]
    val left = (0 until w).all { grid[h][it] < self }
    val right = (w + 1 until width).all { grid[h][it] < self }
    val up = (0 until h).all { grid[it][w] < self }
    val down = (h + 1 until height).all { grid[it][w] < self }
    return left || right || up || down
}

fun main() {
    setUpInput()
    val grid = readForest()
    val visibleTreesCount = grid.indices.sumOf { h ->
        grid.first().indices.count { w -> isVisible(w, h, grid) }
    }
    output(visibleTreesCount)
}