package day09

import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import utils.Point

class Day09Test {
    private val sampleInput = """
        R 4
        U 4
        L 3
        D 1
        R 4
        D 1
        L 5
        R 2
    """.trimIndent().lines()

    private val largerSampleInput = """
        R 5
        U 8
        L 8
        D 3
        R 17
        D 10
        L 25
        U 20
    """.trimIndent().lines()

    @Test
    fun `part1 should process the moves for a rope with 2 knots and return the number of positions the tail knot touches`() {
        assert(part1(sampleInput) == 13)
    }

    @Test
    fun `part2 should process the moves for a rope with 10 knots and return the number of positions the tail knot touches`() {
        assert(part2(largerSampleInput) == 36)
    }

    @Nested
    inner class DirectionTest {
        @Test
        fun `fromCode should return a direction given a valid code`() {
            assert(Direction.fromCode('R') == Direction.Right)
            assert(Direction.fromCode('L') == Direction.Left)
            assert(Direction.fromCode('U') == Direction.Up)
            assert(Direction.fromCode('D') == Direction.Down)
        }

        @Test
        fun `fromCode should throw an exception given an invalid code`() {
            val exception = assertThrows<IllegalArgumentException> { Direction.fromCode('X') }
            assert(exception.message == "Invalid code 'X'")
        }
    }

    @Nested
    inner class MoveTest {
      @Test
      fun `constructor should take a valid line and return a valid move`() {
          val input = sampleInput.first()
          val move = Move.parse(input)

          assert(move.direction == Direction.Right)
          assert(move.spaces == 4)
      }
    }

    @Nested
    inner class RopeStateTest {
        @Test
        fun `processMove should return an updated RopeState and the positions the tail covered`() {
            val ropeState = RopeState(Point(0,0), Point(0,0))
            val move = Move(Direction.Right, 4)
            val (newState:RopeState, tailPositions:Set<Point>) = ropeState.processMove(move)

            assert(newState.knotPositions.first() == Point(4,0))
            assert(newState.knotPositions.last() == Point(3,0))

            assert(tailPositions.size == 4)
            assert(Point(0,0) in tailPositions)
            assert(Point(1,0) in tailPositions)
            assert(Point(2,0) in tailPositions)
            assert(Point(3,0) in tailPositions)
        }

        @Test
        fun `processMove should work as expected with multiple knots`() {
            val initialRopeState = RopeState(List(10) { Point(0,0) })
            var result = initialRopeState
            val visitedTailKnotPositions = mutableSetOf<Point>()
            largerSampleInput
                .map { Move.parse(it) }
                .forEach {
                    val (newState, tailKnotPositionsVisitedThisTurn) = result.processMove(it)
                    result = newState
                    visitedTailKnotPositions += tailKnotPositionsVisitedThisTurn
                }

            assert(result.knotPositions.size == 10)
            assert(result.knotPositions.first() == Point(-11,-15))
            assert(result.knotPositions[1] == result.knotPositions.first().south())
            assert(result.knotPositions[2] == result.knotPositions[1].south())
            assert(result.knotPositions[3] == result.knotPositions[2].south())
            assert(result.knotPositions[4] == result.knotPositions[3].south())
            assert(result.knotPositions[5] == result.knotPositions[4].south())
            assert(result.knotPositions[6] == result.knotPositions[5].south())
            assert(result.knotPositions[7] == result.knotPositions[6].south())
            assert(result.knotPositions[8] == result.knotPositions[7].south())
            assert(result.knotPositions[9] == result.knotPositions[8].south())

            assert (visitedTailKnotPositions.size == 36)
        }
    }

    @Nested
    inner class UpdateTailPositionTest {
        @Test
        fun `updateTailPosition should return the position unchanged when the head is on the same point as the current tail position`() {
            val currentTailPosition = Point(0,0)
            val newHeadPosition = Point(0,0)

            val result = currentTailPosition.updateTailPosition(newHeadPosition)
            assert(result == currentTailPosition)
        }

        @Test
        fun `updateTailPosition should return the position unchanged when the head is adjacent to the current tail position`() {
            val currentTailPosition = Point(0,0)
            val actions = listOf(
                Point::north, Point::south, Point::east, Point::west,
                Point::northEast, Point::northWest, Point::southEast, Point::southWest
            )

            actions.forEach { action ->
                val newHeadPosition = action.invoke(currentTailPosition)
                val result = currentTailPosition.updateTailPosition(newHeadPosition)
                assert(result == currentTailPosition)
            }
        }

        @Test
        fun `updateTailPosition should move the tail one step north when the head is two steps north`() {
            val currentTailPosition = Point(0,0)
            val newHeadPosition = currentTailPosition.north().north()

            val result = currentTailPosition.updateTailPosition(newHeadPosition)
            assert(result == currentTailPosition.north())
        }

        @Test
        fun `updateTailPosition should move the tail one step east when the head is two steps east`() {
            val currentTailPosition = Point(0,0)
            val newHeadPosition = currentTailPosition.east().east()

            val result = currentTailPosition.updateTailPosition(newHeadPosition)
            assert(result == currentTailPosition.east())
        }

        @Test
        fun `updateTailPosition should move the tail one step south when the head is two steps south`() {
            val currentTailPosition = Point(0,0)
            val newHeadPosition = currentTailPosition.south().south()

            val result = currentTailPosition.updateTailPosition(newHeadPosition)
            assert(result == currentTailPosition.south())
        }

        @Test
        fun `updateTailPosition should move the tail one step west when the head is two steps west`() {
            val currentTailPosition = Point(0,0)
            val newHeadPosition = currentTailPosition.west().west()

            val result = currentTailPosition.updateTailPosition(newHeadPosition)
            assert(result == currentTailPosition.west())
        }

        @Test
        fun `updateTailPosition should move the tail one step north east when the head is in the north east quadrant`() {
            val currentTailPosition = Point(0,0)
            val newHeadPosition = currentTailPosition.north().north().east()

            val result = currentTailPosition.updateTailPosition(newHeadPosition)
            assert(result == currentTailPosition.northEast())
        }

        @Test
        fun `updateTailPosition should move the tail one step south east when the head is in the south east quadrant`() {
            val currentTailPosition = Point(0,0)
            val newHeadPosition = currentTailPosition.south().east().east()

            val result = currentTailPosition.updateTailPosition(newHeadPosition)
            assert(result == currentTailPosition.southEast())
        }

        @Test
        fun `updateTailPosition should move the tail one step north west when the head is in the north west quadrant`() {
            val currentTailPosition = Point(0,0)
            val newHeadPosition = currentTailPosition.north().west().west()

            val result = currentTailPosition.updateTailPosition(newHeadPosition)
            assert(result == currentTailPosition.northWest())
        }

        @Test
        fun `updateTailPosition should move the tail one step south west when the head is in the south west quadrant`() {
            val currentTailPosition = Point(0,0)
            val newHeadPosition = currentTailPosition.south().south().west()

            val result = currentTailPosition.updateTailPosition(newHeadPosition)
            assert(result == currentTailPosition.southWest())
        }
    }
}