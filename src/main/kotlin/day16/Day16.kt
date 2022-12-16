package day16

import utils.readInput
import kotlin.system.measureTimeMillis

fun main() {
    val elapsed = measureTimeMillis {
        val input = readInput("Day16")
        println(part1(input))
        println(part2(input))
    }
    println("Calculations took ${elapsed} ms.")
}

fun part1(input: List<String>): Int {
    return ValveLayout(input).findMaximumRelievablePressure()
}

fun part2(input: List<String>): Long {
    return 0
}

data class Location(val code:String)
data class Valve(val location: Location, val flow: Int)

class ValveLayout private constructor(val valves: Map<Location,Valve>, val tunnels: Map<Location, Set<Location>>) {
    fun findMaximumRelievablePressure(): Int {
        var highestPressureReleased = 0

        val queue = ArrayDeque<Progress>()
        queue.addLast(StartingProgress())

//        val highScoreForValveCombinations = mutableMapOf<Set<Valve>, Int>()

        while (queue.isNotEmpty()) {
            val previousMove = queue.removeFirst()
            if (previousMove.minutesRemaining == 0 || previousMove.openedValves.size == valves.size) {
                //once all the valves are opened it doesn't matter where we move
                // total pressure relieved is calculated up front, so no need to even simulate
                if (previousMove.totalPressureReleased > highestPressureReleased) {
                    highestPressureReleased = previousMove.totalPressureReleased
                }
                continue //out of time
            }

            valves[previousMove.location]?.let {
                if (it in previousMove.openedValves) {
                    return@let //we've already opened this value
                }
                val newProgress = OpenValveProgress(it, previousMove)
                queue.addLast(newProgress)

//                /*
//                    PRUNE:
//                    Only consider this step if we have a better score than the last time this set of valves was opened.
//                    If we know it's not as good no need to simulate it
//                 */
//                val highScore = highScoreForValveCombinations[newProgress.openedValves]
//                if (newProgress.totalPressureReleased >= (highScore ?:0)) {
//                    highScoreForValveCombinations[newProgress.openedValves] = newProgress.totalPressureReleased
//                    queue.addLast(newProgress)
//                }
            }

            val previousLocation = if (previousMove is MoveProgress) {
                previousMove.previousLocation
            } else {
                null
            }

            tunnels[previousMove.location]!!
                .filter { it != previousLocation } //don't go somewhere we've just been
                .forEach { newLocation ->
                    queue.addLast(MoveProgress(newLocation, previousMove))
                }
        }
        return highestPressureReleased
    }

    companion object {
        private val parseRegex = """Valve (\w{2}) has flow rate=(\d+); tunnels? leads? to valves? (.+)""".toRegex()
        operator fun invoke(input:List<String>) : ValveLayout {
            val valves = mutableMapOf<Location, Valve>()
            val tunnels = mutableMapOf<Location, Set<Location>>()
            input.forEach { line ->
                val(rawLocation, rawFlowRate, rawOtherLocations) = parseRegex.matchEntire(line)!!.destructured
                val location = Location(rawLocation)
                val flowRate = rawFlowRate.toInt()
                if (flowRate > 0) {
                    valves[location] = Valve(location, flowRate)
                }
                val otherLocations = rawOtherLocations.split(",")
                    .map { it.trim() }
                    .map(::Location)
                    .toSet()
                tunnels[location] = otherLocations
            }
            return ValveLayout(valves, tunnels)
        }
    }
}

sealed interface Progress {
    val totalPressureReleased: Int
    val openedValves: Set<Valve>
    val location: Location
    val minutesRemaining: Int
}

class StartingProgress : Progress {
    override val location = Location("AA")
    override val totalPressureReleased = 0
    override val openedValves = emptySet<Valve>()
    override val minutesRemaining = 30
}

class MoveProgress(override val location: Location, previousMove: Progress) : Progress by previousMove {
    override val minutesRemaining = previousMove.minutesRemaining - 1
    val previousLocation: Location = previousMove.location
}

class OpenValveProgress(openedValve: Valve, previousMove: Progress) : Progress by previousMove {
    override val minutesRemaining = previousMove.minutesRemaining - 1
    override val totalPressureReleased = (openedValve.flow * (minutesRemaining)) + previousMove.totalPressureReleased //pressure only drops on the following turn
    override val openedValves = previousMove.openedValves + openedValve
}