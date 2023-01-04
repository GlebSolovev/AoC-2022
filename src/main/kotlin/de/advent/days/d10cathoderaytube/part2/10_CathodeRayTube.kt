package de.advent.days.d10cathoderaytube.part2

import de.advent.utils.output
import de.advent.utils.readLines

private var tick = 0
private var x = 1

private const val ROWS = 6
private const val COLS = 40
private var crt = MutableList(ROWS) { MutableList(COLS) { false } }
private var row = 0
private var col = 0

private fun drawTick() {
    if (col in (x - 1)..(x + 1)) {
        crt[row][col] = true
    }
    col += 1
    if (col == COLS) {
        row += 1
        col = 0
    }
}

private fun performNoop() = drawTick()

private fun performAddx(arg: Int) {
    repeat(2) { drawTick() }
    x += arg
}

private fun renderCrt(): String =
    crt.joinToString(separator = "\n") { row ->
        row.joinToString(separator = "") { lit -> if (lit) "#" else "." }
    }

fun main() {
    readLines().forEach { line ->
        if (line == "noop") {
            performNoop()
        } else {
            val addxArg = line.split(" ", limit = 2)[1].toInt()
            performAddx(addxArg)
        }
    }
    val renderedCrt = renderCrt()
    output(renderedCrt)
}

private var signalStrength = 0

@Suppress("unused")
private fun tickWithSignalStrength() {
    tick += 1
    if (SignalStrengthCheck.next == tick) {
        signalStrength += x * tick
        SignalStrengthCheck.checkDone()
    }
}

private object SignalStrengthCheck {
    private const val START = 20
    private const val PERIOD = 40

    var next = START
        private set

    fun checkDone() {
        next += PERIOD
    }
}