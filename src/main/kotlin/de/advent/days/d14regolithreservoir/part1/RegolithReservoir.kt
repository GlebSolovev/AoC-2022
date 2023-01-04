package de.advent.days.d14regolithreservoir.part1

import de.advent.utils.output
import de.advent.utils.readLines
import kotlin.math.max
import kotlin.math.min

private data class Coords(val depth: Int, val width: Int)

private const val WIDTH = 1000
private const val DEPTH = 1000
private val grid = MutableList(DEPTH) { MutableList(WIDTH) { false } }

private operator fun MutableList<MutableList<Boolean>>.get(coords: Coords): Boolean = this[coords.depth][coords.width]
private operator fun MutableList<MutableList<Boolean>>.set(coords: Coords, value: Boolean) {
    this[coords.depth][coords.width] = value
}

private val sandSpawn = Coords(0, 500)

private fun addRock(line: String) {
    val points = line.split(" -> ").map { coordsString ->
        val (width, depth) = coordsString.split(",", limit = 2)
        Coords(depth.toInt(), width.toInt())
    }
    var prev = points.first()
    points.forEach { point ->
        if (point.depth == prev.depth) {
            for (w in min(prev.width, point.width)..max(prev.width, point.width)) {
                grid[point.depth][w] = true
            }
        } else {
            for (d in min(prev.depth, point.depth)..max(prev.depth, point.depth)) {
                grid[d][point.width] = true
            }
        }
        prev = point
    }
}

private fun findAbyssDepth(): Int {
    var result = 0
    grid.forEachIndexed { depth, row ->
        if (row.any { it }) result = depth
    }
    return result
}

private fun calcMoveOneSandCoords(fromCoords: Coords): Coords {
    val down = Coords(fromCoords.depth + 1, fromCoords.width)
    if (!grid[down]) return down
    val leftDown = Coords(fromCoords.depth + 1, fromCoords.width - 1)
    if (!grid[leftDown]) return leftDown
    val rightDown = Coords(fromCoords.depth + 1, fromCoords.width + 1)
    if (!grid[rightDown]) return rightDown
    return fromCoords
}

private fun trySpawnAndSimulateSand(abyssDepth: Int): Boolean {
    var curCoords = sandSpawn
    var moved = false
    while (true) {
        if (curCoords.depth == abyssDepth) {
            grid[curCoords] = false
            return false
        }
        val newCoords = calcMoveOneSandCoords(curCoords)
        if (newCoords == curCoords) {
            if (!moved) error("sand is stuck at the start")
            return true
        }
        grid[curCoords] = false
        grid[newCoords] = true
        curCoords = newCoords
        moved = true
    }
}

fun main() {
    readLines().forEach { line -> addRock(line) }
    val abyssDepth = findAbyssDepth()
    var spawned = 0
    while (true) {
        val cameToRest = trySpawnAndSimulateSand(abyssDepth)
        if (!cameToRest) break
        spawned += 1
    }
    output(spawned)
}