package day12

import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import utils.gridmap.Point

class Day12Test {
    private val sampleInput = """
        Sabqponm
        abcryxxl
        accszExk
        acctuvwj
        abdefghi
    """.trimIndent().lines()

    @Test
    fun `part1 should return the number of steps in the shortest path from startPoint to endPoint`() {
        val map = HeightMap.parse(sampleInput)
        val expectedNumberOfSteps = map.findShortestPathToEndPointFrom(map.startPoint).size - 1 //start point doesn't count as a step
        assert(part1(sampleInput) == expectedNumberOfSteps)
    }

    @Test
    fun `part2 should run Djikstra's for _every_ point with height zero and return the shortest number of steps`() {
        assert(part2(sampleInput) == 29)
    }

    @Nested
    inner class HeightMapTest {
        @Test
        fun `parse should return a HeightMap with heights between 0 and 25`() {
            val result = HeightMap.parse(sampleInput)

            assert(result.width == sampleInput.first().length)
            assert(result.height == sampleInput.size)
            result.forEach { point -> assert(result[point] in 0..25) }
        }

        @Test
        fun `startPoint should be a point with height 0`() {
            val map = HeightMap.parse(sampleInput)

            assert(map.startPoint == Point(0, 0))
            assert(map[map.startPoint] == 0)
        }

        @Test
        fun `endPoint should be a point with height 25`() {
            val map = HeightMap.parse(sampleInput)

            assert(map.endPoint == Point(5, 2))
            assert(map[map.endPoint] == 25)
        }

        @Test
        fun `findShortestPathToEndPointFrom should use Djikstra's algorithm to return the shortest path from the designated startPoint to endPoint that only moves to points at most one higher than the current point`() {
            val map = HeightMap.parse(sampleInput)

            val result:Path = map.findShortestPathToEndPointFrom(map.startPoint)
            assert(result.size == 32) //shortest possible path from the example text
            assert(result.first() == map.startPoint)
            assert(result.last() == map.endPoint)

            //check how each node in the path compares to the next one
            result
                .windowed(2, 1)
                .map { Pair(it.first(), it.last()) }
                .forEach { (thisPoint, nextPoint) ->
                    assert(nextPoint in thisPoint.getCardinalNeighbours()) { "path must move between neighbours" }
                    assert(map[thisPoint] + 1 >= map[nextPoint]) { "path must never climb 2 or more" }
                }
        }
    }
}