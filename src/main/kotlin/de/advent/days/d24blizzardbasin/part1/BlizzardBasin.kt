@file:Suppress("PrivatePropertyName")

package de.advent.days.d24blizzardbasin.part1

import de.advent.utils.output
import de.advent.utils.readLines

private data class Coords(val x: Int, val y: Int)

private typealias Cell = MutableList<Coords>?
private typealias Grid<T> = MutableList<MutableList<T>>

private operator fun Coords.plus(other: Coords): Coords {
    val newX = x + other.x
    val newY = y + other.y
    val newValidX = if (newX >= width - 1) 1 else if (newX <= 0) width - 2 else newX
    val newValidY = if (newY >= height - 1) 1 else if (newY <= 0) height - 2 else newY
    return Coords(newValidX, newValidY)
}

private operator fun <T> Grid<T>.get(coords: Coords) = this[coords.y][coords.x]

private var width: Int = -1
private var height: Int = -1
private var snowGrid: Grid<Cell> = mutableListOf()

private fun readSnowGrid() {
    snowGrid = readLines().map { line ->
        line.map { ch ->
            when (ch) {
                '.' -> mutableListOf()
                '<' -> mutableListOf(Coords(-1, 0))
                '>' -> mutableListOf(Coords(1, 0))
                '^' -> mutableListOf(Coords(0, -1))
                'v' -> mutableListOf(Coords(0, 1))
                '#' -> null
                else -> error("unknown symbol")
            }
        }.toMutableList()
    }.toMutableList()
    width = snowGrid.first().size
    height = snowGrid.size
}

private fun makeNextSnowGrid(): Grid<Cell> {
    val gridCopy: Grid<Cell> = snowGrid.map { row ->
        row.map {
            mutableListOf<Coords>()
        }.toMutableList<MutableList<Coords>?>()
    }.toMutableList()
    snowGrid.forEachIndexed { y, row ->
        row.forEachIndexed { x, cell ->
            if (cell == null) gridCopy[y][x] = null
            else if (cell.isNotEmpty()) cell.forEach { blizzard ->
                gridCopy[blizzard + Coords(x, y)]!!.add(blizzard)
            }
        }
    }
    return gridCopy
}

fun main() {
    readSnowGrid()
    var canBeHere: Grid<Boolean> = MutableList(height) { MutableList(width) { false } }
    fun Grid<Boolean>.safeGet(y: Int, x: Int): Boolean {
        if (y !in 0 until height || x !in 0 until width) return false
        return this[y][x]
    }
    canBeHere[0][1] = true
    var curStep = 0
    while (true) {
        curStep += 1
        val nextSnowGrid = makeNextSnowGrid()
        val nextCanBeHere: Grid<Boolean> = MutableList(height) { MutableList(width) { false } }
        for (y in 0 until height) {
            for (x in 0 until width) {
                // check if it is free
                if (nextSnowGrid[y][x] == null || nextSnowGrid[y][x]!!.isNotEmpty()) {
                    nextCanBeHere[y][x] = false
                    continue
                }
                // check if reachable from other cells or through waiting
                if (canBeHere.safeGet(y - 1, x)
                    || canBeHere.safeGet(y + 1, x)
                    || canBeHere.safeGet(y, x - 1)
                    || canBeHere.safeGet(y, x + 1) || canBeHere[y][x]
                ) {
                    nextCanBeHere[y][x] = true
                    if (y == height - 1 && x == width - 2) {
                        output(curStep)
                        return
                    }
                    continue
                }
                // just unreachable
                nextCanBeHere[y][x] = false
            }
        }
        canBeHere = nextCanBeHere
        snowGrid = nextSnowGrid
    }
}