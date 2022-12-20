package day19

import utils.readInput
import utils.stack.Stack
import utils.stack.pop
import utils.stack.push
import kotlin.math.max
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
    return parse(input).sumOf { it.id * calculateMaximumGeodes(it) }
}

fun part2(input: List<String>): Long {
    return 0
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

fun calculateMaximumGeodes(blueprint: Blueprint): Int {

    var maxGeodesSoFar = 0
    val stack = Stack<Progress>()
    stack.push(Progress.starting())
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
        val previous = stack.pop()

        maxGeodesSoFar = max(previous.geodes.value, maxGeodesSoFar)

        if (previous.minutesRemaining == 0) {
            continue
        }

        if (previous.heuristic < maxGeodesSoFar) {
            continue
        }

        val canAffordOreRobot = previous.ore >= blueprint.oreRobotCost
        val canAffordClayRobot = previous.ore >= blueprint.clayRobotCost
        val canAffordObsidianRobot = previous.ore >= blueprint.obsidianRobotCost.first &&
            previous.clay >= blueprint.obsidianRobotCost.second
        val canAffordGeodeRobot = previous.ore >= blueprint.geodeRobotCost.first &&
            previous.obsidian >= blueprint.geodeRobotCost.second

        //there is always at least the option (sometimes it's mandatory) to not build a robot
        enqueue(previous.gatherResources())

        if (canAffordOreRobot && previous.oreRobots < maxOreRobotsNeeded) {
            val next = previous
                .copy(ore = previous.ore - blueprint.oreRobotCost)
                .gatherResources()
                .copy(oreRobots = previous.oreRobots + 1)
            enqueue(next)
        }

        if (canAffordClayRobot && previous.clayRobots < maxClayRobotsNeeded) {
            val next = previous
                .copy(ore = previous.ore - blueprint.clayRobotCost)
                .gatherResources()
                .copy(clayRobots = previous.clayRobots + 1)
            enqueue(next)
        }

        if (canAffordObsidianRobot && previous.obsidianRobots < maxObsidianRobotsNeeded) {
            val (oreCost, clayCost) = blueprint.obsidianRobotCost
            val next = previous
                .copy(ore = previous.ore - oreCost, clay = previous.clay - clayCost)
                .gatherResources()
                .copy(obsidianRobots = previous.obsidianRobots + 1)
            enqueue(next)
        }

        if (canAffordGeodeRobot) {
            val (oreCost, obsidianCost) = blueprint.geodeRobotCost
            val next = previous
                .copy(ore = previous.ore - oreCost, obsidian = previous.obsidian - obsidianCost)
                .gatherResources()
                .copy(geodeRobots = previous.geodeRobots + 1)
            enqueue(next)
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
            ((minutesRemaining - 1 downTo 0).sum() / 1.5) //we can build 1 geode robot per turn — what could that mine?
    }

    companion object {
        fun starting() = Progress(
            Ore(0), Clay(0), Obsidian(0), Geode(0),
            1, 0, 0, 0,
            24
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