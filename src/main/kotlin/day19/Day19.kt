package day19

import utils.readInput
import utils.stack.Stack
import utils.stack.pop
import utils.stack.push
import kotlin.math.max
import kotlin.streams.asStream
import kotlin.streams.toList
import kotlin.system.measureTimeMillis

fun main() {
    val elapsed = measureTimeMillis {
        val input = readInput("Day19")
        println(part1(input))
        println(part2(input))
    }
    println()
    println("Elapsed time: $elapsed ms.")
}

fun part1(input: List<String>): Int {
    return parse(input).stream()
        .parallel()
        .mapToInt { it.id * calculateMaximumGeodes(it, 24) }
        .sum()
}

fun part2(input: List<String>): Int {
    return parse(input).asSequence()
        .take(3)
        .asStream()
        .parallel()
        .mapToInt { calculateMaximumGeodes(it, 32) }
        .toList()
        .reduce { x,y -> x * y }
}

fun parse(input: List<String>): List<Blueprint> {
    val parseRegex = """Blueprint (\d+): Each ore robot costs (\d+) ore. Each clay robot costs (\d+) ore. Each obsidian robot costs (\d+) ore and (\d+) clay. Each geode robot costs (\d+) ore and (\d+) obsidian.""".toRegex()
    return input
        .map { parseRegex.matchEntire(it)!!.groupValues.drop(1) }
        .map { it.map(String::toInt) }
        .map { ints ->
            val id = ints.first()
            val oreRobotCost = Ore(ints[1])
            val clayRobotCost = Ore(ints[2])
            val obsidianRobotCost = Pair(Ore(ints[3]), Clay(ints[4]))
            val geodeRobotCost = Pair(Ore(ints[5]), Obsidian(ints[6]))
            Blueprint(id, oreRobotCost, clayRobotCost, obsidianRobotCost, geodeRobotCost)
        }
}

fun calculateMaximumGeodes(blueprint: Blueprint, minutesAllowed: Int): Int {

    var maxGeodesSoFar = 0
    val stack = Stack<Progress>()
    stack.push(Progress.starting(minutesAllowed))
    val statesSeenBefore = mutableSetOf<Progress>()

    fun <M : Material<M>> max(vararg costs: Amount<M>) : Int {
        var result = 0
        costs.map { it.value }.forEach { result = max(result, it) }
        return result
    }

    //there's no point having more ore robots on hand then we can spend in one turn, as we can only build one robot at a time
    val maxOreRobotsNeeded = max(
        blueprint.oreRobotCost,
        blueprint.clayRobotCost,
        blueprint.obsidianRobotCost.first,
        blueprint.geodeRobotCost.first
    )

    val maxClayRobotsNeeded = blueprint.obsidianRobotCost.second.value //we can only spend clay on obsidian robots
    val maxObsidianRobotsNeeded = blueprint.geodeRobotCost.second.value //we can only spend obsidian on geode robots

    fun enqueue(progress: Progress) {
        if (progress in statesSeenBefore) {
            return
        }
        statesSeenBefore += progress
        stack.push(progress)
    }

    while (stack.isNotEmpty()) {
        val state = stack.pop()

        val geodesThisConfigurationWillProduceWithNoAdditionalRobots = state.geodes.value + (state.geodeRobots * state.minutesRemaining)
        maxGeodesSoFar = max(geodesThisConfigurationWillProduceWithNoAdditionalRobots, maxGeodesSoFar)

        if (state.minutesRemaining <= 0) {
            continue
        }

        if (state.heuristic < maxGeodesSoFar) {
            continue
        }

        /*
           Decide which robot to build next, and wait until we can afford to build it
         */

        // ore robot (if we can benefit from another one)
        if (state.oreRobots < maxOreRobotsNeeded) {
            var buildState = state
            while (buildState.ore < blueprint.oreRobotCost) {
                buildState = buildState.gatherResources()
            }
            if (buildState.minutesRemaining >= 2) {
                //it takes one minute to build the robot and another for it to produce anything
                val next = buildState
                    .copy(ore = buildState.ore - blueprint.oreRobotCost)
                    .gatherResources()
                    .copy(oreRobots = buildState.oreRobots + 1)
                enqueue(next)
            }
        }

        // clay robot (if we can benefit from another one)
        if (state.clayRobots < maxClayRobotsNeeded) {
            var buildState = state
            while (buildState.ore < blueprint.clayRobotCost) {
                buildState = buildState.gatherResources()
            }

            if (buildState.minutesRemaining >= 2) {
                //it takes one minute to build the robot and another for it to produce anything
                val next = buildState
                    .copy(ore = buildState.ore - blueprint.clayRobotCost)
                    .gatherResources()
                    .copy(clayRobots = buildState.clayRobots + 1)
                enqueue(next)
            }
        }

        // obsidian robot (if we can benefit from another one and have the robots we need to build it)
        if (state.obsidianRobots < maxObsidianRobotsNeeded && state.clayRobots > 0) {
            val (oreCost, clayCost) = blueprint.obsidianRobotCost

            var buildState = state
            while (buildState.ore < oreCost || buildState.clay < clayCost) {
                buildState = buildState.gatherResources()
            }

            if (buildState.minutesRemaining >= 2) {
                //it takes one minute to build the robot and another for it to produce anything
                val next = buildState
                    .copy(ore = buildState.ore - oreCost, clay = buildState.clay - clayCost)
                    .gatherResources()
                    .copy(obsidianRobots = buildState.obsidianRobots + 1)
                enqueue(next)
            }
        }

        // try and build a geode robot if we can produce the materials (there's no limit on useful geode robots)
        if (state.obsidianRobots > 0) {
            var buildState = state
            val (oreCost, obsidianCost) = blueprint.geodeRobotCost
            while (buildState.ore < oreCost || buildState.obsidian < obsidianCost) {
                buildState = buildState.gatherResources()
            }

            if (buildState.minutesRemaining >= 2) {
                //it takes one minute to build the robot and another for it to produce anything
                val next = buildState
                    .copy(ore = buildState.ore - oreCost, obsidian = buildState.obsidian - obsidianCost)
                    .gatherResources()
                    .copy(geodeRobots = buildState.geodeRobots + 1)
                enqueue(next)
            }
        }
    }
    return maxGeodesSoFar
}

