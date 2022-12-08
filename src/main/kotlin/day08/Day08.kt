package day08

import utils.gridmap.GridMap
import utils.gridmap.Point
import utils.readInput
import kotlin.math.max

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
        /*
         * Start by marking each tree on the edge visible (nothing blocking the view of these trees)
         * Then sweep away from the edge. Each tree with a visible neighbour that is taller than the
         * largest tree seen in the row or column so far is visible
         *
         * Do this for each edge and then combine the results
         */
        val result = mutableSetOf<Point>()
        var highestTreeSoFar: IntArray

        //sweep south from the north edge
        (0 until width).forEach { result += Point(it, 0) } //explicitly add the trees on the edge in case one is zero units tall
        highestTreeSoFar = IntArray(width) { this[Point(it, 0)] }
        (1 until height).forEach { y ->
            (0 until width).forEach {x ->
                val point = Point(x,y)
                val height = this[point]
                if (height > highestTreeSoFar[x]) {
                    result += point
                }
                highestTreeSoFar[x] = max(highestTreeSoFar[x], height)
            }
        }

        //sweep north from the south edge
        (0 until width).forEach { result += Point(it, height -1) } //explicitly add the trees on the edge in case one is zero height
        highestTreeSoFar = IntArray(width) { this[Point(it, height -1)] }
        (height - 2 downTo 0).forEach { y ->
            (0 until width).forEach {x ->
                val point = Point(x,y)
                val height = this[point]
                if (height > highestTreeSoFar[x]) {
                    result += point
                }
                highestTreeSoFar[x] = max(highestTreeSoFar[x], height)
            }
        }



        //sweep east from the west edge
        (0 until height).forEach { result += Point(0, it) } //explicitly add the trees on the edge in case one is zero units tall
        highestTreeSoFar = IntArray(height) { this[Point(0, it)] }
        (0 until width).forEach { x ->
            (0 until height).forEach {y ->
                val point = Point(x,y)
                val height = this[point]
                if (height > highestTreeSoFar[y]) {
                    result += point
                }
                highestTreeSoFar[y] = max(highestTreeSoFar[y], height)
            }
        }


        //sweep west from the east edge
        (0 until height).forEach { result += Point(width -1, it) } //explicitly add the trees on the edge in case one is zero units tall
        highestTreeSoFar = IntArray(height) { this[Point(width -1, it)] }
        (width - 2 downTo 0).forEach { x ->
            (0 until height).forEach { y ->
                val point = Point(x,y)
                val height = this[point]
                if (height > highestTreeSoFar[y]) {
                    result += point
                }
                highestTreeSoFar[y] = max(highestTreeSoFar[y], height)
            }
        }
        return result
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