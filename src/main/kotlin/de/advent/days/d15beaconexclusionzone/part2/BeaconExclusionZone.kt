package de.advent.days.d15beaconexclusionzone.part2

import de.advent.utils.output
import de.advent.utils.readLines
import kotlin.math.abs

private data class Coords(val x: Int, val y: Int)

private infix fun Coords.dist(other: Coords) = abs(x - other.x) + abs(y - other.y)

private data class SensorInfo(val sensor: Coords, val beacon: Coords)

private const val MAX_X = 4000000
private const val MAX_Y = 4000000

private fun readSensors(): List<SensorInfo> {
    val regex = Regex("Sensor at x=(-?\\d+), y=(-?\\d+): closest beacon is at x=(-?\\d+), y=(-?\\d+)")
    return readLines().map { line ->
        val (sensor, beacon) = regex.matchEntire(line)!!.groupValues.drop(1)
            .chunked(2)
            .map { (x, y) -> Coords(x.toInt(), y.toInt()) }
        SensorInfo(sensor, beacon)
    }
}

private fun checkLine(y: Int, sensorsInfos: List<SensorInfo>): Int? {
    val cannotExistSegments = (sensorsInfos.map { (sensor, beacon) ->
        val dBeacon = sensor dist beacon
        val dLine = abs(sensor.y - y)
        val fromX = dLine - dBeacon + sensor.x
        val toX = dBeacon - dLine + sensor.x
        fromX..toX
    }.toMutableList() + sensorsInfos.filter { (_, beacon) -> beacon.y == y }.map { (_, beacon) ->
        beacon.x..beacon.x
    }).filterNot { it.isEmpty() }
    val canExistSegments = mutableSetOf(0..MAX_X)
    cannotExistSegments.forEach { segment ->
        val start = segment.first
        val end = segment.last
        val fullSegments = canExistSegments.filter { canExistSegment ->
            canExistSegment.first >= start && canExistSegment.last <= end
        }
        canExistSegments.removeAll(fullSegments.toSet())
        val segmentsToSplit = canExistSegments.filterNot { canExistSegment ->
            canExistSegment.last < start || canExistSegment.first > end
        }
        segmentsToSplit.forEach { segmentToSplit ->
            canExistSegments.remove(segmentToSplit)
            val newSegments = if (segmentToSplit.first <= start && end <= segmentToSplit.last) {
                listOf(segmentToSplit.first until start, end + 1..segmentToSplit.last)
            } else if (segmentToSplit.first <= start) {
                check(segmentToSplit.last <= end)
                listOf(segmentToSplit.first until start)
            } else {
                check(segmentToSplit.first >= start && end <= segmentToSplit.last)
                listOf(end + 1..segmentToSplit.last)
            }
            newSegments.filterNot { it.isEmpty() }
            canExistSegments.addAll(newSegments)
        }
    }
    if (canExistSegments.isEmpty()) return null
    check(canExistSegments.size == 1)
    val res = canExistSegments.first()
    check(res.first == res.last)
    return res.first
}

fun main() {
    val sensors = readSensors()
    for (y in 0..MAX_Y) {
        val possibleBeaconPositionX = checkLine(y, sensors) ?: continue
        output(possibleBeaconPositionX * 4000000L + y)
        return
    }
}