package de.advent.days.d02rockpaperscissors.part1

import de.advent.utils.output
import de.advent.utils.readLines

private enum class Weapon(val score: Int) {
    ROCK(1), PAPER(2), SCISSORS(3)
}

private val Weapon.wins get() = when(this) {
    Weapon.ROCK -> Weapon.SCISSORS
    Weapon.PAPER -> Weapon.ROCK
    Weapon.SCISSORS -> Weapon.PAPER
}

private const val LOSE_SCORE = 0
private const val DRAW_SCORE = 3
private const val WIN_SCORE = 6

private fun String.toOpponentWeapon() = when(this) {
    "A" -> Weapon.ROCK
    "B" -> Weapon.PAPER
    "C" -> Weapon.SCISSORS
    else -> error("unknown weapon")
}

private fun String.toMyWeapon() = when(this) {
    "X" -> Weapon.ROCK
    "Y" -> Weapon.PAPER
    "Z" -> Weapon.SCISSORS
    else -> error("unknown weapon")
}

private fun score(opponentWeapon: Weapon, myWeapon: Weapon): Int {
    val matchScore = if (opponentWeapon == myWeapon) {
        DRAW_SCORE
    } else if (opponentWeapon.wins == myWeapon) {
        LOSE_SCORE
    } else {
        WIN_SCORE
    }
    return matchScore + myWeapon.score
}

fun main() {
    val res = readLines().sumOf { line ->
        val (opponentWord, myWord) = line.split(" ", limit=2)
        score(opponentWord.toOpponentWeapon(), myWord.toMyWeapon())
    }
    output(res)
}