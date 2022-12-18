package day17

import utils.Point
import utils.readInput
import kotlin.math.max
import kotlin.system.measureTimeMillis

fun main() {
    val elapsed = measureTimeMillis {
        val input = readInput("Day17").first()
        println(part1(input))
        println(part2(input))
    }
    println()
    println("Elapsed time: $elapsed ms.")
}

fun part1(input: String): Int {
    val simulator = Simulator(input)
    repeat(2022) { simulator.simulateRock() }
    return simulator.maximumRockHeight
}

fun part2(input: String): Long {
    return 0
}

class FallingRock internal constructor(val points: Shape) {

    val valid: Boolean = points.all { it.x in 0..6 && it.y >= 0 }

    fun left(): FallingRock = FallingRock(points, -1, 0)
    fun right(): FallingRock = FallingRock(points, 1, 0)
    fun down(): FallingRock = FallingRock(points, 0, -1)

    companion object {
        operator fun invoke(shape:Shape, xAdjustment: Int, yAdjustment: Int) : FallingRock {
            return FallingRock(shape.translate(xAdjustment, yAdjustment))
        }

        operator fun invoke(shape: Shape, startingHeight: Int) : FallingRock {
            return FallingRock(shape.translate(2, startingHeight))
        }
    }
}

class Simulator(private val jetPatterns: String) {

    var fallenRocks = emptySet<Point>()
        private set

    var maximumRockHeight = 0
        private set

    private var jetIndex = 0
    private var shapeIndex = 0

    private val rockHeightByColumn = IntArray(7) { -1 }

    fun simulateRock() {
        var rock = FallingRock(ROCK_SHAPES[shapeIndex++ % ROCK_SHAPES.size], maximumRockHeight + 3)
        while(true) {
            val jet = jetPatterns[jetIndex++ % jetPatterns.length]

            //try to move with the jet
            val rockAfterJet = when (jet) {
                '<' -> rock.left()
                else -> rock.right()
            }
            if (rockAfterJet.valid && rockAfterJet.points.none { it in fallenRocks }) {
                //the jet can move
                rock = rockAfterJet
            }


            //try to fall
            val rockAfterFalling = rock.down()
            if (rockAfterFalling.valid && rockAfterFalling.points.none { it in fallenRocks }) {
                //the rock can fall
                rock = rockAfterFalling
                continue
            }
            break
        }
        //this is as far as the rock can fall without overlapping a previous rock or the floor
        fallenRocks += rock.points
        maximumRockHeight = max(maximumRockHeight, rock.points.maxOf { it.y } + 1) //rock height is one-based
        rock.points.forEach { (x,y) ->
            val currentHeight = rockHeightByColumn[x]
            rockHeightByColumn[x] = max(currentHeight, y)
        }
        val relevantHeight = rockHeightByColumn.min()
        fallenRocks = fallenRocks.filter { it.y >= relevantHeight - 10 }.toSet()
    }

    fun printFallenRocks() : String {
        val result = StringBuilder()
        ((maximumRockHeight + 2) downTo 0).forEach { y ->
            val rocksInRow = (0..6).map { x ->
                when (Point(x, y) in fallenRocks) {
                    true -> '#'
                    false -> '.'
                }
            }.joinToString("")
            result.append("|$rocksInRow|\n")
        }
        result.append("+-------+")
        return result.toString()
    }
}

typealias Shape = Set<Point>

fun Shape.translate(xAdjustment: Int, yAdjustment: Int): Shape {
    return this.map { Point(it.x + xAdjustment, it.y + yAdjustment) }.toSet()
}
val HORIZONTAL_BAR_SHAPE: Shape = setOf(Point(0,0), Point(1,0), Point(2,0), Point(3,0))
val CROSS_SHAPE: Shape = setOf(Point(1,0), Point(0,1), Point(1,1), Point(2,1), Point(1, 2))
val ELL_SHAPE: Shape = setOf(Point(0,0), Point(1,0), Point(2,0), Point(2,1), Point(2,2))
val VERTICAL_BAR_SHAPE: Shape = setOf(Point(0,0), Point(0,1), Point(0,2), Point(0,3))
val SQUARE_SHAPE: Shape = setOf(Point(0,0), Point(1,0), Point(0,1), Point(1,1))

val ROCK_SHAPES = listOf(HORIZONTAL_BAR_SHAPE, CROSS_SHAPE, ELL_SHAPE, VERTICAL_BAR_SHAPE, SQUARE_SHAPE)