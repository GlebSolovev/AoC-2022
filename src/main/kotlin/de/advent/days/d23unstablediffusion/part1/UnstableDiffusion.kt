package de.advent.days.d23unstablediffusion.part1

import de.advent.utils.output
import de.advent.utils.readLines

private data class Coords(val x: Int, val y: Int)
private data class Elf(val id: Int, var coords: Coords)

private operator fun Coords.plus(other: Coords): Coords {
    val newX = x + other.x
    val newY = y + other.y
    return Coords(newX, newY)
}

private val dirs: List<Coords> = listOf(
    Coords(0, -1),
    Coords(0, 1),
    Coords(-1, 0),
    Coords(1, 0)
)

private val allNearbyDirs: List<Coords> = dirs + listOf(
    Coords(-1, -1),
    Coords(-1, 1),
    Coords(1, -1),
    Coords(1, 1)
)

private class CyclicDirsIterator : Iterator<List<Coords>> {
    private var curIndex = 0
    override fun hasNext() = true

    override fun next(): List<Coords> {
        val toReturn = dirs.subList(curIndex, dirs.size) + dirs.subList(0, curIndex)
        curIndex++
        if (curIndex >= dirs.size) curIndex = 0
        return toReturn
    }
}

// can be optimized
private fun getNearbyDirs(dir: Coords): List<Coords> = if (dir.x == 0) {
    listOf(dir, Coords(1, dir.y), Coords(-1, dir.y))
} else {
    check(dir.y == 0)
    listOf(dir, Coords(dir.x, 1), Coords(dir.x, -1))
}

private fun readElves() = buildList {
    var id = 0
    readLines().forEachIndexed { y, line ->
        line.forEachIndexed { x, ch ->
            when (ch) {
                '.' -> {}
                '#' -> add(Elf(id++, Coords(x, y)))
                else -> error("unknown symbol")
            }
        }
    }
}

//private fun printElvesGrid(elvesById: Map<Int, Elf>) {
//    val grid = MutableList(12) { MutableList(14) { false } }
////    val grid = MutableList(8) { MutableList(8) { false } }
//    elvesById.values.forEach { elf -> grid[elf.coords.y][elf.coords.x] = true }
//    val img = grid.joinToString(separator = "\n") { row -> row.joinToString(separator = "") { if (it) "#" else "." } }
//    println(img)
//}

@Suppress("SameParameterValue")
private fun simulate(rounds: Int, elvesList: List<Elf>): Map<Int, Elf> {
    val dirsIterator = CyclicDirsIterator()
    val elvesById = elvesList.associateBy { elf -> elf.id }
//    printElvesGrid(elvesById)
    repeat(rounds) { round ->
        val busyCells: Set<Coords> = HashSet(elvesById.values.map { it.coords })
        val currentDirsToCheck = dirsIterator.next().map { getNearbyDirs(it) }
//        println("dirs: $currentDirsToCheck")
        val proposedMovesToElves = buildMap<Coords, MutableList<Elf>> {
            for (elf in elvesById.values) {
//                println("\nElf $elf")
//                println("busy cells: $busyCells")
//                println("all nearby dirs: $allNearbyDirs")
                if (allNearbyDirs.all { dir -> (elf.coords + dir) !in busyCells }) {
//                    println("all")
                    continue
                }
                for (dirsToCheck in currentDirsToCheck) {
                    if (dirsToCheck.any { dir -> (elf.coords + dir) in busyCells }) continue
                    val proposedMove = elf.coords + dirsToCheck.first()
//                    println("proposed a move: $proposedMove // ${dirsToCheck.first()}")
                    putIfAbsent(proposedMove, mutableListOf())
                    this[proposedMove]!!.add(elf)
                    break
                }
            }
        }
        proposedMovesToElves
            .filterValues { candidates -> candidates.size == 1 }
            .forEach { (moveToCell, elves) ->
                elves.first().coords = moveToCell
            }
//        println("\nEnd of round $round")
//        printElvesGrid(elvesById)
    }
    return elvesById
}

private fun countEmptyInElvesRect(elvesById: Map<Int, Elf>): Int {
    val elvesCells = elvesById.values.map { it.coords }
    val elvesX = elvesCells.map { it.x }
    val elvesY = elvesCells.map { it.y }
    val rectSquare = (elvesX.max() - elvesX.min() + 1) * (elvesY.max() - elvesY.min() + 1)
    return rectSquare - elvesCells.distinct().size
}

fun main() {
    val elves = readElves()
    val simulatedElvesById = simulate(10, elves)
    output(countEmptyInElvesRect(simulatedElvesById))
}