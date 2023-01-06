package day22

import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.EnumSource
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

    @Test
    fun `parsePath should return a list of moves`() {
        val path:Path = parsePath(sampleInput)

        assert(path[0] == Move(10, TurnDirection.Right))
        assert(path[1] == Move(5, TurnDirection.Left))
        assert(path[2] == Move(5, TurnDirection.Right))
        assert(path[3] == Move(10, TurnDirection.Left))
        assert(path[4] == Move(4, TurnDirection.Right))
        assert(path[5] == Move(5, TurnDirection.Left))
        assert(path[6].tilesToMove == 5)
    }

    @Test
    fun `parsePath should pad the list to make an even number of moves with the same destination _and_ orientation`() {
        val path = parsePath(sampleInput)

        val secondLastMoveDirection = path.dropLast(1).last().turn
        val lastMove = path.last()

        assert(path.size == 8) //an extra entry is added
        assert(lastMove.tilesToMove == 0) { "the last move should move zero tiles to not change the position" }
        assert(lastMove.turn != secondLastMoveDirection) { "the last move should turn opposite to the padded move added to the second last move to preserve the orientation" }
    }

    @Nested
    inner class MoveTest {
        @Test
        fun `move should move 1 point north given North`() {
            val result = Point(0, 0).move(Direction.North)
            assert(result == Point(0, -1))
        }

        @Test
        fun `move should move 1 point east given East`() {
            val result = Point(0, 0).move(Direction.East)
            assert(result == Point(1, 0))
        }

        @Test
        fun `move should move 1 point south given South`() {
            val result = Point(0, 0).move(Direction.South)
            assert(result == Point(0, 1))
        }

        @Test
        fun `move should move 1 point west given West`() {
            val result = Point(0, 0).move(Direction.West)
            assert(result == Point(-1, 0))
        }
    }

    @Nested
    inner class DirectionTest {

        @Test
        fun `turning left from North should return West`() {
            val result = Direction.North.turn(TurnDirection.Left)
            assert(result == Direction.West)
        }

        @Test
        fun `turning right from North should return East`() {
            val result = Direction.North.turn(TurnDirection.Right)
            assert(result == Direction.East)
        }

        @Test
        fun `turning left from East should return North`() {
            val result = Direction.East.turn(TurnDirection.Left)
            assert(result == Direction.North)
        }

        @Test
        fun `turning right from East should return South`() {
            val result = Direction.East.turn(TurnDirection.Right)
            assert(result == Direction.South)
        }

        @Test
        fun `turning left from South should return East`() {
            val result = Direction.South.turn(TurnDirection.Left)
            assert(result == Direction.East)
        }

        @Test
        fun `turning right from South should return West`() {
            val result = Direction.South.turn(TurnDirection.Right)
            assert(result == Direction.West)
        }

        @Test
        fun `turning left from West should return South`() {
            val result = Direction.South.turn(TurnDirection.Left)
            assert(result == Direction.East)
        }

        @Test
        fun `turning right from West should return North`() {
            val result = Direction.South.turn(TurnDirection.Right)
            assert(result == Direction.West)
        }

        @ParameterizedTest
        @EnumSource(Direction::class)
        internal fun `turning left 4 times should return the original direction`(direction: Direction) {
            var newDirection = direction
            repeat(4) {
                newDirection = newDirection.turn(TurnDirection.Left)
            }
            assert(newDirection == direction)
        }

        @ParameterizedTest
        @EnumSource(Direction::class)
        internal fun `turning right 4 times should return the original direction`(direction: Direction) {
            var newDirection = direction
            repeat(4) {
                newDirection = newDirection.turn(TurnDirection.Right)
            }
            assert(newDirection == direction)
        }

        @ParameterizedTest
        @EnumSource(Direction::class)
        internal fun `turning left and then right should return the original direction`(direction: Direction) {
            val newDirection = direction.turn(TurnDirection.Left)
            val result = newDirection.turn(TurnDirection.Right)
            assert(direction != newDirection)
            assert(result == direction)
        }

        @ParameterizedTest
        @EnumSource(Direction::class)
        internal fun `turning right and then left should return the original direction`(direction: Direction) {
            val newDirection = direction.turn(TurnDirection.Right)
            val result = newDirection.turn(TurnDirection.Left)
            assert(direction != newDirection)
            assert(result == direction)
        }
    }
}
