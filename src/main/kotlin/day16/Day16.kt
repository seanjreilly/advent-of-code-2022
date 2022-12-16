package day16

import utils.readInput
import java.lang.Integer.min
import kotlin.system.measureTimeMillis

fun main() {
    val elapsed = measureTimeMillis {
        val input = readInput("Day16")
        println(part1(input))
        println(part2(input))
    }
    println()
    println("Elapsed time: $elapsed ms.")
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
        val shortestDistances = buildShortestDistancesMap()

        val queue = ArrayDeque<Progress>()
        queue.addLast(StartingProgress())

        while (queue.isNotEmpty()) {
            val previousMove = queue.removeFirst()

            if (previousMove.totalPressureReleased > highestPressureReleased) {
                highestPressureReleased = previousMove.totalPressureReleased
            }

            if (previousMove.minutesRemaining == 0 || previousMove.openedValves.size == valves.size) {
                // once all the valves are opened it doesn't matter where we move
                // total pressure relieved is calculated up front, so no need to continue
                // simulating once all valves are opened

                //and of course if we run out of time we run out of time
                continue
            }

            if (previousMove.location != STARTING_LOCATION) {
                //we're at a valve... is it open?
                val valve = valves[previousMove.location]!!
                if (valve !in previousMove.openedValves) {
                    //it's not open, so open it
                    val newProgress = OpenValveProgress(valve, previousMove)
                    queue.addLast(newProgress)
                    continue
                }

            }

            //This valve is open (or we're not at a valve yet), so move to a valve
            valves.keys
                .filter { it != previousMove.location } //don't move to the valve we are already at
                .filter { it !in previousMove.openedValves.map { it.location } } //or a valve we've already opened
                .forEach { destination ->
                    val costOfMove = shortestDistances[Pair(previousMove.location, destination)]!!
                    if (costOfMove > previousMove.minutesRemaining) {
                        return@forEach //can't make it in time!
                    }
                    val newProgress = MoveProgress(destination, costOfMove, previousMove)
                    queue.addLast(newProgress)
                }
        }
        return highestPressureReleased
    }

    /**
     * Builds a map capturing the number of minutes it takes to visit any notable destination from a notable source.
     * Notable sources are the start point plus locations with working valves, and notable
     * destinations are locations with working valves.
     *
     * Using this construct should help improve performance finding a solution.
     */
    private fun buildShortestDistancesMap() : Map<Pair<Location,Location>, Int> {
        //optimize — find the shortest distance from each location to every other location
        val shortestDistances = mutableMapOf <Pair<Location,Location>, Int>()
        tunnels.forEach { (location, otherLocations) ->
            otherLocations.forEach { otherLocation ->
                shortestDistances[Pair(location, otherLocation)] = 1
            }
        }

        //nodes - 1 times is enough to reach every destination
        repeat(tunnels.size - 1) {
            //do this in 2 phases to avoid ConcurrentModificationException
            val mappingsToAdd = mutableListOf<Pair<Pair<Location, Location>, Int>>()
            shortestDistances.forEach { (journey, distance) ->
                val (location, midpoint) = journey
                tunnels[midpoint]!!.forEach { destination ->
                    val key = Pair(location, destination)
                    mappingsToAdd += key to distance + 1
                }
            }
            mappingsToAdd.forEach { (journey, newDistance) ->
                val existingDistance = shortestDistances[journey] ?: Int.MAX_VALUE
                shortestDistances[journey] = min(newDistance, existingDistance)
            }
        }

        //prune the map so that it only tracks journeys from AA or a valve to a valve (keep the costs)
        return shortestDistances
            .filterKeys { it.first in valves.keys + Location("AA") }
            .filterKeys { it.second in valves.keys }
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

val STARTING_LOCATION = Location("AA")

class StartingProgress : Progress {
    override val location = STARTING_LOCATION
    override val totalPressureReleased = 0
    override val openedValves = emptySet<Valve>()
    override val minutesRemaining = 30
}

class MoveProgress(override val location: Location, costOfMove: Int, previousMove: Progress) : Progress by previousMove {
    override val minutesRemaining = previousMove.minutesRemaining - costOfMove
}

class OpenValveProgress(openedValve: Valve, previousMove: Progress) : Progress by previousMove {
    override val minutesRemaining = previousMove.minutesRemaining - 1
    override val totalPressureReleased = (openedValve.flow * (minutesRemaining)) + previousMove.totalPressureReleased //pressure only drops on the following turn
    override val openedValves = previousMove.openedValves + openedValve
}