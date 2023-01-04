package de.advent.utils

import java.io.PrintStream
import kotlin.io.path.*

const val INPUT_FILE = "input.txt"
const val OUTPUT_FILE = "output.txt"

fun setUpInput() = System.setIn(Path(INPUT_FILE).inputStream())
fun setUpOutput(outputFile: String = OUTPUT_FILE) = System.setOut(PrintStream(Path(outputFile).outputStream()))

fun readLines(): List<String> = Path(INPUT_FILE).readLines()
fun clear(file: String) = Path(file).writeText("")

fun output(result: Any) {
    System.setOut(PrintStream(Path(OUTPUT_FILE).outputStream()))
    println(result)
    System.setOut(System.out)
}

fun String.words(limit: Int = 0): List<String> = split(" ", limit = limit)
