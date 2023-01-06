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
        fun `validColumnsForEachRow should return a range of valid columns for each row`() {
            val map = GroveMap(sampleInput)

            assert(map.validColumnsForEachRow.size == map.maxRow + 1)

            assert(map.validColumnsForEachRow[0] == 8..11)
            assert(map.validColumnsForEachRow[1] == 8..11)
            assert(map.validColumnsForEachRow[2] == 8..11)
            assert(map.validColumnsForEachRow[3] == 8..11)

            assert(map.validColumnsForEachRow[4] == 0..11)
            assert(map.validColumnsForEachRow[5] == 0..11)
            assert(map.validColumnsForEachRow[6] == 0..11)
            assert(map.validColumnsForEachRow[7] == 0..11)

            assert(map.validColumnsForEachRow[8] == 8..15)
            assert(map.validColumnsForEachRow[9] == 8..15)
            assert(map.validColumnsForEachRow[10] == 8..15)
            assert(map.validColumnsForEachRow[11] == 8..15)
        }

        @Test
        fun `validRowsForEachColumn should return a range of valid rows for each column`() {
            val map = GroveMap(sampleInput)

            assert(map.validRowsForEachColumn.size == map.maxColumn + 1)

            assert(map.validRowsForEachColumn[0] == 4..7)
            assert(map.validRowsForEachColumn[1] == 4..7)
            assert(map.validRowsForEachColumn[2] == 4..7)
            assert(map.validRowsForEachColumn[3] == 4..7)
            assert(map.validRowsForEachColumn[4] == 4..7)
            assert(map.validRowsForEachColumn[5] == 4..7)
            assert(map.validRowsForEachColumn[6] == 4..7)
            assert(map.validRowsForEachColumn[7] == 4..7)

            assert(map.validRowsForEachColumn[8] == 0..11)
            assert(map.validRowsForEachColumn[9] == 0..11)
            assert(map.validRowsForEachColumn[10] == 0..11)
            assert(map.validRowsForEachColumn[11] == 0..11)

            assert(map.validRowsForEachColumn[12] == 8..11)
            assert(map.validRowsForEachColumn[13] == 8..11)
            assert(map.validRowsForEachColumn[14] == 8..11)
            assert(map.validRowsForEachColumn[15] == 8..11)
        }
    }

    //parseDirections should return a path — a list of moves

    //TODO: show how turning clockwise and counterclockwise moves around the cardinal directions
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