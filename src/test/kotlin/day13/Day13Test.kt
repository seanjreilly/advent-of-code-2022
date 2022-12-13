package day13

import org.junit.jupiter.api.Test
import utils.stack.Stack
import utils.stack.pop
import utils.stack.push

class Day13Test {
    private val sampleInput = """
        [1,1,3,1,1]
        [1,1,5,1,1]

        [[1],[2,3,4]]
        [[1],4]

        [9]
        [[8,7,6]]

        [[4,4],4,4]
        [[4,4],4,4,4]

        [7,7,7,7]
        [7,7,7]

        []
        [3]

        [[[]]]
        [[]]

        [1,[2,[3,[4,[5,6,7]]]],8,9]
        [1,[2,[3,[4,[5,6,0]]]],8,9]
    """.trimIndent().lines()

    @Test
    fun `parse should return a singleton list given an array of one int`() {
        assert(parse("[9]") == listOf(9))
    }

    @Test
    fun `parse should return a list of ints given simple array`() {
        assert(parse("[1,2,3]") == listOf(1, 2, 3))
    }

    @Test
    fun `parse should return an empty list given an empty array`() {
        assert(parse("[]") == emptyList<Any>())
    }

    @Test
    fun `parse should handle nested empty lists`() {
     assert(parse("[[]]") == listOf(emptyList<Any>()))
    }

    @Test
    fun `parse should handle nested lists`() {
        assert(parse("[[1],[2,3,4]]") == listOf(listOf(1), listOf(2,3,4)))
    }

    @Test
    fun `parse should handle lists that start with primitives but contain other lists`() {
        assert(parse("[1,2,3,[4]]") == listOf(1,2,3, listOf(4)))
    }
}