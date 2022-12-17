package day14

import utils.Point
import utils.readInput
import kotlin.math.max
import kotlin.math.min
import kotlin.system.measureTimeMillis

fun main() {
    val elapsed = measureTimeMillis {
        val input = readInput("Day14")
        println(part1(input))
        println(part2(input))
    }
    println()
    println("Elapsed time: $elapsed ms.")
}

fun part1(input: List<String>): Int {
    return Cave(input).simulateSand()
}

fun part2(input: List<String>): Int {
    return Cave(input).simulateSandWithFloor()
}

class Cave private constructor(val blockedSquares: MutableSet<Point>) {
    fun simulateSand(): Int {
        var grainsOfSandThatHaveComeToRest = 0
        nextGrain@while(true) {
            var sandPosition = START_POINT
            sandMove@while (sandPosition.x in validX && sandPosition.y <= maxY) {
                val potentialPoints = listOf(sandPosition.south(), sandPosition.southWest(), sandPosition.southEast())
                for (potentialPoint in potentialPoints) {
                    if (potentialPoint !in blockedSquares) {
                        sandPosition = potentialPoint
                        continue@sandMove
                    }
                }
                //the grain of sand has no legal moves, and has come to rest
                grainsOfSandThatHaveComeToRest++
                blockedSquares += sandPosition
                continue@nextGrain
            }
            //the grain of sand has fallen to the abyss!
            return grainsOfSandThatHaveComeToRest
        }
    }

    fun simulateSandWithFloor(): Int {
        var grainsOfSand = 0
        while(START_POINT !in blockedSquares) {
            grainsOfSand++
            var sandPosition = START_POINT
            sandMove@while(true) {
                val newPoint = listOf(sandPosition.south(), sandPosition.southWest(), sandPosition.southEast())
                    .filter { it !in blockedSquares }
                    .firstOrNull { it.y <= maxY + 1 }

                if (newPoint != null ) {
                    sandPosition = newPoint
                    continue@sandMove
                }
                break@sandMove
            }
            //the grain of sand has come to rest, either from being blocked or hitting the floor
            blockedSquares += sandPosition
        }
        return grainsOfSand
    }

    val validX: IntRange = blockedSquares.minOf { it.x } .. blockedSquares.maxOf { it.x}
    val maxY: Int = blockedSquares.maxOf { it.y }

    companion object {
        operator fun invoke(input: List<String>) : Cave {
            val blockedSquares = mutableSetOf<Point>()
            input.forEach { line ->
                val segments = line
                    .split(" -> ")
                    .map { it.split(',') }
                    .map { it.map(String::toInt) }
                    .map { Point(it[0],it[1]) }
                    .windowed(2, 1, false)
                    .map { Pair(it[0], it[1]) }

                segments.forEach { (first, second) ->
                    //line segments are either vertical or horizontal
                    if (first.x == second.x) {
                        rangeBetween(first.y, second.y).forEach { y -> blockedSquares += Point(first.x, y) }
                    } else {
                        rangeBetween(first.x, second.x).forEach { x -> blockedSquares += Point(x, first.y) }
                    }
                }
            }
            return Cave(blockedSquares)
        }

        private val START_POINT = Point(500, 0)
    }
}

private fun rangeBetween(a: Int, b: Int) = min(a, b) .. max(a, b)