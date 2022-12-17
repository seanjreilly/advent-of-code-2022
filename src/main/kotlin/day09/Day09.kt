package day09

import utils.Point
import utils.readInput
import kotlin.math.abs
import kotlin.system.measureTimeMillis

fun main() {
    val elapsed = measureTimeMillis {
        val input = readInput("Day09")
        println(part1(input))
        println(part2(input))
    }
    println()
    println("Elapsed time: $elapsed ms.")
}

fun part1(input: List<String>): Int {
    val ropeState = RopeState(Point(0,0), Point(0,0)) //a two-knot rope starting at the origin with head and tail overlapping
    return processMoves(input, ropeState)
}

fun part2(input: List<String>): Int {
    val ropeState = RopeState(List(10) { Point(0,0) }) //a 10-knot rope starting with all knots at the origin
    return processMoves(input, ropeState)
}

private fun processMoves(input: List<String>, initialRopeState: RopeState): Int {
    var ropeState = initialRopeState
    val moves = input.map { Move.parse(it) }
    val visitedTailPositions = mutableSetOf<Point>()
    moves.forEach { move ->
        val (newState, tailPositionsVisitedThisTurn) = ropeState.processMove(move)
        ropeState = newState
        visitedTailPositions += tailPositionsVisitedThisTurn
    }
    return visitedTailPositions.size
}

enum class Direction(val pointMove: (Point) -> Point) {
    Right(Point::east),
    Left(Point::west),
    Up(Point::north),
    Down(Point::south);

    companion object {
        fun fromCode(code:Char) : Direction {
            val result = Direction.values().firstOrNull { it.name.startsWith(code) }
            return result ?: throw java.lang.IllegalArgumentException("Invalid code '${code}'")
        }
    }
}

data class Move(val direction: Direction, val spaces:Int) {
    companion object {
        private val PARSE_REGEX = """([RLUD]) (\d+)""".toRegex()

        fun parse(input: String) : Move {
            val (direction, spaces) = PARSE_REGEX.matchEntire(input)!!.destructured
            return Move(Direction.fromCode(direction.first()), spaces.toInt())
        }
    }
}

fun Point.updateTailPosition(newHeadPosition: Point): Point {
    fun northOrSouth() = if (newHeadPosition.y > this.y) { this.south() } else { this.north() }
    fun eastOrWest() = if (newHeadPosition.x > this.x) { this.east() } else { this.west() }
    fun northEastOrSouthEast() = if (this.y > newHeadPosition.y) { this.northEast() } else {this.southEast() }
    fun northWestOrSouthWest() = if (this.y > newHeadPosition.y) { this.northWest() } else {this.southWest() }

    return when {
        isAdjacent(newHeadPosition) -> this //this knot doesn't need to move if the previous knot overlaps or is adjacent
        newHeadPosition.x == this.x -> northOrSouth()
        newHeadPosition.y == this.y -> eastOrWest()
        newHeadPosition.x > this.x -> northEastOrSouthEast()
        else -> northWestOrSouthWest()
    }
}

fun Point.isAdjacent(otherPoint: Point): Boolean {
    return abs(this.x - otherPoint.x) <= 1 && abs(this.y - otherPoint.y) <= 1
}

data class RopeState(val knotPositions: List<Point>) {
    constructor(vararg knotPositions: Point) : this(knotPositions.toList())

    fun processMove(move: Move): Pair<RopeState, Set<Point>> {

        val visitedTailPositions = mutableSetOf<Point>(knotPositions.last())
        var currentPositions = knotPositions
        repeat(move.spaces) {
            val newPositions = mutableListOf<Point>()
            newPositions += move.direction.pointMove.invoke(currentPositions.first()) //update the head with the move instruction
            //update everything else using updateTailPosition
            currentPositions.drop(1).forEach {
                val updatedPosition = it.updateTailPosition(newPositions.last())
                newPositions += updatedPosition
            }
            visitedTailPositions += newPositions.last()
            currentPositions = newPositions
        }
        return Pair(RopeState(currentPositions), visitedTailPositions)
    }
}