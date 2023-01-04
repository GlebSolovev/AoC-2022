package de.advent.days.d22monkeymap.part2

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
    operator fun minus(other: Coords) = Coords(x - other.x, y - other.y)
}

private data class Grid<T>(val grid: MutableList<MutableList<T>>, val height: Int, val width: Int) {
    operator fun get(coords: Coords) = grid[coords.y][coords.x]
}


private enum class Dir(val coords: Coords) {
    RIGHT(Coords(1, 0)), DOWN(Coords(0, 1)), LEFT(Coords(-1, 0)), UP(Coords(0, -1))
}

private val dirs = listOf(Dir.RIGHT, Dir.DOWN, Dir.LEFT, Dir.UP)

private const val SQUARE_SIZE = 50

private sealed class Square(
    val id: Int,
    val leftIndex: Int,
    val rightIndex: Int,
    val upperIndex: Int,
    val lowerIndex: Int
) {
    data class MoveInfo(val newPos: Coords, val newDir: Dir)

    fun move(from: Coords, dir: Dir): MoveInfo {
        val (toSquareId, newDir, inverse) = moveToSquare(dir)
        val toSquare = squaresById[toSquareId]!!
        val curShift = when (dir) {
            Dir.LEFT, Dir.RIGHT -> {
                from.y - upperIndex * SQUARE_SIZE
            }

            Dir.DOWN, Dir.UP -> {
                from.x - leftIndex * SQUARE_SIZE
            }
        }
        val relShift = if (inverse) SQUARE_SIZE - curShift - 1 else curShift
        val newPos = when (newDir) {
            Dir.RIGHT -> {
                Coords(toSquare.leftIndex * SQUARE_SIZE, toSquare.upperIndex * SQUARE_SIZE + relShift)
            }

            Dir.DOWN -> {
                Coords(toSquare.leftIndex * SQUARE_SIZE + relShift, toSquare.upperIndex * SQUARE_SIZE)
            }

            Dir.LEFT -> {
                Coords(toSquare.rightIndex * SQUARE_SIZE - 1, toSquare.upperIndex * SQUARE_SIZE + relShift)
            }

            Dir.UP -> {
                Coords(toSquare.leftIndex * SQUARE_SIZE + relShift, toSquare.lowerIndex * SQUARE_SIZE - 1)
            }
        }
        return MoveInfo(newPos, newDir)
    }

    protected data class MoveToSquareInfo(val toId: Int, val newDir: Dir, val inverse: Boolean)

    protected abstract fun moveToSquare(dir: Dir): MoveToSquareInfo

    fun isInside(coords: Coords): Boolean =
        coords.x in ((leftIndex * SQUARE_SIZE) until (rightIndex * SQUARE_SIZE))
                && (coords.y in (upperIndex * SQUARE_SIZE) until (lowerIndex * SQUARE_SIZE))
}

private val squaresById =
    listOf(Square1, Square2, Square3, Square4, Square5, Square6).associateBy { square -> square.id }

private fun Coords.currentSquareId(): Int = squaresById.values
    .find { square -> square.isInside(this) }?.id ?: error("$this square is not found")

private object Square1 : Square(1, leftIndex = 2, rightIndex = 3, upperIndex = 0, lowerIndex = 1) {
    override fun moveToSquare(dir: Dir): MoveToSquareInfo = when (dir) {
        Dir.RIGHT -> MoveToSquareInfo(4, Dir.LEFT, true)
        Dir.DOWN -> MoveToSquareInfo(3, Dir.LEFT, false)
        Dir.LEFT -> error("must not be asked")
        Dir.UP -> MoveToSquareInfo(6, Dir.UP, false)
    }
}

private object Square2 : Square(2, leftIndex = 1, rightIndex = 2, upperIndex = 0, lowerIndex = 1) {
    override fun moveToSquare(dir: Dir): MoveToSquareInfo = when (dir) {
        Dir.RIGHT -> error("must not be asked")
        Dir.DOWN -> error("must not be asked")
        Dir.LEFT -> MoveToSquareInfo(5, Dir.RIGHT, true)
        Dir.UP -> MoveToSquareInfo(6, Dir.RIGHT, false)
    }
}

private object Square3 : Square(3, leftIndex = 1, rightIndex = 2, upperIndex = 1, lowerIndex = 2) {
    override fun moveToSquare(dir: Dir): MoveToSquareInfo = when (dir) {
        Dir.RIGHT -> MoveToSquareInfo(1, Dir.UP, false)
        Dir.DOWN -> error("must not be asked")
        Dir.LEFT -> MoveToSquareInfo(5, Dir.DOWN, false)
        Dir.UP -> error("must not be asked")
    }
}

