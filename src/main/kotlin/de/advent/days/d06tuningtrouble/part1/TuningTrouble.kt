package de.advent.days.d06tuningtrouble.part1

import de.advent.utils.output
import de.advent.utils.setUpInput

fun main() {
    setUpInput()
    val line = readln()
    val bufferSize = 4
    for (i in line.indices) {
        val buffer = line.subSequence(i, i + bufferSize)
        if (buffer.chars().distinct().count() == bufferSize.toLong()) {
            output(i + bufferSize)
            return
        }
    }
}