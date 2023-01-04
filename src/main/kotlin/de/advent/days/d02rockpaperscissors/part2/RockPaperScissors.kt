package de.advent.days.d02rockpaperscissors.part2

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

private val Weapon.losesTo get() = when(this) {
    Weapon.ROCK -> Weapon.PAPER
    Weapon.PAPER -> Weapon.SCISSORS
    Weapon.SCISSORS -> Weapon.ROCK
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

private fun score(opponentWeapon: Weapon, target: String): Int =
    when(target) {
        "X" -> LOSE_SCORE + opponentWeapon.wins.score
        "Y" -> DRAW_SCORE + opponentWeapon.score
        "Z" -> WIN_SCORE + opponentWeapon.losesTo.score
        else -> error("unknown target")
    }

fun main() {
    val res = readLines().sumOf { line ->
        val (opponentWord, target) = line.split(" ", limit=2)
        score(opponentWord.toOpponentWeapon(), target)
    }
    output(res)
}