private object Square4 : Square(4, leftIndex = 1, rightIndex = 2, upperIndex = 2, lowerIndex = 3) {
    override fun moveToSquare(dir: Dir): MoveToSquareInfo = when (dir) {
        Dir.RIGHT -> MoveToSquareInfo(1, Dir.LEFT, true)
        Dir.DOWN -> MoveToSquareInfo(6, Dir.LEFT, false)
        Dir.LEFT -> error("must not be asked")
        Dir.UP -> error("must not be asked")
    }
}

private object Square5 : Square(5, leftIndex = 0, rightIndex = 1, upperIndex = 2, lowerIndex = 3) {
    override fun moveToSquare(dir: Dir): MoveToSquareInfo = when (dir) {
        Dir.RIGHT -> error("must not be asked")
        Dir.DOWN -> error("must not be asked")
        Dir.LEFT -> MoveToSquareInfo(2, Dir.RIGHT, true)
        Dir.UP -> MoveToSquareInfo(3, Dir.RIGHT, false)
    }
}

private object Square6 : Square(6, leftIndex = 0, rightIndex = 1, upperIndex = 3, lowerIndex = 4) {
    override fun moveToSquare(dir: Dir): MoveToSquareInfo = when (dir) {
        Dir.RIGHT -> MoveToSquareInfo(4, Dir.UP, false)
        Dir.DOWN -> MoveToSquareInfo(1, Dir.DOWN, false)
        Dir.LEFT -> MoveToSquareInfo(2, Dir.DOWN, false)
        Dir.UP -> error("must not be asked")
    }
}

private fun Coords.teleportIfOutOfBounds(grid: Grid<Cell>, dir: Dir): Square.MoveInfo {
    if (x !in 0 until grid.width || y !in 0 until grid.height || grid[this] == Cell.EMPTY) {
        val previousCoords = this - dir.coords
        return squaresById[previousCoords.currentSquareId()]!!.move(previousCoords, dir)
    }
    return Square.MoveInfo(this, dir)
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

private fun rotateDir(curDir: Dir, rotate: Rotate): Dir {
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

@Suppress("unused")
private fun printGrid(grid: Grid<Cell>, curPos: Coords, curDir: Dir) {
    var y = 0
    var x = 0
    val asText = grid.grid.joinToString(separator = "\n") { line ->
        line.joinToString(separator = "") { cell ->
            if (y == curPos.y && x == curPos.x) {
                when (curDir) {
                    Dir.RIGHT -> ">"
                    Dir.DOWN -> "v"
                    Dir.LEFT -> "<"
                    Dir.UP -> "^"
                }.also { x++ }
            } else {
                when (cell) {
                    Cell.EMPTY -> " "
                    Cell.FREE -> "."
                    Cell.WALL -> "#"
                }.also { x++ }
            }
        }.also {
            x = 0
            y++
        }
    }
    println(asText)
    println("")
}

private fun simulate(grid: Grid<Cell>, pass: List<Instruction>): Int {
    val startX = grid.grid.first().indexOfFirst { cell -> cell != Cell.EMPTY }
    var curPos = Coords(startX, 0)
    var curDir = Dir.RIGHT
    pass.forEach { instruction ->
        when (instruction) {
            is Rotate -> {
//                println("rotate: ${instruction.name}, looking ${curDir.name}")
                curDir = rotateDir(curDir, instruction)
//                println("looking in: $curDir")
            }

            is Move -> {
//                println("move: ${instruction.dist}, looking ${curDir.name}, from $curPos")
                repeat(instruction.dist) {
                    val (nextPos, nextDir) = (curPos + curDir.coords).teleportIfOutOfBounds(grid, curDir)
//                    println("try pos: $nextPos")
                    if (grid[nextPos] == Cell.FREE) {
                        curPos = nextPos
                        curDir = nextDir
                    }
                }
//                println("newPos: $curPos")
//                printGrid(grid, curPos, curDir)
            }
        }
    }
//    println("res: $curPos, $curDir")
    return 1000 * (curPos.y + 1) + 4 * (curPos.x + 1) + dirs.indexOf(curDir)
}

fun main() {
    val (grid, pass) = readInput()
    val password = simulate(grid, pass)
    output(password)
}