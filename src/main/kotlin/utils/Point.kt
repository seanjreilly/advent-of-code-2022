package utils

import kotlin.math.abs

data class Point(val x: Int, val y: Int) {
    fun north() = Point(x, y - 1)
    fun northEast() = Point(x + 1, y - 1)
    fun east() = Point(x + 1, y)
    fun southEast() = Point(x + 1, y + 1)
    fun south() = Point(x, y + 1)
    fun southWest() = Point(x - 1, y + 1)
    fun west() = Point(x - 1, y)
    fun northWest() = Point(x - 1, y - 1)

    fun getCardinalNeighbours() : Collection<Point> {
        return listOf(
            north(),
            south(),
            east(),
            west(),
        )
    }

    fun getCardinalAndDiagonalNeighbours() : Collection<Point> {
        return getCardinalNeighbours() + listOf(
            northEast(),
            northWest(),
            southEast(),
            southWest()
        )
    }

    fun manhattanDistance(other: Point): Int {
        return abs(other.x - x) + abs(other.y - y)
    }

    fun pointsWithManhattanDistance(manhattanDistance: Int): Sequence<Point> {
        return (0..manhattanDistance)
            .asSequence()
            .flatMap { xAdjustment ->
                val yAdjustment = manhattanDistance - xAdjustment
                sequenceOf(
                    Point(this.x + xAdjustment, this.y + yAdjustment),
                    Point(this.x + xAdjustment, this.y - yAdjustment),
                    Point(this.x - xAdjustment, this.y + yAdjustment),
                    Point(this.x - xAdjustment, this.y - yAdjustment),
                )
            }
    }
}