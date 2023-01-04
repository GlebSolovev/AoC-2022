package de.advent.days.d18boilingboulders.part1

import de.advent.utils.output
import de.advent.utils.readLines

private data class Coords(val x: Int, val y: Int, val z: Int)

private fun Coords.getNearbyCoords(): HashSet<Coords> = listOf(
    Coords(x + 1, y, z),
    Coords(x - 1, y, z),
    Coords(x, y + 1, z),
    Coords(x, y - 1, z),
    Coords(x, y, z + 1),
    Coords(x, y, z - 1),
).toHashSet()

fun main() {
    val cubes = readLines().map{line ->
        val (x, y, z) = line.split(",").map { it.toInt() }
        Coords(x, y, z)
    }.toHashSet()
    val res = cubes.sumOf { cube ->
        val nearbyCubes = cube.getNearbyCoords()
        nearbyCubes.minus(cubes).size
    }
    output(res)
}