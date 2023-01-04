package de.advent.days.d05supplystacks.part2

import de.advent.utils.setUpInput
import de.advent.utils.setUpOutput
import java.util.*

private const val STACKS_CNT = 9

private fun readStacks(): List<Stack<Char>> {
    val stacks = List<MutableList<Char>>(STACKS_CNT) { mutableListOf() }
    while (true) {
        var stackIndex = 0
        val line = readln()
        val parts = line.split("[", "]")
        if (parts.size == 1) { // end of stacks input
            break
        }
        for (part in parts) {
            stackIndex += if (part.isBlank()) {
                part.length / 4
            } else {
                stacks[stackIndex].add(part[0])
                1
            }
        }
    }
    readln()
    return stacks.map { list -> Stack<Char>().apply { list.reversed().forEach { push(it) } } }
}

private data class Command(val count: Int, val from: Int, val to: Int)

private fun readCommandLine(): Command? {
    val line = readlnOrNull() ?: return null
    val words = line.split(' ')
    val count = words[1].toInt()
    val from = words[3].toInt() - 1
    val to = words[5].toInt() - 1
    return Command(count, from, to)
}

fun main() {
    setUpInput()
    val stacks = readStacks()
    while (true) {
        val (count, from, to) = readCommandLine() ?: break
        val bufferStack = Stack<Char>().apply {
            repeat(count) {
                val char = stacks[from].pop()
                push(char)
            }
        }
        repeat(count) {
            stacks[to].push(bufferStack.pop())
        }
    }
    val result = stacks.joinToString(separator = "") { stack -> if (stack.empty()) "" else stack.pop().toString() }
    setUpOutput()
    print(result)
}