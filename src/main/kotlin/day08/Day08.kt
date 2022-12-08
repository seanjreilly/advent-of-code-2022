package day08

import utils.gridmap.GridMap
import utils.gridmap.Point
import utils.readInput

fun main() {
    val input = readInput("Day08")
    println(part1(input))
    println(part2(input))
}

fun part1(input: List<String>): Int {
    return ForestMap(input).findVisibleTrees().size
}

fun part2(input: List<String>): Int {
    val forest = ForestMap(input)
    return forest.maxOf { forest.getSenicScore(it) }
}

class ForestMap(rawData: List<String>) : GridMap<Int>(parseTrees(rawData), Point::getCardinalNeighbours) {

    fun findVisibleTrees(): Set<Point> {

        fun visible(point: Point, move: (Point) -> Point): Boolean {
            val treeHeight = this[point]
            var pt = move.invoke(point)
            while (contains(pt)) {
                if (this[pt] >= treeHeight) {
                    return false
                }
                pt = move.invoke(pt)
            }
            return true
        }

        return iterator()
            .asSequence()
            .filter { point ->
                visible(point, Point::north) ||
                    visible(point, Point::east) ||
                    visible(point, Point::south) ||
                    visible(point, Point::west)
            }
            .toSet()
    }

    fun getSenicScore(point: Point): Int {

        fun calculateViewingDistance(direction: (Point) -> Point) : Int {
            var viewingDistance = 0
            var pt = direction.invoke(point)
            while(contains(pt)) {
                viewingDistance++
                if (this[point] <= this[pt]) {
                    break
                }
                pt = direction.invoke(pt)
            }
            return viewingDistance
        }

        val northViewingDistance = calculateViewingDistance(Point::north)
        val eastViewingDistance = calculateViewingDistance(Point::east)
        val southViewingDistance = calculateViewingDistance(Point::south)
        val westViewingDistance = calculateViewingDistance(Point::west)

        return northViewingDistance * eastViewingDistance * southViewingDistance * westViewingDistance
    }

    companion object {
        private fun parseTrees(input:List<String>) : Array<Array<Int>> {
            return input
                .map { it.toCharArray().map { it.digitToInt() }.toTypedArray() }
                .toTypedArray()
        }
    }
}