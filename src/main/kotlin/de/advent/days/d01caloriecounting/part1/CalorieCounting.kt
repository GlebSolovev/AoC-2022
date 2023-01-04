package de.advent.days.d01caloriecounting.part1

import de.advent.utils.output
import de.advent.utils.setUpInput

fun main() {
    setUpInput()
    val elvesCalories = buildList {
        var curElfCalories = 0
        while (true) {
            val line = readlnOrNull() ?: break
            if(line.isBlank()) {
                add(curElfCalories)
                curElfCalories = 0
            } else {
                curElfCalories += line.toInt()
            }
        }
        if(curElfCalories != 0) add(curElfCalories)
    }
    val res = elvesCalories.max()
    output(res)
}