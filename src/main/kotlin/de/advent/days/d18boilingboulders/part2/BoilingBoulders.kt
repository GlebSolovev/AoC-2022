package de.advent.days.d18boilingboulders.part2

import de.advent.utils.output
import de.advent.utils.readLines
import java.util.LinkedList
import java.util.Queue

private data class Coords(val x: Int, val y: Int, val z: Int)

private fun Coords.getNearbyCoords(): HashSet<Coords> = listOf(
    Coords(x + 1, y, z),
    Coords(x - 1, y, z),
    Coords(x, y + 1, z),
    Coords(x, y - 1, z),
    Coords(x, y, z + 1),
    Coords(x, y, z - 1),
).toHashSet()

private fun Coords.getValidNearbyCoords(): HashSet<Coords> = getNearbyCoords().filter { (x, y, z) ->
    x in space.indices && y in space.indices && z in space.indices
}.toHashSet()

typealias Grid3D<T> = MutableList<MutableList<MutableList<T>>>

private const val SPACE_DIM = 100
private const val SHIFT = 50
private val space: Grid3D<Boolean> =
    MutableList(SPACE_DIM) {
        MutableList(SPACE_DIM) {
            MutableList(SPACE_DIM) { true }
        }
    }
private val colouring: Grid3D<Int> =
    MutableList(SPACE_DIM) { z ->
        MutableList(SPACE_DIM) { y ->
            MutableList(SPACE_DIM) { x -> -(z * SPACE_DIM * SPACE_DIM + y * SPACE_DIM + x + 1) }
        }
    }

private operator fun <T> Grid3D<T>.get(coords: Coords) = this[coords.z][coords.y][coords.x]
private operator fun <T> Grid3D<T>.set(coords: Coords, value: T) {
    this[coords.z][coords.y][coords.x] = value
}

private fun colour(start: Coords, colorId: Int) {
    val queue: Queue<Coords> = LinkedList()
    queue.add(start)
    while (queue.isNotEmpty()) {
        val node = queue.poll()
        if (!space[node]) {
            colouring[node] = 0
            continue
        }
        if (colouring[node] == colorId) continue
        colouring[node] = colorId
        node.getValidNearbyCoords().filter { nearbyNode ->
            space[nearbyNode] && colouring[nearbyNode] != colorId
        }.forEach { nextNode -> queue.add(nextNode) }
    }
}

private fun colourComponents() {
    var color = 1
    for (z in space.indices) {
        for (y in space.indices) {
            for (x in space.indices) {
                if (colouring[Coords(z, y, x)] < 0) {
                    colour(Coords(z, y, x), color++)
                }
            }
        }
    }
}

fun main() {
    val cubes = readLines().map { line ->
        val (x, y, z) = line.split(",").map { it.toInt() }
        val cube = Coords(x + SHIFT, y + SHIFT, z + SHIFT)
        space[cube] = false
        cube
    }.toHashSet()
    colourComponents()
    val outsideColor = colouring[Coords(SPACE_DIM - 1, SPACE_DIM - 1, SPACE_DIM - 1)]
    val res = cubes.sumOf { cube ->
        val nearbyCubes = cube.getNearbyCoords()
        nearbyCubes.count { nearbyCube ->
            colouring[nearbyCube] == outsideColor
        }
    }
    output(res)
}