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
    fun `part2 should fill in any interior points, and then count the unconnected sides for the input`() {
        assert(part2(sampleInput) == 58)
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

    @Test
    fun `findInteriorPoints should return interior points from a sealed structure`() {
        /*
        // looks like this, with a solid floor at z=0
        // and a ceiling at z=3
            XXXX
            X..X
            X..X
            XXXX
         */
        val boxFloor = (0..4).flatMap { x ->
            (0..4).map { y -> Point3D(x,y,0)}
        }.toSet()
        val hollowExteriorLevel1 = setOf(
            Point3D(0,0,1),
            Point3D(1,0,1),
            Point3D(2,0,1),
            Point3D(3,0,1),
            Point3D(0,1,1),
            Point3D(3,1,1),
            Point3D(0,2,1),
            Point3D(3,2,1),
            Point3D(0,3,1),
            Point3D(1,3,1),
            Point3D(2,3,1),
            Point3D(3,3,1),
        )
        val hollowExteriorLevel2 = hollowExteriorLevel1.map { (x,y, _) -> Point3D(x,y,2) }.toSet()
        val boxCeiling = boxFloor.map { (x,y, _) -> Point3D(x,y,3) }.toSet()
        val points = boxFloor + hollowExteriorLevel1 + hollowExteriorLevel2 + boxCeiling

        val expectedInteriorPoints = setOf(
            Point3D(1,1,1),
            Point3D(2,1,1),
            Point3D(1,2,1),
            Point3D(2,2,1),
            Point3D(1,1,2),
            Point3D(2,1,2),
            Point3D(1,2,2),
            Point3D(2,2,2),
        )

        val interiorPoints = findInteriorPoints(points)

        assert (expectedInteriorPoints == interiorPoints)
    }

    @Test
    fun `findInteriorPoints should return an empty set given a structure with a hole`() {
        /*
        // looks like this, with a solid floor at z=0
        // and a ceiling at z=3
            XXXX
            X..X
            X..X
            XXXX
         */
        val boxFloor = (0..4).flatMap { x ->
            (0..4).map { y -> Point3D(x,y,0)}
        }.toSet()
        val hollowExteriorLevel1 = setOf(
            Point3D(0,0,1),
            Point3D(1,0,1),
            Point3D(2,0,1),
            Point3D(3,0,1),
            Point3D(0,1,1),
            Point3D(3,1,1),
            Point3D(0,2,1),
            Point3D(3,2,1),
            Point3D(0,3,1),
            Point3D(1,3,1),
            Point3D(2,3,1),
            Point3D(3,3,1),
        )
        val hollowExteriorLevel2 = hollowExteriorLevel1.map { (x,y, _) -> Point3D(x,y,2) }.toSet()
        val boxCeiling = boxFloor.map { (x,y, _) -> Point3D(x,y,3) }.toSet()
        val missingCeilingPiece = Point3D(2,2,3)
        val points = boxFloor + hollowExteriorLevel1 + hollowExteriorLevel2 + (boxCeiling - missingCeilingPiece)

        val interiorPoints = findInteriorPoints(points)

        assert (interiorPoints == emptySet<Point3D>())
    }

    @Test
    fun `findInteriorPoints should return the specified point given the provided example`() {
        val points = parse(sampleInput)
        val result = findInteriorPoints(points)

        assert(result == setOf(Point3D(2,2,5)))
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