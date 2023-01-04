package de.advent.days.d14regolithreservoir.part2

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

private fun addLine(from: Coords, to: Coords) {
    if (to.depth == from.depth) {
        for (w in min(from.width, to.width)..max(from.width, to.width)) {
            grid[to.depth][w] = true
        }
    } else {
        for (d in min(from.depth, to.depth)..max(from.depth, to.depth)) {
            grid[d][to.width] = true
        }
    }
}

private fun addRock(line: String) {
    val points = line.split(" -> ").map { coordsString ->
        val (width, depth) = coordsString.split(",", limit = 2)
        Coords(depth.toInt(), width.toInt())
    }
    var prev = points.first()
    points.forEach { point ->
        addLine(prev, point)
        prev = point
    }
}

private fun findFloorDepth(): Int {
    var result = 0
    grid.forEachIndexed { depth, row ->
        if (row.any { it }) result = depth
    }
    return result + 2
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

private fun spawnAndSimulateSand() {
    if (grid[sandSpawn]) error("spawn is already stuck")
    var curCoords = sandSpawn
    while (true) {
        val newCoords = calcMoveOneSandCoords(curCoords)
        grid[curCoords] = false
        grid[newCoords] = true
        if (newCoords == curCoords) return
        curCoords = newCoords
    }
}

@Suppress("unused")
private fun printSnap() {
    grid.forEachIndexed { d, row ->
        if (d <= 12) {
            println(row.subList(490, 510).joinToString(separator = "") { if (it) "#" else "." })
        }
    }
    println()
}

fun main() {
    readLines().forEach { line -> addRock(line) }
    val floorDepth = findFloorDepth()
    addLine(Coords(floorDepth, 0), Coords(floorDepth, WIDTH - 1))
    var spawned = 0
    while (true) {
        if (grid[sandSpawn]) break
        spawnAndSimulateSand()
        spawned += 1
    }
    output(spawned)
}