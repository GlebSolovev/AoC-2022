package de.advent.days.d25fullofhotair.part1

import de.advent.utils.output
import de.advent.utils.readLines

private const val SNAFU_BASE = 5
private const val MAX_DIGITS = 22

private val pow5: List<Long> = buildList {
    var res = 1L
    repeat(MAX_DIGITS) {
        add(res)
        res *= SNAFU_BASE
    }
}

private fun String.fromSnafu(): Long {
    var pow = 1L
    var sum = 0L
    for (dig in toList().reversed()) {
        val intDig: Long = when (dig) {
            '=' -> -2
            '-' -> -1
            else -> dig.toString().toLong()
        }
        sum += intDig * pow
        pow *= SNAFU_BASE
    }
    return sum
}

@Suppress("ReplaceRangeToWithUntil")
private fun Long.toSnafu(): String {
    val digs = mutableListOf<Int>()
    var leftLong = this
    for (power in (0 until MAX_DIGITS).reversed()) {
        val nextDig = when (2 * leftLong) {
            in (3 * pow5[power] + 1)..(5 * pow5[power] - 1) -> 2
            in (1 * pow5[power] + 1)..(3 * pow5[power] - 1) -> 1
            in ((-1) * pow5[power] + 1)..(1 * pow5[power] - 1) -> 0
            in ((-3) * pow5[power] + 1)..((-1) * pow5[power] - 1) -> -1
            in ((-5) * pow5[power] + 1)..((-3) * pow5[power] - 1) -> -2
            else -> error("unexpected range")
        }
        digs.add(nextDig)
        leftLong -= nextDig * pow5[power]
    }
    check(leftLong == 0L)
    return digs.dropWhile { it == 0 }.joinToString(separator = "") { dig ->
        when (dig) {
            -2 -> "="
            -1 -> "-"
            else -> dig.toString()
        }
    }
}

fun main() {
    val longSum = readLines().map(String::fromSnafu).sum()
    output(longSum.toSnafu())
}