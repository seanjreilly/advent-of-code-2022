package day18

import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

class Day18Test {
    private val sampleInput = """
        2,2,2
        1,2,2
        3,2,2
        2,1,2
        2,3,2
        2,2,1
        2,2,3
        2,2,4
        2,2,6
        1,2,5
        3,2,5
        2,1,5
        2,3,5
    """.trimIndent().lines()

    @Test
    fun `part1 should count the unconnected sides for the input`() {
        assert(part1(sampleInput) == 64)
    }

    @Test
    fun `parse should return a list of Point3D instances`() {
        val points = parse(sampleInput)

        val expectedResults = setOf(
            Point3D(2,2,2),
            Point3D(1,2,2),
            Point3D(3,2,2),
            Point3D(2,1,2),
            Point3D(2,3,2),
            Point3D(2,2,1),
            Point3D(2,2,3),
            Point3D(2,2,4),
            Point3D(2,2,6),
            Point3D(1,2,5),
            Point3D(3,2,5),
            Point3D(2,1,5),
            Point3D(2,3,5),
        )

        assert (expectedResults == points)
    }

    @Test
    fun `countUnconnectedSides should return 6 for a single cube`() {
        val points = setOf(Point3D(0,0,0))

        assert(countUnconnectedSides(points) == 6)
    }

    @Test
    fun `countUnconnectedSides should return 12 for a two unconnected cubes`() {
        val points = setOf(Point3D(0,0,0), Point3D(0, 10, 0))

        assert(countUnconnectedSides(points) == 12)
    }

    @Test
    fun `countUnconnectedSides should return 10 for 2 adjacent cubes`() {
        val points = setOf(Point3D(0,0,0), Point3D(0, 1, 0))
        assert(countUnconnectedSides(points) == 10)
    }

    @Test
    fun `countUnconnectedSides should work for sample input`() {
        val points = parse(sampleInput)
        assert(countUnconnectedSides(points) == 64)
    }

    @Nested
    inner class Point3DTest {
        @Test
        fun `neighbours should return the 6 points adjacent to this point`() {
            val point = Point3D(0,0,0)
            val neighbours = point.neighbours()

            assert(Point3D(1,0,0) in neighbours)
            assert(Point3D(-1,0,0) in neighbours)
            assert(Point3D(0,1,0) in neighbours)
            assert(Point3D(0,-1,0) in neighbours)
            assert(Point3D(0,0,1) in neighbours)
            assert(Point3D(0,0,-1) in neighbours)
            assert(neighbours.size == 6)
        }
    }
}