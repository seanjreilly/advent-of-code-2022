package day12

import utils.gridmap.GridMap
import utils.gridmap.Point
import utils.readInput
import java.util.*

fun main() {
    val input = readInput("Day12")
    println(part1(input))
    println(part2(input))
}

fun part1(input: List<String>): Int {
    val map = HeightMap.parse(input)
    return map.findShortestNumberOfStepsToEndPoint(listOf(map.startPoint))
}

fun part2(input: List<String>): Int {
    val map = HeightMap.parse(input)
    val squaresOfHeightZero = map.filter { map[it] == 0 }
    return map.findShortestNumberOfStepsToEndPoint(squaresOfHeightZero)
}

class HeightMap(data: Array<Array<Int>>, val startPoint: Point, val endPoint: Point) : GridMap<Int>(data, Point::getCardinalNeighbours) {

    fun findShortestPathToEndPointFromStartPoint(): Path {
        //Djikstra's algorithm
        val tentativeDistances = this.associateWith { Int.MAX_VALUE }.toMutableMap()
        tentativeDistances[startPoint] = 0

        val unvisitedPoints = PriorityQueue<Pair<Point, Int>>(compareBy { it.second })
        tentativeDistances.forEach { (point, distance) ->
            unvisitedPoints.add(Pair(point, distance))
        }

        val cheapestNeighbour = mutableMapOf<Point, Point>()
        val visitedPoints = mutableSetOf<Point>()

        while (unvisitedPoints.isNotEmpty()) {
            val currentPoint = unvisitedPoints.remove().first

            //do an extra filter to remove the duplicate entries from the priority queue (see below)
            if (currentPoint in visitedPoints) {
                continue
            }

            visitedPoints += currentPoint

            if (currentPoint == endPoint) {
                break
            }

            getNeighbours(currentPoint)
                .filter { it !in visitedPoints }
                .filter { this[it] <= this[currentPoint] + 1 } //enforce the "can only go up 1 level" rule here
                .forEach { point ->
                    val currentDistanceToPoint = tentativeDistances[point]!!
                    val altDistance = tentativeDistances[currentPoint]!! + 1
                    if (altDistance < currentDistanceToPoint) {
                        tentativeDistances[point] = altDistance
                        cheapestNeighbour[point] = currentPoint
                        unvisitedPoints.add(Pair(point, altDistance)) //don't remove the old point (slow), just leave a duplicate entry
                    }
                }
        }

        //unroll weights to find the cheapest path
        val shortestPath = emptyList<Point>().toMutableList()
        var currentPoint = endPoint
        do {
            shortestPath.add(currentPoint)
            currentPoint = cheapestNeighbour[currentPoint]!!
        } while (currentPoint != startPoint)
        shortestPath.add(startPoint)
        return shortestPath.reversed()
    }

    fun findShortestNumberOfStepsToEndPoint(startingPoints: Collection<Point>): Int {
        //Djikstra's algorithm in reverse from endPoint to every other reachable point
        val tentativeDistances = this.associateWith { Int.MAX_VALUE }.toMutableMap()
        tentativeDistances[endPoint] = 0

        val unvisitedPoints = PriorityQueue<Pair<Point, Int>>(compareBy { it.second })
        tentativeDistances.forEach { (point, distance) ->
            unvisitedPoints.add(Pair(point, distance))
        }

        val visitedPoints = mutableSetOf<Point>()

        while (unvisitedPoints.isNotEmpty()) {
            val currentPoint = unvisitedPoints.remove().first

            //do an extra filter to remove the duplicate entries from the priority queue (see below)
            if (currentPoint in visitedPoints) {
                continue
            }

            visitedPoints += currentPoint

            val distanceToCurrentPoint = tentativeDistances[currentPoint]!!
            if (distanceToCurrentPoint == Int.MAX_VALUE) {
                break //we've reached an unreachable point
            }

            getNeighbours(currentPoint)
                .filter { it !in visitedPoints }
                .filter { this[currentPoint] <= this[it] + 1 } //enforce the "can only go up 1 level" rule in reverse because we're going backwards
                .forEach { point ->
                    val currentDistanceToPoint = tentativeDistances[point]!!
                    val altDistance = distanceToCurrentPoint + 1
                    if (altDistance < currentDistanceToPoint) { //filter out more expensive paths
                        tentativeDistances[point] = altDistance
                        unvisitedPoints.add(Pair(point, altDistance)) //don't remove the old point (slow), just leave a duplicate entry
                    }
                }
        }

        //tentativePoints now has a distance from endPoint to every other reachable point
        //find the distance from each height zero point and return the minimum distance
        return startingPoints
            .minOf { tentativeDistances[it]!! }
    }

    companion object {
        //region parse logic
        fun parse(input: List<String>): HeightMap {
            val characters = input.map { it.toCharArray() }.toTypedArray()
            val startPoint = findInCharacters(START_POINT_MARKER, characters)
            val endPoint = findInCharacters(END_POINT_MARKER, characters)

            val data = characters
                .mapIndexed {y, line ->
                    line.mapIndexed {x, char ->
                        when(char) {
                            START_POINT_MARKER -> { 0 }
                            END_POINT_MARKER -> { 25 }
                            else -> char - 'a'
                        }
                    }.toTypedArray()
                }.toTypedArray()

            return HeightMap(data, startPoint, endPoint)
        }

        private const val START_POINT_MARKER = 'S'
        private const val END_POINT_MARKER = 'E'

        private fun findInCharacters(char: Char, characters: Array<CharArray>) : Point {
            return characters
                .flatMapIndexed { y,line -> line.mapIndexed { x, it -> Pair(Point(x,y), it) }}
                .first { it.second == char }
                .first
        }
        //endregion
    }
}

typealias Path = List<Point>