package de.advent.days.d10cathoderaytube.part1

import de.advent.utils.output
import de.advent.utils.readLines

private object SignalStrengthCheck {
    private const val START = 20
    private const val PERIOD = 40

    var next = START
        private set

    fun checkDone() {
        next += PERIOD
    }
}

private var tick = 0
private var x = 1
private var signalStrength = 0

private fun tickWithSignalStrength() {
    tick += 1
    if (SignalStrengthCheck.next == tick) {
        signalStrength += x * tick
        SignalStrengthCheck.checkDone()
    }
}

private fun performNoop() = tickWithSignalStrength()

private fun performAddx(arg: Int) {
    repeat(2) { tickWithSignalStrength() }
    x += arg
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
    output(signalStrength)
}