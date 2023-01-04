package de.advent.days.d11monkeyinthemiddle.part2

import de.advent.utils.*
import java.math.BigInteger
import java.util.LinkedList
import java.util.Queue

private val monkeys = mutableListOf<Monkey>()

private var worryStressModulo = 0L
private fun keepWorryLevel(item: Long): Long = item % worryStressModulo

private data class Monkey(
    val id: Int,
    private val items: Queue<Long>,
    private val operation: (Long) -> Long,
    val testDivBy: Long,
    private val throwToTrueId: Int,
    private val throwToFalseId: Int,
) {
    var inspectedCount: BigInteger = BigInteger.ZERO
        private set

    fun simulateRound() {
        while (items.isNotEmpty()) {
            val inspectedItem = items.poll()!!
            val newInspectedItem = keepWorryLevel(operation(inspectedItem))
            val target = if (newInspectedItem % testDivBy == 0L) throwToTrueId else throwToFalseId
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
            .map { it.toLong() }
    )
    val operationWords = readln().substringAfter("  Operation: new = ").words(limit = 3)
    val operation = { old: Long ->
        val lhs = if (operationWords[0] == "old") old else operationWords[0].toLong()
        val rhs = if (operationWords[2] == "old") old else operationWords[2].toLong()
        when (operationWords[1]) {
            "+" -> lhs + rhs
            "*" -> lhs * rhs
            else -> error("unknown operation")
        }
    }
    val testDivBy = readln().substringAfter("  Test: divisible by ").toLong()
    val throwToTrueId = readln().substringAfter("    If true: throw to monkey ").toInt()
    val throwToFalseId = readln().substringAfter("    If false: throw to monkey ").toInt()
    val monkey = Monkey(monkeyId, startingItems, operation, testDivBy, throwToTrueId, throwToFalseId)
    monkeys.add(monkey)
    check(monkeys.indices.last() == monkeyId)
}

private fun simulateRound() {
    monkeys.forEach { monkey ->
        monkey.simulateRound()
    }
}

private const val ROUNDS = 10000

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
    monkeys.forEach { println(it) }
    // all testDivBy are prime numbers => just multiply them to find LCD
    worryStressModulo = monkeys.map { it.testDivBy }.reduce { l, r -> l * r }
    println(worryStressModulo)
    repeat(ROUNDS) {
        simulateRound()
    }
    monkeys.forEach { println("INSPECTED: ${it.inspectedCount}") }
    output(countMonkeyBusiness())
}