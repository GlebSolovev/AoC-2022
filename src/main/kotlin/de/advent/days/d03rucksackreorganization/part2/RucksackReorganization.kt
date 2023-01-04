package de.advent.days.d03rucksackreorganization.part2

import de.advent.utils.output
import de.advent.utils.readLines

fun main() {
    val res = readLines().chunked(3).sumOf { lines ->
        val commonChar = lines.map { it.toSet() }.reduce(Iterable<Char>::intersect).first()
        if (commonChar.isLowerCase()) {
            commonChar - 'a' + 1
        } else {
            commonChar - 'A' + 27
        }
    }
    output(res)
}