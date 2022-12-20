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
        val expectedResult = listOf<Long>(1, 2, -3, 4, 0, 3, -2)

        val result = list.mix()

        assert(result circularEquals expectedResult)
    }

    @Test
    fun `mix should work with duplicate entries`() {
        // should move as follows:
        // 2 1 1 0
        // 1 1 2 0
        // 1 2 1 0
        val list = listOf<Long>(1, 2, 1, 0)

        val result = list.mix()
        assert(result circularEquals listOf(1, 2, 1, 0))
    }
}

/**
 * Checks that two circular lists are equal.
 * A circular function could offset the list elements by one or more,
 * which breaks regular equality but should still work for the purposes
 * of these tests.
 */
infix fun List<Long>.circularEquals(other: List<Long>) : Boolean {
    if (this.size != other.size) {
        return false
    }
    if (this == other) {
        return true
    }

    val mutableOther = other.toMutableList()
    repeat(size - 1) {
        val element = mutableOther.removeFirst()
        mutableOther.add(element)
        if (this == mutableOther) {
            return true
        }
    }
    return false
}
