package de.advent.days.d11monkeyinthemiddle.part1

import de.advent.utils.*
import java.util.LinkedList
import java.util.Queue
import kotlin.io.path.Path
import kotlin.io.path.readText

private val monkeys = mutableListOf<Monkey>()

private data class Monkey(
    val id: Int,
    private val items: Queue<Int>,
    private val operation: (Int) -> Int,
    private val test: (Int) -> Boolean,
    private val throwToTrueId: Int,
    private val throwToFalseId: Int,
) {
    var inspectedCount: Int = 0
        private set

    fun simulateRound() {
        while (items.isNotEmpty()) {
            val inspectedItem = items.poll()!!
            val newInspectedItem = operation(inspectedItem) / 3
            val target = if (test(newInspectedItem)) throwToTrueId else throwToFalseId
            monkeys[target].items.add(newInspectedItem)
            inspectedCount++
        }
    }
}

private fun readMonkey(monkeyId: Int) {
    val startingItems = LinkedList(
        readln()
            .substringAfter("  Starting items: ")
            .split(", ")
            .map { it.toInt() }
    )
    val operationWords = readln().substringAfter("  Operation: new = ").words(limit = 3)
    val operation = { old: Int ->
        val lhs = if (operationWords[0] == "old") old else operationWords[0].toInt()
        val rhs = if (operationWords[2] == "old") old else operationWords[2].toInt()
        when (operationWords[1]) {
            "+" -> lhs + rhs
            "*" -> lhs * rhs
            else -> error("unknown operation")
        }
    }
    val testDivBy = readln().substringAfter("  Test: divisible by ").toInt()
    val test = { item: Int -> item % testDivBy == 0 }
    val throwToTrueId = readln().substringAfter("    If true: throw to monkey ").toInt()
    val throwToFalseId = readln().substringAfter("    If false: throw to monkey ").toInt()
    val monkey = Monkey(monkeyId, startingItems, operation, test, throwToTrueId, throwToFalseId)
    monkeys.add(monkey)
    check(monkeys.indices.last() == monkeyId)
}

private fun simulateRound() {
    monkeys.forEach { monkey ->
        monkey.simulateRound()
    }
}

private const val ROUNDS = 20

private fun countMonkeyBusiness() = monkeys.map { it.inspectedCount }
    .sortedByDescending { it }
    .take(2)
    .reduce { l, r -> l * r }

fun main() {
    setUpInput()
    while (true) {
        val line = readlnOrNull() ?: break
        if (line.startsWith("Monkey")) {
            readMonkey(line.words()[1].substringBefore(":").toInt())
        }
    }
    repeat(ROUNDS) {
        simulateRound()
    }
    output(countMonkeyBusiness())
}