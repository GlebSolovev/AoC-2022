package de.advent.days.d12hillclimbing.part2

import de.advent.utils.output
import de.advent.utils.readLines
import java.util.LinkedList
import java.util.Queue
import kotlin.streams.toList

private class Node(val height: Int, val adjReverseTo: MutableList<Node> = mutableListOf())

private var startNodes = mutableListOf<Node>()
private var target: Node = Node('z' - 'a')

private val nodes = mutableListOf<MutableList<Node>>()

private fun readHeightsGraph() {
    val grid = mutableListOf<List<Int>>()

    readLines().forEach { line ->
        grid.add(line.chars().toList())
    }
    grid.forEachIndexed { i, row ->
        nodes.add(mutableListOf())
        row.forEach { ch ->
            val newNode = when (ch.toChar()) {
                'S', 'a' -> {
                    startNodes.add(Node(0))
                    startNodes.last()
                }

                'E' -> target
                else -> Node(ch.toChar() - 'a')
            }
            nodes[i].add(newNode)
        }
    }
    fun Node.addAdj(i: Int, j: Int) {
        if (i in nodes.indices && j in nodes.first().indices) {
            val other = nodes[i][j]
            if (height - other.height <= 1) {
                adjReverseTo.add(nodes[i][j])
            }
        }
    }
    nodes.forEachIndexed { i, row ->
        row.forEachIndexed { j, node ->
            node.addAdj(i + 1, j)
            node.addAdj(i - 1, j)
            node.addAdj(i, j + 1)
            node.addAdj(i, j - 1)
        }
    }
}

private fun bfs(node: Node): Map<Node, Node> {
    val parents = mutableMapOf<Node, Node>()
    val queue: Queue<Node> = LinkedList()
    queue.add(node)
    while (queue.isNotEmpty()) {
        val curNode = queue.poll()
        curNode.adjReverseTo.forEach { neigh ->
            if (neigh !in parents) {
                parents[neigh] = curNode
                queue.add(neigh)
            }
        }
    }
    return parents
}

private fun calcDist(bfsParents: Map<Node, Node>, from: Node, to: Node): Int {
    var curNode = to
    var dist = 0
    while (true) {
        if (curNode == from) return dist
        if (curNode !in bfsParents) return Int.MAX_VALUE
        curNode = bfsParents[curNode]!!
        dist++
    }
}

@Suppress("unused")
private fun printBfsParents(bfsParents: Map<Node, Node>) {
    val printBfs = nodes.joinToString(separator = "\n") { row ->
        row.joinToString(separator = " ") { bfsParents[it]?.height?.toString() ?: "K" }
    }
    println(printBfs)
}

fun main() {
    readHeightsGraph()
    val bfsReversedParents = bfs(target)
    val res = startNodes.minOf { start -> calcDist(bfsReversedParents, target, start) }
    output(res)
}