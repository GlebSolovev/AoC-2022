package de.advent.days.d09ropebridge.part1

import de.advent.utils.output
import de.advent.utils.setUpInput
import kotlin.math.abs

private data class Pos(val h: Int, val w: Int)

private fun String.dirToPos() = when (this) {
    "R" -> Pos(0, 1)
    "L" -> Pos(0, -1)
    "U" -> Pos(1, 0)
    "D" -> Pos(-1, 0)
    else -> error("unknown dir")
}

private operator fun Pos.plus(other: Pos): Pos = Pos(h + other.h, w + other.w)
private operator fun Pos.minus(other: Pos): Pos = Pos(h - other.h, w - other.w)
private fun Int.sign(): Int = if (this == 0) 0 else this / abs(this)
private fun Pos.truncateToOne(): Pos = Pos(h.sign(), w.sign())

private val visitedByTail = mutableSetOf(Pos(0, 0))

private const val ELEMENTS = 2
private val elements = MutableList(ELEMENTS) { Pos(0, 0) }

private fun catchUp(elementIndex: Int) {
    val parentPos = elements[elementIndex - 1]
    val selfPos = elements[elementIndex]
    val shift = parentPos - selfPos

    val wShiftValue = abs(shift.w)
    val hShiftValue = abs(shift.h)
    val shiftValue = wShiftValue + hShiftValue

    if (shiftValue <= 1) return
    if (wShiftValue == 1 && hShiftValue == 1) return

    val newPos = selfPos + shift.truncateToOne()
    elements[elementIndex] = newPos
}

private fun makeSingleStep(dir: String) {
    elements[0] = elements[0] + dir.dirToPos()
    for (i in 1 until elements.size) {
        catchUp(i)
        if (i == elements.size - 1) {
            visitedByTail.add(elements.last())
        }
    }
}

private fun makeStep(dir: String, step: Int) {
    repeat(step) {
        makeSingleStep(dir)
    }
}

fun main() {
    setUpInput()
    while (true) {
        val (dir, step) = readlnOrNull()?.split(" ", limit = 2) ?: break
        makeStep(dir, step.toInt())
    }
    val visitedCount = visitedByTail.count()
    output(visitedCount)
}