package day09

import utils.gridmap.Point
import utils.readInput
import kotlin.math.abs

fun main() {
    val input = readInput("Day09")
    println(part1(input))
    println(part2(input))
}

fun part1(input: List<String>): Int {
    val moves = input.map { Move.parse(it) }
    val visitedTailPositions = mutableSetOf<Point>()
    var ropeState = RopeState(Point(0,0), Point(0,0)) //starts at the origina with head and tail overlapping
    moves.forEach { move ->
        val (newState, newTailPositions) = ropeState.processMove(move)
        ropeState = newState
        visitedTailPositions += newTailPositions
    }
    return visitedTailPositions.size
}

fun part2(input: List<String>): Long {
    return 0
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
    if (isAdjacent(newHeadPosition)) {
        return this
    }
    if (newHeadPosition.x == this.x) {
        return if (newHeadPosition.y > this.y) { this.south() } else { this.north() }
    } else if (newHeadPosition.y == this.y) {
        return if (newHeadPosition.x > this.x) { this.east() } else { this.west() }
    }
    if (newHeadPosition.x > this.x) {
        return if (this.y > newHeadPosition.y) { this.northEast() } else {this.southEast() }
    } else {
        return if (this.y > newHeadPosition.y) { this.northWest() } else {this.southWest() }
    }
}

fun Point.isAdjacent(otherPoint: Point): Boolean {
    return abs(this.x - otherPoint.x) <= 1 && abs(this.y - otherPoint.y) <= 1
}

data class RopeState(val headPosition: Point, val tailPosition: Point) {
    fun processMove(move: Move): Pair<RopeState, Set<Point>> {
        val visitedTailPositions = mutableSetOf<Point>(tailPosition)
        var currentHeadPosition = headPosition
        var currentTailPosition = tailPosition
        repeat(move.spaces) {
            currentHeadPosition = move.direction.pointMove.invoke(currentHeadPosition)
            currentTailPosition = currentTailPosition.updateTailPosition(currentHeadPosition)
            visitedTailPositions += currentTailPosition
        }
        return Pair(RopeState(currentHeadPosition, currentTailPosition), visitedTailPositions)
    }
}