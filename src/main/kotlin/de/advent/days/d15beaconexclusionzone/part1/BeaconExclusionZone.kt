package de.advent.days.d15beaconexclusionzone.part1

import de.advent.utils.output
import de.advent.utils.readLines
import kotlin.math.abs

private data class Coords(val x: Int, val y: Int)

private infix fun Coords.dist(other: Coords) = abs(x - other.x) + abs(y - other.y)

private const val SHIFT = 2_000_000
private const val MAX_X = 5_000_000 + SHIFT
private const val TARGET_LINE_Y = 2000000 + SHIFT
private val beaconCanExistTargetLine = MutableList(MAX_X) { true }

private val beacons = mutableListOf<Coords>()

private fun markBeaconsCannotExist(sensor: Coords, beacon: Coords) {
    val dBeacon = sensor dist beacon
    val dLine = abs(sensor.y - TARGET_LINE_Y)
    val fromX = dLine - dBeacon + sensor.x
    val toX = dBeacon - dLine + sensor.x
    for (x in fromX..toX) {
        beaconCanExistTargetLine[x] = false
    }
    beacons.add(beacon)
}

fun main() {
    val regex = Regex("Sensor at x=(-?\\d+), y=(-?\\d+): closest beacon is at x=(-?\\d+), y=(-?\\d+)")
    readLines().forEach { line ->
        val (sensor, beacon) = regex.matchEntire(line)!!.groupValues.drop(1)
            .chunked(2)
            .map { (x, y) -> Coords(x.toInt() + SHIFT, y.toInt() + SHIFT) }
        markBeaconsCannotExist(sensor, beacon)
    }
    beacons.forEach { (x, y) -> if (y == TARGET_LINE_Y) beaconCanExistTargetLine[x] = true }
    output(beaconCanExistTargetLine.count { !it })
}