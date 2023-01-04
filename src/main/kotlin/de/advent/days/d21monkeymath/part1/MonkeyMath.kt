package de.advent.days.d21monkeymath.part1

import de.advent.utils.output
import de.advent.utils.readLines

private data class Edge(val from: String, val to: String)

private sealed class Monkey(val name: String, var outEdges: MutableList<String> = mutableListOf(), var result: Long = 0)

private class ConstMonkey(name: String, val value: Long) : Monkey(name, result = value)
private class OpMonkey(name: String, val lhs: String, val rhs: String, val op: (Long, Long) -> Long) : Monkey(name)

private fun readMonkeysGraph(): Map<String, Monkey> {
    val edges = mutableListOf<Edge>()
    val monkeysByNames = readLines().map { line ->
        val words = line.split(" ")
        val monkeyName = words[0].substringBefore(":")
        if (words.size > 2) {
            val lhs = words[1]
            val rhs = words[3]
            val op: (Long, Long) -> Long = when (words[2]) {
                "+" -> Long::plus
                "-" -> Long::minus
                "*" -> Long::times
                "/" -> Long::div
                else -> error("unknown op")
            }
            edges.add(Edge(lhs, monkeyName))
            edges.add(Edge(rhs, monkeyName))
            OpMonkey(monkeyName, lhs, rhs, op)
        } else {
            val value = words[1].toLong()
            ConstMonkey(monkeyName, value)
        }
    }.associateBy { monkey -> monkey.name }
    edges.forEach { (from, to) ->
        monkeysByNames[from]!!.outEdges.add(to)
    }
    return monkeysByNames
}

private fun sortTop(monkeysByNames: Map<String, Monkey>): List<String> {
    val degrees = monkeysByNames.mapValues { (_, monkey) ->
        when (monkey) {
            is ConstMonkey -> 0
            is OpMonkey -> 2
        }
    }.toMutableMap()
    val sorted = mutableListOf<String>()
    while (true) {
        if (degrees.isEmpty()) break
        val zeros = degrees.filterValues { deg -> deg == 0 }.keys
        check(zeros.isNotEmpty()) { "unable to build top-sort" }
        zeros.forEach { monkeyName ->
            sorted.add(monkeyName)
            degrees.remove(monkeyName)
            monkeysByNames[monkeyName]!!.outEdges.forEach { toName ->
                degrees[toName] = degrees[toName]!! - 1
            }
        }
    }
    return sorted
}

fun main() {
    val monkeysByNames = readMonkeysGraph()
    val sortedMonkeys = sortTop(monkeysByNames)
    sortedMonkeys.forEach { monkeyName ->
        val monkey = monkeysByNames[monkeyName]
        if (monkey is OpMonkey) {
            monkey.result = monkey.op(monkeysByNames[monkey.lhs]!!.result, monkeysByNames[monkey.rhs]!!.result)
        }
    }
    output(monkeysByNames["root"]!!.result)
}