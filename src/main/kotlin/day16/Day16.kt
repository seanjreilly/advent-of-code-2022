package day16

import utils.readInput
import java.util.*
import kotlin.collections.ArrayDeque
import kotlin.math.max
import kotlin.math.min
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

fun part2(input: List<String>): Int {
    return ValveLayout(input).findMaximumDuoRelievablePressure()
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

    fun findMaximumDuoRelievablePressure(): Int {
        var highestPressureReleased = 0
        val shortestDistances = buildShortestDistancesMap()

        val queue = PriorityQueue<Pair<DuoProgress, Int>>(compareByDescending { it.second })
        queue.add(Pair(StartingDuoProgress(), 0))

        fun upperBound(progress: DuoProgress) : Int {
            val maxMinutesRemaining = max(progress.minutesRemainingA, progress.minutesRemainingB)
            var additionalScore = 0
            val remainingValves = (valves.values - progress.openedValves)
            remainingValves.forEach { valve ->
                val distance = min(shortestDistances[Pair(progress.locationA, valve.location)]!!, shortestDistances[Pair(progress.locationB, valve.location)]!!)
                if (maxMinutesRemaining > distance) {
                    additionalScore += (valve.flow * ((maxMinutesRemaining - distance) - 1))
                }
            }
            return ((additionalScore * 7) /12) + progress.totalPressureReleased
        }

        while (queue.isNotEmpty()) {
            val (previous, previousUpperBound)= queue.remove()

            if (previousUpperBound < highestPressureReleased) {
                continue
            }

            if (previous.totalPressureReleased > highestPressureReleased) {
                highestPressureReleased = previous.totalPressureReleased
            }

            if (previous.openedValves.size == valves.size) {
                // once all the valves are opened it doesn't matter where we move
                continue
            }

            if (previous.minutesRemainingA == 0 && previous.minutesRemainingB == 0) {
                //the person and the elephant are both out of time
                continue
            }

            fun assignToA(valve: Valve, previous: DuoProgress) : DuoProgress {
                val costOfMove = shortestDistances[Pair(previous.locationA, valve.location)]!!
                return DuoProgressUpdateA(valve, costOfMove, previous)
            }

            fun assignToB(valve: Valve, previous: DuoProgress): DuoProgress {
                val costOfMove = shortestDistances[Pair(previous.locationB, valve.location)]!!
                return DuoProgressUpdateB(valve, costOfMove, previous)
            }

            fun addToQueue(newProgress: DuoProgress) {
                if (newProgress.minutesRemainingA < 0 || newProgress.minutesRemainingB < 0) {
                    return //somebody can't make it in time!
                }
                val upperBound = upperBound(newProgress)
                if (upperBound < highestPressureReleased) {
                    return //prune the solution
                }
                queue.add(Pair(newProgress, upperBound))
            }

            val valvesToTry = valves.values - previous.openedValves
            valvesToTry.forEach { valve ->
                addToQueue(assignToA(valve, previous))
                if (previous.locationA != previous.locationB || previous.minutesRemainingA != previous.minutesRemainingB) {
                    //it could matter who we assign this to
                    addToQueue(assignToB(valve, previous))
                }
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

val STARTING_LOCATION = Location("AA")

class StartingSoloProgress : SoloProgress {
    override val location = STARTING_LOCATION
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

sealed interface DuoProgress {
    val openedValves: Set<Valve>
    val locationA: Location
    val minutesRemainingA: Int
    val locationB: Location
    val minutesRemainingB: Int
    val totalPressureReleased: Int
}

class StartingDuoProgress: DuoProgress {
    override val openedValves = emptySet<Valve>()
    override val locationA = Location("AA")
    override val minutesRemainingA = 26
    override val locationB = Location("AA")
    override val minutesRemainingB = 26
    override val totalPressureReleased = 0
}

class DuoProgressUpdateA(valveOpenedByA: Valve, costOfMoveA: Int, previous: DuoProgress) : DuoProgress by previous {
    override val openedValves = previous.openedValves + valveOpenedByA
    override val locationA = valveOpenedByA.location
    override val minutesRemainingA = (previous.minutesRemainingA - costOfMoveA) - 1
    override val totalPressureReleased = (valveOpenedByA.flow * minutesRemainingA) + previous.totalPressureReleased
}

class DuoProgressUpdateB(valveOpenedByB: Valve, costOfMoveB: Int, previous: DuoProgress) : DuoProgress by previous {
    override val openedValves = previous.openedValves + valveOpenedByB
    override val locationB = valveOpenedByB.location
    override val minutesRemainingB = (previous.minutesRemainingB - costOfMoveB) - 1
    override val totalPressureReleased = (valveOpenedByB.flow * minutesRemainingB) + previous.totalPressureReleased
}