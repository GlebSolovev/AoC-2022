package de.advent.days.d16proboscideavolcanium.part1

import de.advent.utils.output
import de.advent.utils.readLines
import kotlin.math.max

private data class Valve(val name: String, val flow: Int, val tunnelsTo: Set<String>) {
    override fun toString() = "Valve[$name, flow = $flow]"
}

private data class DistBetween(val from: String, val to: String)

private var valvesByNames: Map<String, Valve> = emptyMap()
private var positiveValvesNames: List<String> = emptyList()
private var distBetweenValves: Map<DistBetween, Int> = emptyMap()
private var maxFlowReleased: Int = 0

private fun readValvesGraph() {
    val valveRegex = Regex("Valve ([A-Z]{2}) has flow rate=(\\d+)")
    valvesByNames = readLines().map { line ->
        val (valveWord, tunnelsWord) = line.split("; ", limit = 2)
        val (valveName, valveFlow) = valveRegex.matchEntire(valveWord)!!.groupValues.drop(1)
        val tunnelsTo = if (tunnelsWord.startsWith("tunnels")) {
            tunnelsWord.substringAfter("tunnels lead to valves ").split(", ")
        } else {
            listOf(tunnelsWord.substringAfter("tunnel leads to valve "))
        }
        Valve(valveName, valveFlow.toInt(), tunnelsTo.toSet())
    }.associateBy { valve -> valve.name }
}

private fun filterPositiveFlowValves() {
    positiveValvesNames = valvesByNames.values.filter { valve -> valve.flow > 0 }.map { valve -> valve.name }.toList()
}

// Floyd-Warshall algorithm
private fun findDistBetweenValues() {
    val valvesNames = valvesByNames.keys
    val distToRelax = buildMap {
        valvesNames.forEach { valveName ->
            valvesNames.forEach { toValveName -> put(DistBetween(valveName, toValveName), -1) }
            put(DistBetween(valveName, valveName), 0)
            valvesByNames[valveName]!!.tunnelsTo.forEach { toValveName ->
                put(DistBetween(valveName, toValveName), 1)
            }
        }
    }.toMutableMap()
    for (k in valvesNames) {
        for (i in valvesNames) {
            for (j in valvesNames) {
                val ikDist = distToRelax[DistBetween(i, k)]!!
                val kjDist = distToRelax[DistBetween(k, j)]!!
                val newDist = if (ikDist >= 0 && kjDist >= 0) {
                    ikDist + kjDist
                } else {
                    continue
                }
                val ijDist = distToRelax[DistBetween(i, j)]!!
                if (ijDist > newDist || ijDist == -1) {
                    distToRelax[DistBetween(i, j)] = newDist
                }
            }
        }
    }
    distBetweenValves = distToRelax
}

private fun walkFromOpenedValve(
    currentValveName: String,
    minutesLeft: Int,
    openedPositiveValvesNames: Set<String>,
    flowReleasedUponEnd: Int
) {
    maxFlowReleased = max(maxFlowReleased, flowReleasedUponEnd)
    for (toValveName in positiveValvesNames.minus(openedPositiveValvesNames)) {
        val newMinutesLeft = minutesLeft - distBetweenValves[DistBetween(currentValveName, toValveName)]!! - 1
        if (newMinutesLeft <= 1) {
            continue
        }
        walkFromOpenedValve(
            toValveName,
            newMinutesLeft,
            openedPositiveValvesNames + toValveName,
            flowReleasedUponEnd + newMinutesLeft * valvesByNames[toValveName]!!.flow
        )
    }
}

fun main() {
    readValvesGraph()
    filterPositiveFlowValves()
    findDistBetweenValues()
    walkFromOpenedValve("AA", 30, emptySet(), 0)
    output(maxFlowReleased)
}