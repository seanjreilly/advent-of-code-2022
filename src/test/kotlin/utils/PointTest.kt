package utils

import org.junit.jupiter.api.Test

class PointTest {
    @Test
    fun `manhattanDistance should return the manhattan distance between two points`() {
        val pointA = Point(0,0)
        val pointB = Point(3,5)

        val manhattanDistance = pointA.manhattanDistance(pointB)

        assert(manhattanDistance == 8)
    }

    @Test
    fun `manhattanDistance should be reflexive`() {
        val pointA = Point(0,0)
        val pointB = Point(3,5)

        assert(pointA.manhattanDistance(pointB) == pointB.manhattanDistance(pointA))
    }

    @Test
    fun `pointsWithManhattanDistance should return all Points with a given manhattan distance from this point`() {
        val point = Point(0,0)
        val manhattanDistance = 3

        val expectedResult = setOf(
            //this is a set, so just duplicate some squares for obviousness and it will all work out
            Point(0, 3),
            Point(1, 2),
            Point(2,1),
            Point(3,0),
            Point(0, -3),
            Point(1, -2),
            Point(2, -1),
            Point(3,-0),
            Point(-0,3),
            Point(-1, 2),
            Point(-2, 1),
            Point(-3, 0),
            Point(-0, -3),
            Point(-1, -2),
            Point(-2, -1),
            Point(-3, -0),
        )

        val result = point.pointsWithManhattanDistance(manhattanDistance).toSet()
        expectedResult.forEach {
            assert(it in result)
        }
        assert(expectedResult.size == result.size)
    }
}