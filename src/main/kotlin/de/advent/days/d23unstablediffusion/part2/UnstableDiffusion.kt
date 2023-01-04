package de.advent.days.d23unstablediffusion.part2

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

private fun simulateUntilNoMoves(elvesList: List<Elf>): Int {
    val dirsIterator = CyclicDirsIterator()
    val elvesById = elvesList.associateBy { elf -> elf.id }
    var round = 1
    while (true) {
        val busyCells: Set<Coords> = HashSet(elvesById.values.map { it.coords })
        val currentDirsToCheck = dirsIterator.next().map { getNearbyDirs(it) }
        val proposedMovesToElves = buildMap<Coords, MutableList<Elf>> {
            for (elf in elvesById.values) {
                if (allNearbyDirs.all { dir -> (elf.coords + dir) !in busyCells }) {
                    continue
                }
                for (dirsToCheck in currentDirsToCheck) {
                    if (dirsToCheck.any { dir -> (elf.coords + dir) in busyCells }) continue
                    val proposedMove = elf.coords + dirsToCheck.first()
                    putIfAbsent(proposedMove, mutableListOf())
                    this[proposedMove]!!.add(elf)
                    break
                }
            }
        }
        val confirmedMoves = proposedMovesToElves
            .filterValues { candidates -> candidates.size == 1 }.mapValues { (_, candidates) -> candidates.first() }
        if (confirmedMoves.isEmpty()) return round
        confirmedMoves.forEach { (moveToCell, elf) ->
            elf.coords = moveToCell
        }
        round++
    }
}
fun main() {
    val elves = readElves()
    val stopRound = simulateUntilNoMoves(elves)
    output(stopRound)
}