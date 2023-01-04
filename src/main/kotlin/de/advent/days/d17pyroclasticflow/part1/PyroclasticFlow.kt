package de.advent.days.d17pyroclasticflow.part1

import de.advent.utils.output
import de.advent.utils.readLines
import kotlin.math.max

private data class Coords(val x: Int, val y: Int)

private const val WIDTH = 7
private const val MAX_HEIGHT = 100_000
private val grid = MutableList(MAX_HEIGHT) { MutableList(WIDTH + 2) { false } }.apply {
    this[0] = MutableList(WIDTH + 2) { true }
    indices.forEach { h ->
        this[h][0] = true
        this[h][WIDTH + 1] = true
    }
}
private var curMaxHeight = 0

private const val ROCKS = 2022

private sealed class Shape {
    protected var curCoords: MutableSet<Coords> = mutableSetOf()
    abstract fun spawn()

    val maxHeight: Int get() = curCoords.maxOf { (_, y) -> y }

    private fun moveIfPossible(nextCoords: Set<Coords>): Boolean {
        curCoords.forEach { (x, y) -> grid[y][x] = false }
        if (nextCoords.all { (x, y) -> x in 1..WIDTH && y in 1..MAX_HEIGHT && !grid[y][x] }) {
            curCoords = nextCoords.toMutableSet()
            spawnCurCoords()
            return true
        }
        spawnCurCoords()
        return false
    }

    protected fun spawnCurCoords() {
        curCoords.forEach { (x, y) -> grid[y][x] = true }
    }

    fun moveByGas(gasDir: Char) {
        val xShift = when (gasDir) {
            '>' -> 1
            '<' -> -1
            else -> error("unsupported")
        }
        val nextCoords = curCoords.map { (x, y) -> Coords(x + xShift, y) }.toSet()
        moveIfPossible(nextCoords)
    }

    fun fall(): Boolean {
        val nextCoords = curCoords.map { (x, y) -> Coords(x, y - 1) }.toSet()
        return moveIfPossible(nextCoords)
    }
}

private const val Y_SPAWN_SHIFT = 4
private const val X_SPAWN_SHIFT = 3

private class LineShape : Shape() {
    override fun spawn() {
        val startY = curMaxHeight + Y_SPAWN_SHIFT
        val startX = X_SPAWN_SHIFT
        val length = 4
        (startX until startX + length).forEach { x ->
            curCoords.add(Coords(x, startY))
        }
        spawnCurCoords()
    }
}

private class CrossShape : Shape() {
    override fun spawn() {
        val startY = curMaxHeight + Y_SPAWN_SHIFT
        val startX = X_SPAWN_SHIFT
        val length = 3
        (startX until startX + length).forEach { x ->
            curCoords.add(Coords(x, startY + 1))
        }
        (startY until startY + length).forEach { y ->
            curCoords.add(Coords(startX + 1, y))
        }
        spawnCurCoords()
    }
}

private class LShape : Shape() {
    override fun spawn() {
        val startY = curMaxHeight + Y_SPAWN_SHIFT
        val startX = X_SPAWN_SHIFT
        val length = 3
        (startX until startX + length).forEach { x ->
            curCoords.add(Coords(x, startY))
        }
        (startY until startY + length).forEach { y ->
            curCoords.add(Coords(startX + length - 1, y))
        }
        spawnCurCoords()
    }
}

private class ColShape : Shape() {
    override fun spawn() {
        val startY = curMaxHeight + Y_SPAWN_SHIFT
        val startX = X_SPAWN_SHIFT
        val length = 4
        (startY until startY + length).forEach { y ->
            curCoords.add(Coords(startX, y))
        }
        spawnCurCoords()
    }
}

private class SquareShape : Shape() {
    override fun spawn() {
        val startY = curMaxHeight + Y_SPAWN_SHIFT
        val startX = X_SPAWN_SHIFT
        val length = 2
        (startX until startX + length).forEach { x ->
            curCoords.add(Coords(x, startY))
            curCoords.add(Coords(x, startY + length - 1))
        }
        spawnCurCoords()
    }
}

private const val SHAPES_NUMBER = 5

@Suppress("unused")
private fun printTopHeight(top: Int = 25) {
    val fromY = max((curMaxHeight - top + 1), 0)
    val text = (fromY..(fromY + top)).joinToString(separator = "\n") { y ->
        (1..WIDTH).joinToString(separator = "") { x -> if (grid[y][x]) "#" else "." }
    }
    println("\n$text")
}

fun main() {
    val gasPlan = readLines().joinToString(separator = "")
    var shapeIndex = 0
    var gasIndex = 0
    repeat(ROCKS) {
        shapeIndex++
        val shape = when (shapeIndex % SHAPES_NUMBER) {
            1 -> LineShape()
            2 -> CrossShape()
            3 -> LShape()
            4 -> ColShape()
            0 -> SquareShape()
            else -> error("unreachable")
        }
        shape.spawn()
        while (true) {
            shape.moveByGas(gasPlan[gasIndex++ % gasPlan.length])
            if (!shape.fall()) {
                curMaxHeight = max(curMaxHeight, shape.maxHeight)
                break
            }
        }
    }
    output(curMaxHeight)
}