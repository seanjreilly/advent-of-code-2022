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
        assert(part1(sampleInput) == 31)
    }

    @Test
    fun `part2 should run Djikstra's and return the shortest number of steps to endPoint from _any_ point with height zero`() {
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
        fun `findShortestNumberOfStepsToEndPoint should use Djikstra's algorithm to return the shortest path from the designated startPoint to endPoint that only moves to points at most one higher than the current point`() {
            val map = HeightMap.parse(sampleInput)

            val result = map.findShortestNumberOfStepsToEndPoint(listOf(map.startPoint))
            assert(result == 31) //shortest possible number of steps from the example text
        }

        @Test
        fun `findShortestNumberOfStepsToEndPoint should use Djikstra's to return the shortest path from _any_ square of height zero to endPoint`() {
            val map = HeightMap.parse(sampleInput)

            assert(map.findShortestNumberOfStepsToEndPoint(map.filter { map[it] == 0 }) == 29)
        }

        @Test
        fun `findShortestNumberOfStepsToEndPoint should gracefully handle squares of height zero that cannot reach endPoint`() {
            // the first a and the start square can reach endPoint in 25 and 26 moves respectively,
            // but the last two as cannot climb the cliff and reach endPoint
            // (two because an unreachable point next to another unreachable point triggers the overflow bug I found)

            val input = """
                Sabcde
                kjihgf
                lmnopq
                wvutsr
                xyzEaa
            """.trimIndent().lines()

            val map = HeightMap.parse(input)

            val result = map.findShortestNumberOfStepsToEndPoint(map.filter { map[it] == 0 })
            assert(result == 26)
        }
    }
}