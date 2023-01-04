package de.advent.days.d07nospaceleftondevice.part1

import de.advent.utils.output
import de.advent.utils.readLines

private sealed class Node(open val name: String) {
    abstract val size: Long
}

private data class Dir(
    override val name: String,
    val parent: Dir?,
    val children: MutableMap<String, Node> = mutableMapOf()
) : Node(name) {
    override val size: Long get() = children.values.sumOf { child -> child.size }
}

private data class File(override val name: String, override val size: Long) : Node(name)

private val root = Dir("/", null)
private val dirs = mutableListOf<Dir>(root)

private fun handleLs(outputLines: List<String>, currentDir: Dir) {
    outputLines.forEach { line ->
        if (line.startsWith("dir")) {
            val dirName = line.substringAfter("dir ")
            if (dirName !in currentDir.children) {
                val newDir = Dir(dirName, currentDir)
                currentDir.children[dirName] = newDir
                dirs.add(newDir)
            }
        } else {
            val (size, name) = line.split(" ")
            currentDir.children.putIfAbsent(name, File(name, size.toLong()))
        }
    }
}

private fun handleCd(dirName: String, currentDir: Dir): Dir {
    if (dirName == "/") {
        return root
    }
    if (dirName == "..") {
        return currentDir.parent!!
    }
    if (dirName in currentDir.children) {
        return currentDir.children[dirName] as Dir
    }
    val newDir = Dir(dirName, currentDir)
    currentDir.children[newDir.name] = newDir
    dirs.add(newDir)
    return newDir
}

private fun readCommandsBlocks(): List<List<String>> {
    val blocks = mutableListOf<MutableList<String>>()
    readLines().forEach { line ->
        if (line.startsWith("$")) {
            blocks.add(mutableListOf(line))
        } else {
            blocks.last().add(line)
        }
    }
    return blocks
}

private fun buildFileTree() {
    val commandBlocks = readCommandsBlocks()
    var currentDir = root
    commandBlocks.forEach { block ->
        val commandWords = block.first().split(" ")
        when (commandWords[1]) {
            "cd" -> {
                currentDir = handleCd(commandWords[2], currentDir)
            }

            "ls" -> handleLs(block.subList(1, block.size), currentDir)
        }
    }
}

fun main() {
    buildFileTree()
    val result = dirs.filter { it.size <= 100000 }.sumOf { it.size }
    output(result)
}