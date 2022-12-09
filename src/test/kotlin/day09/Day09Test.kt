package day09

import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import utils.gridmap.Point

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

    @Test
    fun `part1 should process the moves and return the number of positions the tail touches`() {
        assert(part1(sampleInput) == 13)
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

            assert(newState.headPosition == Point(4,0))
            assert(newState.tailPosition == Point(3,0))

            assert(tailPositions.size == 4)
            assert(Point(0,0) in tailPositions)
            assert(Point(1,0) in tailPositions)
            assert(Point(2,0) in tailPositions)
            assert(Point(3,0) in tailPositions)
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