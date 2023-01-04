package de.advent.days.d21monkeymath.part2

import de.advent.utils.output
import de.advent.utils.readLines

private data class Edge(val from: String, val to: String)

private sealed class Monkey(
    val name: String,
    var outEdges: MutableList<String> = mutableListOf(),
    var result: Long? = null
)

private class ConstMonkey(name: String, val value: Long) : Monkey(name, result = value)
private sealed class LhsRhsMonkey(name: String, val lhs: String, val rhs: String) : Monkey(name)
private class OpMonkey(
    name: String,
    lhs: String,
    rhs: String,
    val op: (Long, Long) -> Long,
    val opText: String,
    val findRhsOp: (Long, Long) -> Long,
    val findLhsOp: (Long, Long) -> Long
) : LhsRhsMonkey(name, lhs, rhs)

private class HumanMonkey : Monkey("humn")
private class RootMonkey(lhs: String, rhs: String) : LhsRhsMonkey("root", lhs, rhs)

private fun LhsRhsMonkey.lhsResult(monkeysByNames: Map<String, Monkey>): Long? = monkeysByNames[lhs]!!.result
private fun LhsRhsMonkey.rhsResult(monkeysByNames: Map<String, Monkey>): Long? = monkeysByNames[rhs]!!.result

private data class GetNotNullOperandResult(val result: Long, val isLeftOperand: Boolean)

private fun LhsRhsMonkey.getNotNullOperandResult(monkeysByNames: Map<String, Monkey>): GetNotNullOperandResult {
    val lhsRes = lhsResult(monkeysByNames)
    val rhsRes = rhsResult(monkeysByNames)
    return if (lhsRes != null) {
        GetNotNullOperandResult(lhsRes, true)
    } else if (rhsRes != null) {
        GetNotNullOperandResult(rhsRes, false)
    } else {
        error("both operands are null-s")
    }
}

private fun LhsRhsMonkey.getOtherOperand(notNullIsLeft: Boolean): String = if (notNullIsLeft) {
    rhs
} else {
    lhs
}

private fun readMonkeysGraph(): Map<String, Monkey> {
    val edges = mutableListOf<Edge>()
    val monkeysByNames = readLines().map { line ->
        val words = line.split(" ")
        val monkeyName = words[0].substringBefore(":")
        if (words.size > 2) {
            val lhs = words[1]
            val rhs = words[3]
            edges.add(Edge(lhs, monkeyName))
            edges.add(Edge(rhs, monkeyName))
            if (monkeyName == "root") {
                RootMonkey(lhs, rhs)
            } else {
                when (words[2]) {
                    "+" -> OpMonkey(monkeyName, lhs, rhs, Long::plus, "+", Long::minus, Long::minus)
                    "-" -> OpMonkey(monkeyName, lhs, rhs, Long::minus, "-", { s, l -> l - s }, Long::plus)
                    "*" -> OpMonkey(monkeyName, lhs, rhs, Long::times, "*", Long::div, Long::div)
                    "/" -> OpMonkey(
                        monkeyName,
                        lhs,
                        rhs,
                        Long::div,
                        "/",
                        { s, l -> l / s }) { s, r -> s * r } // improve
                    else -> error("unknown op")
                }
            }
        } else {
            if (monkeyName == "humn") {
                HumanMonkey()
            } else {
                val value = words[1].toLong()
                ConstMonkey(monkeyName, value)
            }
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
            is ConstMonkey, is HumanMonkey -> 0
            is LhsRhsMonkey -> 2
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

private fun printAsExpr(monkeyName: String, monkeysByNames: Map<String, Monkey>): String {
    return when (val monkey = monkeysByNames[monkeyName]!!) {
        is LhsRhsMonkey -> {
            if (monkey.result != null) {
                monkey.result.toString()
            } else {
                val opText = when (monkey) {
                    is RootMonkey -> "="
                    is OpMonkey -> monkey.opText
                }
                "(${printAsExpr(monkey.lhs, monkeysByNames)} $opText ${printAsExpr(monkey.rhs, monkeysByNames)})"
            }
        }

        is HumanMonkey -> "x"
        is ConstMonkey -> monkey.value.toString()
    }
}

private fun rollbackToFindHumanEquation(monkeysByNames: Map<String, Monkey>): String {
    val root = monkeysByNames["root"]!! as RootMonkey
    if (root.lhsResult(monkeysByNames) == null && root.rhsResult(monkeysByNames) == null) {
        return printAsExpr(root.name, monkeysByNames)
    }
    var (curValue, isLeftOperand) = root.getNotNullOperandResult(monkeysByNames)
    var curMonkeyName = root.getOtherOperand(isLeftOperand)
    while (true) {
        val curMonkey = monkeysByNames[curMonkeyName]!!
        if (curMonkey is HumanMonkey) {
            curMonkey.result = curValue
            return curValue.toString()
        }
        if (curMonkey is OpMonkey) {
            if (curMonkey.lhsResult(monkeysByNames) == null && curMonkey.rhsResult(monkeysByNames) == null) {
                val unknownExpr = printAsExpr(curMonkey.name, monkeysByNames)
                return "$curValue = $unknownExpr"
            }
            val (nextNotNullOperandResult, nextIsLeftOperand) = curMonkey.getNotNullOperandResult(
                monkeysByNames
            )
            curValue = if (nextIsLeftOperand) {
                curMonkey.findRhsOp(curValue, nextNotNullOperandResult)
            } else {
                curMonkey.findLhsOp(curValue, nextNotNullOperandResult)
            }
            curMonkeyName = curMonkey.getOtherOperand(nextIsLeftOperand)
            continue
        }
        error("reached unexpected monkey instead of human / op")
    }
}

fun main() {
    val monkeysByNames = readMonkeysGraph()
    val sortedMonkeys = sortTop(monkeysByNames)
    for (monkeyName in sortedMonkeys) {
        val monkey = monkeysByNames[monkeyName]
        if (monkey is OpMonkey) {
            val lhsRes = monkey.lhsResult(monkeysByNames) ?: continue
            val rhsRes = monkey.rhsResult(monkeysByNames) ?: continue
            monkey.result = monkey.op(lhsRes, rhsRes)
        }
    }
    output(rollbackToFindHumanEquation(monkeysByNames))
}