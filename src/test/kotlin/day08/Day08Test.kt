package day08

import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import utils.Point

class Day08Test {
    private val sampleInput = """
        30373
        25512
        65332
        33549
        35390
    """.trimIndent().lines()

    @Test
    fun `part1 should return the number of visible trees in the grid`() {
        assert(part1(sampleInput) == 21)
    }

    @Test
    fun `part2 should return the maximum senic score for all trees in the grid`() {
        assert(part2(sampleInput) == 8)
    }

    @Nested
    inner class ForestMapTest {

        @Test
        fun `ForestMap constructor should parse input`() {
            val result = ForestMap(sampleInput)

            assert(result.height == 5)
            assert(result.width == 5)
            assert(result[Point(0, 0)] == 3)
            assert(result[Point(1, 0)] == 0)
            assert(result[Point(2, 0)] == 3)
            assert(result[Point(3, 0)] == 7)
            assert(result[Point(4, 0)] == 3)

            assert(result[Point(0, 1)] == 2)
            assert(result[Point(1, 1)] == 5)
            assert(result[Point(2, 1)] == 5)
            assert(result[Point(3, 1)] == 1)
            assert(result[Point(4, 1)] == 2)

            assert(result[Point(0, 2)] == 6)
            assert(result[Point(1, 2)] == 5)
            assert(result[Point(2, 2)] == 3)
            assert(result[Point(3, 2)] == 3)
            assert(result[Point(4, 2)] == 2)

            assert(result[Point(0, 3)] == 3)
            assert(result[Point(1, 3)] == 3)
            assert(result[Point(2, 3)] == 5)
            assert(result[Point(3, 3)] == 4)
            assert(result[Point(4, 3)] == 9)

            assert(result[Point(0, 4)] == 3)
            assert(result[Point(1, 4)] == 5)
            assert(result[Point(2, 4)] == 3)
            assert(result[Point(3, 4)] == 9)
            assert(result[Point(4, 4)] == 0)
        }

        @Test
        fun `findVisibleTrees should return a set of points that have visible trees`() {
            val visibleTrees: Set<Point> = ForestMap(sampleInput).findVisibleTrees()
            assert(visibleTrees.isNotEmpty())
        }

        @Test
        fun `findVisibleTrees should include all points on the perimeter`() {
            val forestMap = ForestMap(sampleInput)
            val visibleTrees = forestMap.findVisibleTrees()

            val northEdgePoints = (0 until forestMap.width).map { Point(it, 0) }.toSet()
            assert(visibleTrees.containsAll(northEdgePoints))

            val eastEdgePoints = (0 until forestMap.height).map { Point(forestMap.width - 1, it) }.toSet()
            assert(visibleTrees.containsAll(eastEdgePoints))

            val southEdgePoints = (0 until forestMap.width).map { Point(it, forestMap.height - 1) }.toSet()
            assert(visibleTrees.containsAll(southEdgePoints))

            val westEdgePoints = (0 until forestMap.height).map { Point(0, it) }.toSet()
            assert(visibleTrees.containsAll(westEdgePoints))
        }

        @Test
        fun `findVisibleTrees should include all points for trees that are taller than any trees between them and an edge`() {
            val visibleTrees = ForestMap(sampleInput).findVisibleTrees()

            assert(Point(1, 1) in visibleTrees)
            assert(Point(2, 1) in visibleTrees)
            assert(Point(1, 2) in visibleTrees)
            assert(Point(3, 2) in visibleTrees)
            assert(Point(2, 3) in visibleTrees)
        }

        @Test
        fun `findVisibleTrees should not include any points for trees with a taller tree between them and every edge`() {
            val visibleTrees = ForestMap(sampleInput).findVisibleTrees()

            assert(Point(3,1) !in visibleTrees)
            assert(Point(2,2) !in visibleTrees)
            assert(Point(1,3) !in visibleTrees)
            assert(Point(3,3) !in visibleTrees)
        }

         @Test
         fun `getScenicScore should return the product of the viewing distances in each direction`() {
             val forest = ForestMap(sampleInput)

             val score = forest.getScenicScore(Point(2,1))

             val expectedNorthViewingDistance = 1
             val expectedWestViewingDistance = 1
             val expectedSouthViewingDistance = 2
             val expectedEastViewingDistance = 2

             //score is the product of the viewing distance in each direction
             assert(score == expectedNorthViewingDistance * expectedWestViewingDistance * expectedSouthViewingDistance * expectedEastViewingDistance)

             assert(forest.getScenicScore(Point(2,3)) == 8)
         }
    }
}