sealed interface Material<M> where M : Material<M> {
    operator fun invoke(amount: Int) : Amount<M> = Amount(amount)
}
object Ore : Material<Ore>
object Clay: Material<Clay>
object Obsidian: Material<Obsidian>
object Geode: Material<Geode>

data class Amount<M> (val value: Int) where M : Material<M> {
    operator fun minus (other: Amount<M>) : Amount<M> = Amount(this.value - other.value)
    operator fun plus (other: Amount<M>) : Amount<M> = Amount(this.value + other.value)
    operator fun times (other: Int) : Amount<M> = Amount(this.value * other)
    operator fun compareTo(other: Amount<M>) : Int = this.value.compareTo(other.value)
}

data class Progress(
    val ore: Amount<Ore>,
    val clay: Amount<Clay>,
    val obsidian: Amount<Obsidian>,
    val geodes: Amount<Geode>,

    val oreRobots: Int,
    val clayRobots: Int,
    val obsidianRobots: Int,
    val geodeRobots: Int,

    val minutesRemaining: Int,
) {
    fun gatherResources() : Progress {
        return copy(
            ore = ore + Ore(oreRobots),
            clay = clay + Clay(clayRobots),
            obsidian = obsidian + Obsidian(obsidianRobots),
            geodes = geodes + Geode(geodeRobots),
            minutesRemaining = minutesRemaining - 1
        )
    }

    val heuristic by lazy {
//        geodes.value + //geodes already mined
//            geodeRobots * minutesRemaining + //geodes that will be mined
//            (minutesRemaining - 1 downTo 0).sum() //we can build 1 geode robot per turn — what could that mine?

        geodes.value + //geodes already mined
            geodeRobots * minutesRemaining + //geodes that will be mined
            ((minutesRemaining - 1 downTo 0).sum() / 1.2) //we can build 1 geode robot per turn — what could that mine?
    }

    companion object {
        fun starting(minutesRemaining: Int) = Progress(
            Ore(0), Clay(0), Obsidian(0), Geode(0),
            1, 0, 0, 0,
            minutesRemaining
        )
    }
}

data class Blueprint(
    val id: Int,
    val oreRobotCost: Amount<Ore>,
    val clayRobotCost: Amount<Ore>,
    val obsidianRobotCost: Pair<Amount<Ore>, Amount<Clay>>,
    val geodeRobotCost: Pair<Amount<Ore>, Amount<Obsidian>>
)