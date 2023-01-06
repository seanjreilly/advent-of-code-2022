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

internal enum class SquareType {
    Open, Solid
}

internal class GroveMap(internal val data : Map<Point, SquareType>, val validColumnsForEachRow: Array<IntRange>, val validRowsForEachColumn: Array<IntRange>) {
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

internal enum class TurnDirection {
    Left, Right
}
internal data class Move(val tilesToMove: Int, val turn: TurnDirection)
internal typealias Path = List<Move>

internal fun parsePath(input: List<String>): Path {
    var line = input.last { it.isNotBlank() }

    /*
    The last move contains only a distance, with no turn specified
    We can make this an even list of pairs of moves and turns by adding two extra turns and a move of zero
    The turns cancel each other out, and moving zero tiles won't change the position
     */
    line += "L0R"
    var characters = line.toList()

    val result = mutableListOf<Move>()

    while (characters.isNotEmpty()) {
        val rawTilesToMove = characters.takeWhile { it.isDigit() }.joinToString("")
        val rawTurn = characters[rawTilesToMove.length]
        val turn = TurnDirection.values().first { it.name.startsWith(rawTurn) }

        result.add(Move(rawTilesToMove.toInt(), turn))
        characters = characters.drop(rawTilesToMove.length + 1)
    }

    return result
}

internal fun Point.move(direction: Direction): Point {
    return direction.moveOperation.invoke(this)
}

internal enum class Direction(internal val moveOperation: (Point) -> Point) {
    North(Point::north),
    East(Point::east),
    South(Point::south),
    West(Point::west);

    fun turn(direction: TurnDirection): Direction {
        var index = values().indexOf(this)
        when (direction) {
            TurnDirection.Left -> index--
            TurnDirection.Right -> index++
        }
        return values()[index.mod(values().size)]
    }
}