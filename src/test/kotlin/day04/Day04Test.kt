package day04

import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

class Day04Test {

    private val sampleInput = """
        2-4,6-8
        2-3,4-5
        5-7,7-9
        2-8,3-7
        6-6,4-6
        2-6,4-8
    """.trimIndent().lines()

    @Test
    fun `part1 should return the count of lines where one range completely contains the other`() {
        assert(part1(sampleInput) == 2)
    }

    @Test
    fun `parseLine should return a Pair of IntRange objects`() {
        val (first: IntRange, second: IntRange) = parseLine(sampleInput.first())

        assert(first == 2..4)
        assert(second == 6..8)
    }

    @Test
    fun `parseLine should work with multi-digit numbers`() {
        val line = "123-234,456-567"
        val (first, second) = parseLine(line)

        assert(first == 123..234)
        assert(second == 456..567)
    }

    @Nested
    inner class IntRangeExtensionTest {
        @Test
        fun `IntRange dot contains should return true given an IntRange completely contained within the range`() {
            val thisRange = 2..8
            val otherRange = 3..7

            assert(thisRange.contains(otherRange))
        }

        @Test
        fun `IntRange dot contains should return true given an IntRange with the same start and end points`() {
            val thisRange = 2..8
            val otherRange = 2..8

            assert(thisRange.contains(otherRange))
        }

        @Test
        fun `IntRange dot contains should return true given itself`() {
            val thisRange = 2..8

            assert(thisRange.contains(thisRange))
        }

        @Test
        fun `IntRange dot contains should return false given an IntRange with a start value not contained within the range`() {
            val thisRange = 2..8
            val otherRange = 1..7

            assert(!thisRange.contains(otherRange))
        }

        @Test
        fun `IntRange dot contains should return false given an IntRange with an end value not contained within the range`() {
            val thisRange = 2..8
            val otherRange = 3..9

            assert(!thisRange.contains(otherRange))
        }
    }
}