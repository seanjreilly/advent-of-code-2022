package day14

import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import utils.gridmap.Point

class Day14Test {

    private val sampleInput = """
    498,4 -> 498,6 -> 496,6
    503,4 -> 502,4 -> 502,9 -> 494,9
    """.trimIndent().lines()

    @Test
    fun `part1 should construct a cave, simulate sand and return the number of grains that came to rest`() {
        assert(part1(sampleInput) == 24)
    }

    @Nested
    inner class CaveTest {
        @Test
        fun `constructor should return a Cave instance with the appropriate squares filled in`() {
            val cave = Cave(sampleInput)

            assert(Point(496,6) in cave.blockedSquares)
            assert(Point(497,6) in cave.blockedSquares)
            assert(Point(498,6) in cave.blockedSquares)
            assert(Point(498,5) in cave.blockedSquares)
            assert(Point(498,4) in cave.blockedSquares)

            assert(Point(494,9) in cave.blockedSquares)
            assert(Point(495,9) in cave.blockedSquares)
            assert(Point(496,9) in cave.blockedSquares)
            assert(Point(497,9) in cave.blockedSquares)
            assert(Point(498,9) in cave.blockedSquares)
            assert(Point(499,9) in cave.blockedSquares)
            assert(Point(500,9) in cave.blockedSquares)
            assert(Point(501,9) in cave.blockedSquares)
            assert(Point(502,9) in cave.blockedSquares)
            assert(Point(502,8) in cave.blockedSquares)
            assert(Point(502,7) in cave.blockedSquares)
            assert(Point(502,6) in cave.blockedSquares)
            assert(Point(502,5) in cave.blockedSquares)
            assert(Point(502,4) in cave.blockedSquares)
            assert(Point(503,4) in cave.blockedSquares)
            assert(cave.blockedSquares.size == 20)

            assert(cave.validX == 494..503)
            assert(cave.maxY == 9)
        }

        @Test
        fun `simulateSand should return the number of grains that come to rest before a grain falls to the abyss`() {
            val cave = Cave(sampleInput)

            val result = cave.simulateSand()
            assert(result == 24)
        }

        @Test
        fun `simulateSand should include grains of sand that have come to rest in blockedSquares`() {
            val cave = Cave(sampleInput)
            val initialBlockedSquares = cave.blockedSquares.size

            val result = cave.simulateSand()
            assert(cave.blockedSquares.size == initialBlockedSquares + result)

            assert(Point(500,2) in cave.blockedSquares)
            assert(Point(500,3) in cave.blockedSquares)
            assert(Point(500,4) in cave.blockedSquares)
            assert(Point(500,5) in cave.blockedSquares)
            assert(Point(500,6) in cave.blockedSquares)
            assert(Point(500,7) in cave.blockedSquares)
            assert(Point(500,8) in cave.blockedSquares)
            assert(Point(501,3) in cave.blockedSquares)
            assert(Point(501,4) in cave.blockedSquares)
            assert(Point(501,5) in cave.blockedSquares)
            assert(Point(501,6) in cave.blockedSquares)
            assert(Point(501,7) in cave.blockedSquares)
            assert(Point(501,8) in cave.blockedSquares)
            assert(Point(499,3) in cave.blockedSquares)
            assert(Point(499,4) in cave.blockedSquares)
            assert(Point(499,5) in cave.blockedSquares)
            assert(Point(499,6) in cave.blockedSquares)
            assert(Point(499,7) in cave.blockedSquares)
            assert(Point(499,8) in cave.blockedSquares)
            assert(Point(498,7) in cave.blockedSquares)
            assert(Point(498,8) in cave.blockedSquares)
            assert(Point(497,8) in cave.blockedSquares)
            assert(Point(495,8) in cave.blockedSquares)
            assert(Point(497,5) in cave.blockedSquares)
        }
    }
}