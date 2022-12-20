package day20

import org.junit.jupiter.api.Test

class Day20Test {
    private val sampleInput = """
        1
        2
        -3
        3
        -2
        0
        4
    """.trimIndent().lines()

    @Test
    fun `part1 should mix the input and return the sum of the 1000th, 2000th, and 3000th symbols after the 0`() {
        assert(part1(sampleInput) == 4L + -3L + 2L)
    }

    @Test
    fun `parse should return a list of Longs`() {
        val result = parse(sampleInput)

        val expectedResult = listOf<Long>(1, 2, -3, 3, -2, 0, 4)
        assert(expectedResult == result)
    }

    @Test
    fun `mix should move each list item a number of positions equal to its value, wrapping around as necessary`() {
        val list = parse(sampleInput)
        val expectedResult = listOf<Long>(-2, 1, 2, -3, 4, 0, 3)

        val result = list.mix()

        assert(result == expectedResult)
    }

    @Test
    fun `mix should work with duplicate entries`() {
        // should move as follows:
        // 2 1 1 0
        // 1 1 2 0
        // 1 2 1 0
        val list = listOf<Long>(1, 2, 1, 0)

        val result = list.mix()
        assert(result == listOf<Long>(1, 2, 1, 0))
    }
}