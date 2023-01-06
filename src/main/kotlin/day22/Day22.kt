package day22

import utils.CardinalDirection
import utils.Point
import utils.TurnDirection
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
    val map = GroveMap(input)
    val startPosition = Position(map.startPoint, CardinalDirection.East)
    return map.followPath(startPosition, parsePath(input)).score
}

fun part2(input: List<String>): Long {
    return 0
}

internal enum class SquareType {
    Open, Solid
}

internal class GroveMap(internal val data : Map<Point, SquareType>, val validColumnsForEachRow: Array<IntRange>, val validRowsForEachColumn: Array<IntRange>) {
    operator fun get(point: Point): SquareType? = data[point]

    val startPoint = Point(validColumnsForEachRow[0].first, 0)

    /**
     * The maximum valid column index across all the rows
     */
    val maxColumn = validRowsForEachColumn.size - 1

    /**
     * The maximum valid row id across all the columns
     */
    val maxRow = validColumnsForEachRow.size - 1

    fun updatePosition(position: Position, move: Move): Position {
        var location = position.point
        var movesRemaining = move.tilesToMove
        while (movesRemaining-- > 0) {
            var newLocation = location.move(position.direction)
            var squareType = data[newLocation]
            if (squareType == null) {
                //we've moved off of the edge of the map — wrap around
                val x = newLocation.x
                val y = newLocation.y
                newLocation = when (position.direction) {
                    CardinalDirection.East -> Point(validColumnsForEachRow[y].first, y)
                    CardinalDirection.West -> Point(validColumnsForEachRow[y].last, y)
                    CardinalDirection.South -> Point(x, validRowsForEachColumn[x].first)
                    CardinalDirection.North -> Point(x, validRowsForEachColumn[x].last)
                }
                squareType = data[newLocation]!!
            }

            if (squareType == SquareType.Solid) {
                break
            }
            location = newLocation
        }
        return Position(location, position.direction.turn(move.turn))
    }

    fun followPath(currentPosition: Position, path: Path): Position {
        return when (path.isEmpty()) {
            true -> currentPosition
            false -> followPath(updatePosition(currentPosition, path.first()), path.drop(1))
        }
    }

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

internal data class Move(val tilesToMove: Int, val turn: TurnDirection)
internal typealias Path = List<Move>

private val directionScoreMap = mapOf(
    CardinalDirection.East to 0,
    CardinalDirection.South to 1,
    CardinalDirection.West to 2,
    CardinalDirection.North to 3,
)
internal data class Position(val point: Point, val direction: CardinalDirection) {
    val score = ((point.y + 1) * 1000L) + ((point.x + 1) * 4L) + directionScoreMap[direction]!!
}

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