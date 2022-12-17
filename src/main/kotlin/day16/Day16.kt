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
    return ValveLayout(input).findMaximumSoloRelievablePressure()
}

fun part2(input: List<String>): Long {
    return 0
}

data class Location(val code:String)
data class Valve(val location: Location, val flow: Int)

class ValveLayout private constructor(val valves: Map<Location,Valve>, val tunnels: Map<Location, Set<Location>>) {
    fun findMaximumSoloRelievablePressure(): Int {

        var highestPressureReleased = 0
        val shortestDistances = buildShortestDistancesMap()

        val queue = ArrayDeque<SoloProgress>()
        queue.addLast(StartingSoloProgress())

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

            //travel to a valve and turn it on
            (valves.values - previousMove.openedValves)
                .forEach { valve ->
                    val costOfMove = shortestDistances[Pair(previousMove.location, valve.location)]!!
                    val newProgress = SoloProgressUpdate(valve, costOfMove, previousMove)

                    if (newProgress.minutesRemaining <= 0) {
                        return@forEach //can't make it in time!
                    }
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

        //continue until the cost from every location to every other location is known
        while (shortestDistances.size < (tunnels.size * tunnels.size)) {
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

sealed interface SoloProgress {
    val totalPressureReleased: Int
    val openedValves: Set<Valve>
    val location: Location
    val minutesRemaining: Int
}

class StartingSoloProgress : SoloProgress {
    override val location = Location("AA")
    override val totalPressureReleased = 0
    override val openedValves = emptySet<Valve>()
    override val minutesRemaining = 30
}

class SoloProgressUpdate(openedValve: Valve, costOfMove: Int, previous: SoloProgress) : SoloProgress {
    override val location = openedValve.location
    override val minutesRemaining = (previous.minutesRemaining - costOfMove) - 1
    override val totalPressureReleased = (openedValve.flow * minutesRemaining) + previous.totalPressureReleased //pressure only drops on the following turn
    override val openedValves = previous.openedValves + openedValve
}