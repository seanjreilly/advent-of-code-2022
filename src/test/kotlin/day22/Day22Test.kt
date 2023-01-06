package day22

import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import utils.Point

class Day22Test {
    private val sampleInput = """
                ...#
                .#..
                #...
                ....
        ...#.......#
        ........#...
        ..#....#....
        ..........#.
                ...#....
                .....#..
                .#......
                ......#.
        
        10R5L5R10L4R5L5
    """.trimIndent().lines()

    @Nested
    inner class GroveMapTest {
        @Test
        fun `should represent an irregular map of open and solid squares`() {
            val input = sampleInput.take(3) //use more limited input so we can check fewer points

            val map = GroveMap(input)

            assert(map[Point(8,0)] == SquareType.Open)
            assert(map[Point(9,0)] == SquareType.Open)
            assert(map[Point(10,0)] == SquareType.Open)
            assert(map[Point(11,0)] == SquareType.Solid)

            assert(map[Point(8,1)] == SquareType.Open)
            assert(map[Point(9,1)] == SquareType.Solid)
            assert(map[Point(10,1)] == SquareType.Open)
            assert(map[Point(11,1)] == SquareType.Open)

            assert(map[Point(8,2)] == SquareType.Solid)
            assert(map[Point(9,2)] == SquareType.Open)
            assert(map[Point(10,2)] == SquareType.Open)
            assert(map[Point(11,2)] == SquareType.Open)

            assert(map.data.size == 12) {"Unexpected square found in the map"}
        }

        @Test
        fun `constructor should ignore the blank line and everything following it`() {
            GroveMap(sampleInput)
        }

        @Test
        fun `maxColumn should return the maximum x value of a point in the map`() {
            val map = GroveMap(sampleInput)

            assert(map.maxColumn == 15)
        }

        @Test
        fun `maxRow should return the maximum y value of a point in the map`() {
            val map = GroveMap(sampleInput)

            assert(map.maxRow == 11)
        }

        @Test
        fun `validColumns should return a range of valid columns for each row`() {
            val map = GroveMap(sampleInput)

            assert(map.validColumns.size == map.maxRow + 1)

            assert(map.validColumns[0] == 8..11)
            assert(map.validColumns[1] == 8..11)
            assert(map.validColumns[2] == 8..11)
            assert(map.validColumns[3] == 8..11)

            assert(map.validColumns[4] == 0..11)
            assert(map.validColumns[5] == 0..11)
            assert(map.validColumns[6] == 0..11)
            assert(map.validColumns[7] == 0..11)

            assert(map.validColumns[8] == 8..15)
            assert(map.validColumns[9] == 8..15)
            assert(map.validColumns[10] == 8..15)
            assert(map.validColumns[11] == 8..15)
        }

        @Test
        fun `validRows should return a range of valid rows for each column`() {
            val map = GroveMap(sampleInput)

            assert(map.validRows.size == map.maxColumn + 1)

            assert(map.validRows[0] == 4..7)
            assert(map.validRows[1] == 4..7)
            assert(map.validRows[2] == 4..7)
            assert(map.validRows[3] == 4..7)
            assert(map.validRows[4] == 4..7)
            assert(map.validRows[5] == 4..7)
            assert(map.validRows[6] == 4..7)
            assert(map.validRows[7] == 4..7)

            assert(map.validRows[8] == 0..11)
            assert(map.validRows[9] == 0..11)
            assert(map.validRows[10] == 0..11)
            assert(map.validRows[11] == 0..11)

            assert(map.validRows[12] == 8..11)
            assert(map.validRows[13] == 8..11)
            assert(map.validRows[14] == 8..11)
            assert(map.validRows[15] == 8..11)
        }
    }

    //parseDirections should return a path — a list of moves

    //TODO: show how turning clockwise and counterclockwise moves around the cardinal directions
}

enum class SquareType {
    Open, Solid
}
class GroveMap(internal val data : Map<Point, SquareType>, val validColumns: Array<IntRange>, val validRows: Array<IntRange>) {
    operator fun get(point: Point): SquareType? = data[point]

    val maxColumn = validRows.size - 1
    val maxRow = validColumns.size - 1

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