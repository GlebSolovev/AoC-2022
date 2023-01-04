package de.advent.days.d03rucksackreorganization.part1

import de.advent.utils.output
import de.advent.utils.readLines

fun main() {
    val res = readLines().sumOf { line ->
        val firstHalfChars = line.substring(0, line.length / 2).toSet()
        val secondHalfChars = line.substring(line.length / 2, line.length).toSet()
        val commonChar = firstHalfChars.intersect(secondHalfChars).first()
        if(commonChar.isLowerCase()) {
            commonChar - 'a' + 1
        } else {
            commonChar - 'A' + 27
        }
    }
    output(res)
}