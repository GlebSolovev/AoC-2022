package de.advent.days.d22monkeymap.part1

import de.advent.utils.output
import de.advent.utils.readLines

private sealed interface Instruction

private enum class Rotate : Instruction {
    LEFT, RIGHT
}

private data class Move(val dist: Int) : Instruction

private enum class Cell {
    EMPTY, FREE, WALL
}

private data class Coords(val x: Int, val y: Int) {
    operator fun plus(other: Coords) = Coords(x + other.x, y + other.y)
}

private data class Grid<T>(val grid: MutableList<MutableList<T>>, val height: Int, val width: Int) {
    operator fun get(coords: Coords) = grid[coords.y][coords.x]
}

private fun Coords.teleportIfOutOfBounds(grid: Grid<Cell>, dir: Coords): Coords {
    if (x !in 0 until grid.width || y !in 0 until grid.height || grid[this] == Cell.EMPTY) {
        return when (dir) {
            Coords(1, 0) -> {
                Coords(grid.grid[y].indexOfFirst { cell -> cell != Cell.EMPTY }, y)
            }

            Coords(0, 1) -> {
                Coords(x, grid.grid.indexOfFirst { line -> line[x] != Cell.EMPTY })
            }

            Coords(-1, 0) -> {
                Coords(grid.grid[y].indexOfLast { cell -> cell != Cell.EMPTY }, y)
            }

            Coords(0, -1) -> {
                Coords(x, grid.grid.indexOfLast { line -> line[x] != Cell.EMPTY })
            }

            else -> error("invalid dir")
        }
    }
    return this
}

private data class Input(val grid: Grid<Cell>, val pass: List<Instruction>)

private fun readInput(): Input {
    val lines = readLines()
    val gridLines = lines.takeWhile { it.isNotBlank() }
    val height = gridLines.size
    val width = gridLines.maxOf { line -> line.length }
    val grid = MutableList(height) { MutableList(width) { Cell.EMPTY } }
    gridLines.forEachIndexed { y, line ->
        line.forEachIndexed { x, ch ->
            val cell = when (ch) {
                '.' -> Cell.FREE
                '#' -> Cell.WALL
                ' ' -> Cell.EMPTY
                else -> error("unexpected char")
            }
            grid[y][x] = cell
        }
    }
    val passLine = lines.takeLastWhile { it.isNotBlank() }.joinToString(separator = "")
    val pass = buildList {
        val curMoveWord = mutableListOf<Char>()
        fun flushCurMoveWord() {
            if (curMoveWord.isNotEmpty()) {
                add(Move(curMoveWord.joinToString(separator = "").toInt()))
                curMoveWord.clear()
            }
        }
        for (ch in passLine) {
            when (ch) {
                'R' -> {
                    flushCurMoveWord()
                    add(Rotate.RIGHT)
                }

                'L' -> {
                    flushCurMoveWord()
                    add(Rotate.LEFT)
                }

                else -> curMoveWord.add(ch)
            }
        }
        flushCurMoveWord()
    }
    return Input(Grid(grid, height, width), pass)
}

private val dirs = listOf(Coords(1, 0), Coords(0, 1), Coords(-1, 0), Coords(0, -1))

private fun rotateDir(curDir: Coords, rotate: Rotate): Coords {
    var curIndex = dirs.indexOf(curDir)
    when (rotate) {
        Rotate.RIGHT -> {
            curIndex++
            if (curIndex >= dirs.size) curIndex -= dirs.size
        }

        Rotate.LEFT -> {
            curIndex--
            if (curIndex < 0) curIndex += dirs.size
        }
    }
    return dirs[curIndex]
}

private fun simulate(grid: Grid<Cell>, pass: List<Instruction>): Int {
    val startX = grid.grid.first().indexOfFirst { cell -> cell != Cell.EMPTY }
    var curPos = Coords(startX, 0)
    var curDir = Coords(1, 0)
    pass.forEach { instruction ->
        when (instruction) {
            is Rotate -> {
                curDir = rotateDir(curDir, instruction)
            }

            is Move -> {
                repeat(instruction.dist) {
                    val nextPos = (curPos + curDir).teleportIfOutOfBounds(grid, curDir)
                    if (grid[nextPos] == Cell.FREE) {
                        curPos = nextPos
                    }
                }
            }
        }
    }
    return 1000 * (curPos.y + 1) + 4 * (curPos.x + 1) + dirs.indexOf(curDir)
}

fun main() {
    val (grid, pass) = readInput()
    val password = simulate(grid, pass)
    output(password)
}