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
    fun `part1 should mix the input and return the sum of the 1000th, 2000th, and 3000th elements after the 0`() {
        assert(part1(sampleInput) == 4L + -3L + 2L)
    }

    @Test
    fun `part2 should multiply each element of the input by the decryption key, mix it 10 times and return the sum of the 1000th, 2000th, and 3000th elements after the 0`() {
        assert(part2(sampleInput) == 811589153L + 2434767459L + -1623178306L)
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

    @Test
    fun `mix should mix items for up to 10 rounds`() {
        val list = parse(sampleInput).map { it * DECRYPTION_KEY }

        assert(list.mix(1) circularEquals listOf(0, -2434767459, 3246356612, -1623178306, 2434767459, 1623178306, 811589153))
        assert(list.mix(2) circularEquals listOf(0, 2434767459, 1623178306, 3246356612, -2434767459, -1623178306, 811589153))
        assert(list.mix(3) circularEquals listOf(0, 811589153, 2434767459, 3246356612, 1623178306, -1623178306, -2434767459))
        assert(list.mix(4) circularEquals listOf(0, 1623178306, -2434767459, 811589153, 2434767459, 3246356612, -1623178306))
        assert(list.mix(5) circularEquals listOf(0, 811589153, -1623178306, 1623178306, -2434767459, 3246356612, 2434767459))
        assert(list.mix(6) circularEquals listOf(0, 811589153, -1623178306, 3246356612, -2434767459, 1623178306, 2434767459))
        assert(list.mix(7) circularEquals listOf(0, -2434767459, 2434767459, 1623178306, -1623178306, 811589153, 3246356612))
        assert(list.mix(8) circularEquals listOf(0, 1623178306, 3246356612, 811589153, -2434767459, 2434767459, -1623178306))
        assert(list.mix(9) circularEquals listOf(0, 811589153, 1623178306, -2434767459, 3246356612, 2434767459, -1623178306))
        assert(list.mix(10) circularEquals listOf(0, -2434767459, 1623178306, 3246356612, -1623178306, 2434767459, 811589153))
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
