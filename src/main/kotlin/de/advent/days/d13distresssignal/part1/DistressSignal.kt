package de.advent.days.d13distresssignal.part1

import de.advent.utils.output
import de.advent.utils.readLines

private sealed class Elem

private data class Value(val value: Int) : Elem()

private data class Li(val elems: MutableList<Elem>) : Elem() {
    val size = elems.size
}

private fun parseLineToElem(line: String): Elem {
    fun String.separateBrackets(): List<String> = buildList {
        val value = mutableListOf<Char>()
        fun resetValueIfNotEmpty() {
            if (value.isNotEmpty()) {
                add(value.joinToString(separator = ""))
                value.clear()
            }
        }
        for (chInt in chars()) {
            when (val ch = chInt.toChar()) {
                '[' -> {
                    resetValueIfNotEmpty()
                    add("[")
                }

                ']' -> {
                    resetValueIfNotEmpty()
                    add("]")
                }

                else -> value.add(ch)
            }
        }
        resetValueIfNotEmpty()
    }

    val stringElems = line.split(",").flatMap { it.separateBrackets() }.toMutableList()
    stringElems.add("]")
    var index = -1
    fun parseList(): Elem {
        val elems = mutableListOf<Elem>()
        while (true) {
            index++
            val el = when (val stringEl = stringElems[index]) {
                "[" -> {
                    parseList()
                }

                "]" -> return Li(elems)
                else -> {
                    Value(stringEl.toInt())
                }
            }
            elems.add(el)
        }
    }
    return parseList()
}

private operator fun Elem.compareTo(other: Elem): Int {
    when (this) {
        is Value -> {
            return when (other) {
                is Value -> value.compareTo(other.value)
                is Li -> Li(mutableListOf(this)).compareTo(other)
            }
        }

        is Li -> {
            return when (other) {
                is Value -> compareTo(Li(mutableListOf(other)))
                is Li -> {
                    val zipped = elems.zip(other.elems)
                    for ((l, r) in zipped) {
                        val res = l.compareTo(r)
                        if (res != 0) return res
                    }
                    size.compareTo(other.size)
                }
            }
        }
    }
}

private fun checkPair(leftLine: String, rightLine: String): Boolean {
    val left = parseLineToElem(leftLine)
    val right = parseLineToElem(rightLine)
    return left < right
}

fun main() {
    val result = readLines()
        .asSequence()
        .filter { it.isNotEmpty() }
        .chunked(2)
        .mapIndexed { index, pair -> Pair(index, pair) }
        .filter { (_, pair) ->
            val (l, r) = pair
            checkPair(l, r)
        }.sumOf { (index, _) -> index + 1 }
    output(result)
}