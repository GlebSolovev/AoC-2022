package de.advent.days.d04campcleanup.part1

import de.advent.utils.output
import de.advent.utils.readLines

private fun IntRange.containsRange(other: IntRange) = first <= other.first && other.last <= last

fun main() {
    val regex = Regex("([0-9]+)-([0-9]+),([0-9]+)-([0-9]+)")
    val res = readLines().count { line ->
        val (rangeAFrom, rangeATo, rangeBFrom, rangeBTo) = regex.matchEntire(line)!!
            .groupValues.drop(1).map { it.toInt() }
        val rangeA = rangeAFrom..rangeATo
        val rangeB = rangeBFrom..rangeBTo
        rangeA.containsRange(rangeB) || rangeB.containsRange(rangeA)
    }
    output(res)
}