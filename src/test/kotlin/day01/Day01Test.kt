package day01

import org.junit.jupiter.api.Test

class Day01Test {
    val sampleInput = """
        1000
        2000
        3000

        4000

        5000
        6000

        7000
        8000
        9000

        10000
    """.trimIndent().lines()

    @Test
    fun `calculateSums should split on blank lines, and return the sum of each chunk`() {
        val result: List<Int> = calculateSums(sampleInput)
        assert(result == listOf(6000, 4000, 11000, 24000, 10000))
    }

    @Test
    fun `part 1 should split on blank lines, sum each chunk, and return the largest sum`() {
        val result = part1(sampleInput)

        assert (result == 24000)
    }

    @Test
    fun `part2 should split on blank lines, sum each chunk, and return the sum of the largest three chunks`() {
        val result = part2(sampleInput)

        assert (result == 45000)
    }
}