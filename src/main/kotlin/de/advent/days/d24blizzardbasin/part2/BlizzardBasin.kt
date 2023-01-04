@file:Suppress("PrivatePropertyName")

package de.advent.days.d24blizzardbasin.part2

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

private const val FIELDS = 3

fun main() {
    readSnowGrid()
    var canBeHere: MutableList<Grid<Boolean>> =
        MutableList(FIELDS) { MutableList(height) { MutableList(width) { false } } }

    fun MutableList<Grid<Boolean>>.safeGet(f: Int, y: Int, x: Int): Boolean {
        if (y !in 0 until height || x !in 0 until width) return false
        return this[f][y][x]
    }
    canBeHere[0][0][1] = true
    var curStep = 0
    while (true) {
        curStep += 1
        val nextSnowGrid = makeNextSnowGrid()
        val nextCanBeHere: MutableList<Grid<Boolean>> =
            MutableList(FIELDS) { MutableList(height) { MutableList(width) { false } } }
        for (f in 0 until FIELDS) {
            for (y in 0 until height) {
                for (x in 0 until width) {
                    // check if it is free
                    if (nextSnowGrid[y][x] == null || nextSnowGrid[y][x]!!.isNotEmpty()) {
                        nextCanBeHere[f][y][x] = false
                        continue
                    }
                    // check if reachable from other cells or through waiting
                    if (canBeHere.safeGet(f, y - 1, x)
                        || canBeHere.safeGet(f, y + 1, x)
                        || canBeHere.safeGet(f, y, x - 1)
                        || canBeHere.safeGet(f, y, x + 1) || canBeHere[f][y][x]
                    ) {
                        nextCanBeHere[f][y][x] = true
                        if (f == 0 && y == height - 1 && x == width - 2) {
                            nextCanBeHere[1][y][x] = true
                        } else if (f == 1 && y == 0 && x == 1) {
                            nextCanBeHere[2][y][x] = true
                        } else if (f == 2 && y == height - 1 && x == width - 2) {
                            output(curStep)
                            return
                        }
                        continue
                    }
                }
            }
        }
        canBeHere = nextCanBeHere
        snowGrid = nextSnowGrid
    }
}