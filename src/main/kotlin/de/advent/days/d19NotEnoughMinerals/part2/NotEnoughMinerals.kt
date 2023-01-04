package de.advent.days.d19NotEnoughMinerals.part2

import de.advent.utils.output
import de.advent.utils.readLines

private fun readBlueprints(): List<Blueprint> = readLines().map { line ->
    val regex =
        Regex("Blueprint (\\d+): Each ore robot costs (\\d+) ore. Each clay robot costs (\\d+) ore. Each obsidian robot costs (\\d+) ore and (\\d+) clay. Each geode robot costs (\\d+) ore and (\\d+) obsidian.")
    val values = regex.matchEntire(line)!!.groupValues.drop(1).map { it.toInt() }
    Blueprint(
        values[0],
        listOf(
            RobotBlueprint(Resource(ore = values[1]), Robot.ORE),
            RobotBlueprint(Resource(ore = values[2]), Robot.CLAY),
            RobotBlueprint(
                Resource(
                    ore = values[3],
                    clay = values[4]
                ), Robot.OBSIDIAN
            ),
            RobotBlueprint(
                Resource(
                    ore = values[5],
                    obsidian = values[6]
                ), Robot.GEODE
            ),
        )
    )
}

private data class Resource(val ore: Int = 0, val clay: Int = 0, val obsidian: Int = 0, val geode: Int = 0) {
    operator fun minus(other: Resource) =
        Resource(ore - other.ore, clay - other.clay, obsidian - other.obsidian, geode - other.geode)

    operator fun plus(other: Resource) =
        Resource(ore + other.ore, clay + other.clay, obsidian + other.obsidian, geode + other.geode)

    infix fun betterOrEqual(other: Resource) =
        ore >= other.ore && clay >= other.clay && obsidian >= other.obsidian && geode >= other.geode
}

private enum class Robot {
    ORE, CLAY, OBSIDIAN, GEODE
}

private data class Robots(val ore: Int = 0, val clay: Int = 0, val obsidian: Int = 0, val geode: Int = 0) {
    infix fun betterOrEqual(other: Robots) =
        ore >= other.ore && clay >= other.clay && obsidian >= other.obsidian && geode >= other.geode
}

private data class RobotBlueprint(val needed: Resource, val robotType: Robot)

private data class Blueprint(
    val id: Int,
    val robotBlueprints: List<RobotBlueprint>
)

private data class State(val resource: Resource, val robots: Robots)

private const val OLD_MINUTES = 24
private const val MINUTES = 32

private sealed class Opt(val minutesRange: IntRange) {
    fun checkCutIfTime(curState: State, minutesPassed: Int, roundsWithoutNewRobots: Int): Boolean {
        if (minutesPassed !in minutesRange) return false
        return checkCut(curState, minutesPassed, roundsWithoutNewRobots)
    }

    protected abstract fun checkCut(curState: State, minutesPassed: Int, roundsWithoutNewRobots: Int): Boolean

    abstract fun clear()
}

private class BetterStatesOpt(minutesRange: IntRange) : Opt(minutesRange) {
    private val minuteToBestStates = mutableMapOf<Int, HashSet<State>>()

    override fun checkCut(curState: State, minutesPassed: Int, roundsWithoutNewRobots: Int): Boolean {
        val allBetterExists = minuteToBestStates[minutesPassed]?.any { state ->
            (state.resource betterOrEqual curState.resource) && (state.robots betterOrEqual curState.robots)
        } ?: false
        if (allBetterExists) return true
        minuteToBestStates.getOrPut(minutesPassed) { HashSet() }.add(curState)
        return false
    }

    override fun clear() = minuteToBestStates.clear()
}

private class MaxOreResourcesOpt(minutesRange: IntRange, private val maxOre: Int) : Opt(minutesRange) {
    override fun checkCut(curState: State, minutesPassed: Int, roundsWithoutNewRobots: Int): Boolean {
        return curState.resource.ore > maxOre
    }

    override fun clear() {}
}

private class MaxClayResourcesOpt(minutesRange: IntRange, private val maxClay: Int) : Opt(minutesRange) {
    override fun checkCut(curState: State, minutesPassed: Int, roundsWithoutNewRobots: Int): Boolean {
        return curState.resource.clay > maxClay
    }

    override fun clear() {}
}

private class MaxObsResourcesOpt(minutesRange: IntRange, private val maxObs: Int) : Opt(minutesRange) {
    override fun checkCut(curState: State, minutesPassed: Int, roundsWithoutNewRobots: Int): Boolean {
        return curState.resource.obsidian > maxObs
    }

