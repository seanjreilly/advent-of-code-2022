package day22

import utils.Point
import utils.readInput
import kotlin.system.measureTimeMillis

fun main() {
    val elapsed = measureTimeMillis {
        val input = readInput("Day22")
        println(part1(input))
        println(part2(input))
    }
    println()
    println("Elapsed time: $elapsed ms.")
}

fun part1(input: List<String>): Long {
    return 0
}

fun part2(input: List<String>): Long {
    return 0
}

enum class SquareType {
    Open, Solid
}

class GroveMap(internal val data : Map<Point, SquareType>, val validColumnsForEachRow: Array<IntRange>, val validRowsForEachColumn: Array<IntRange>) {
    operator fun get(point: Point): SquareType? = data[point]

    /**
     * The maximum valid column index across all the rows
     */
    val maxColumn = validRowsForEachColumn.size - 1

    /**
     * The maximum valid row id across all the columns
     */
    val maxRow = validColumnsForEachRow.size - 1

    companion object {
        operator fun invoke(input: List<String>) : GroveMap {
            val points = input
                .takeWhile { it.isNotBlank() }
                .flatMapIndexed { y, line ->
                    line.toCharArray()
                        .withIndex()
                        .map { Point(it.index, y) to it.value }
                }
                .filter { it.second != ' ' }
                .associate {
                    val value = when (it.second) {
                        '#' -> SquareType.Solid
                        '.' -> SquareType.Open
                        else -> throw IllegalStateException("Unexpected grid content '${it.second}'")
                    }
                    it.first to value
                }

            val validColumns = points.keys
                .groupBy { it.y }
                .map { (y, points) ->
                    y to (points.minOf { it.x }..points.maxOf { it.x })
                }
                .sortedBy { it.first }
                .map { it.second }
                .toTypedArray()

            val validRows = points.keys
                .groupBy { it.x }
                .map { (x, points) ->
                    x to (points.minOf { it.y } .. points.maxOf { it.y })
                }
                .sortedBy { it.first }
                .map { it.second }
                .toTypedArray()

            return GroveMap(points, validColumns, validRows)
        }
    }
}