    override fun clear() {}
}

private class CraftStartRobotsOpt(minutesRange: IntRange, private val maxRoundsWithoutNewRobots: Int) :
    Opt(minutesRange) {
    override fun checkCut(curState: State, minutesPassed: Int, roundsWithoutNewRobots: Int): Boolean {
        return roundsWithoutNewRobots > maxRoundsWithoutNewRobots
    }

    override fun clear() {}
}

private class MaxRobotsOpt(
    minutesRange: IntRange,
    private val maxOreRobots: Int,
    private val maxClayRobots: Int,
    private val maxObsRobots: Int
) : Opt(minutesRange) {
    override fun checkCut(curState: State, minutesPassed: Int, roundsWithoutNewRobots: Int): Boolean {
        return curState.robots.run { ore > maxOreRobots || clay > maxClayRobots || obsidian > maxObsRobots }
    }

    override fun clear() {}
}

private val opts = listOf(
    BetterStatesOpt(0..6),
    MaxOreResourcesOpt(0..5, 5),
    MaxOreResourcesOpt(6..15, 10),
    MaxOreResourcesOpt(16..OLD_MINUTES, 15),
    MaxClayResourcesOpt(0..OLD_MINUTES, 60),
    MaxObsResourcesOpt(0..OLD_MINUTES, 25),
    MaxOreResourcesOpt((OLD_MINUTES + 1)..MINUTES, 15),
    MaxClayResourcesOpt((OLD_MINUTES + 1)..MINUTES, 60),
    MaxObsResourcesOpt((OLD_MINUTES + 1)..MINUTES, 25),
    CraftStartRobotsOpt(0..10, 4),
    CraftStartRobotsOpt(11..OLD_MINUTES, 4),
    CraftStartRobotsOpt((OLD_MINUTES + 1)..MINUTES, 5),
    MaxRobotsOpt(0..OLD_MINUTES, maxOreRobots = 6, maxClayRobots = 15, maxObsRobots = 8),
    MaxRobotsOpt((OLD_MINUTES + 1)..MINUTES, maxOreRobots = 10, maxClayRobots = 25, maxObsRobots = 15),
)

private fun cutBranch(curState: State, minutesPassed: Int, roundsWithoutNewRobots: Int): Boolean {
    return opts.any { opt -> opt.checkCutIfTime(curState, minutesPassed, roundsWithoutNewRobots) }
}

private fun List<RobotBlueprint>.forceBuyingGeodeRobot(): List<RobotBlueprint> =
    find { it.robotType == Robot.GEODE }?.let { listOf(it) } ?: this

private fun findMaxGeodes(
    blueprint: Blueprint,
    curState: State = State(
        Resource(),
        Robots(ore = 1)
    ),
    minutesPassed: Int = 0,
    roundsWithoutNewRobots: Int = 0
): Int {
    if (cutBranch(curState, minutesPassed, roundsWithoutNewRobots)) return 0
    val (curResource, robots) = curState
    if (minutesPassed == MINUTES) return curResource.geode

    val newRobotOptions = blueprint.robotBlueprints.filter { (needed, _) ->
        curResource betterOrEqual needed
    }.forceBuyingGeodeRobot()
    val newResource = curResource + curState.robots.run { Resource(ore, clay, obsidian, geode) }
    val nextSteps = newRobotOptions.map { robotBlueprint ->
        val newRobots = robots.run {
            when (robotBlueprint.robotType) {
                Robot.ORE -> Robots(ore + 1, clay, obsidian, geode)
                Robot.CLAY -> Robots(ore, clay + 1, obsidian, geode)
                Robot.OBSIDIAN -> Robots(ore, clay, obsidian + 1, geode)
                Robot.GEODE -> Robots(ore, clay, obsidian, geode + 1)
            }
        }
        findMaxGeodes(
            blueprint,
            State(newResource - robotBlueprint.needed, newRobots),
            minutesPassed + 1,
            0
        )
    }.toMutableList()
    nextSteps += listOf(
        findMaxGeodes(
            blueprint,
            State(newResource, robots),
            minutesPassed + 1,
            roundsWithoutNewRobots + 1
        )
    )
    return nextSteps.max()
}

fun main() {
    val res = readBlueprints().take(3).map { blueprint ->
        opts.forEach { it.clear() }
        val res = findMaxGeodes(blueprint)
        println("Blueprint: ${blueprint.id} /// $res")
        res
    }.reduce(Int::times)
    output(res)
}