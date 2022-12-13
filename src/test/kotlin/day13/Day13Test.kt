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
    fun `part1 should return the sum of the (one-based) indicies that are in the correct order`() {
        assert(part1(sampleInput) == 13)
    }

    @Test
    fun `part2 should add the divider packets to the input, sort the input, and return the product of the indexes of the divder packets in the sorted results`() {
        assert(part2(sampleInput) == 10 * 14)
    }

    @Test
    fun `sortPackets should sort the packets so that they are in correct order`() {
        val expectedResult = """
            []
            [[]]
            [[[]]]
            [1,1,3,1,1]
            [1,1,5,1,1]
            [[1],[2,3,4]]
            [1,[2,[3,[4,[5,6,0]]]],8,9]
            [1,[2,[3,[4,[5,6,7]]]],8,9]
            [[1],4]
            [[2]]
            [3]
            [[4,4],4,4]
            [[4,4],4,4,4]
            [[6]]
            [7,7,7]
            [7,7,7,7]
            [[8,7,6]]
            [9]
        """.trimIndent().lines()
            .map { parse(it) }

        val input = (sampleInput.filter { it.isNotEmpty() } + dividerPackets).map { parse(it) }

        val result = sortPackets(input)

        assert(result == expectedResult)
    }

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
    
    @Test
    fun `checkOrder should return CorrectOrder when both parameters are ints and the left int is less than the right int`() {
        assert(checkOrder(1,2) == CheckResult.CorrectOrder)
    }

    @Test
    fun `checkOrder should return WrongOrder when both parameters are ints and the left int is greater than the right int`() {
        assert(checkOrder(2,1) == CheckResult.WrongOrder)
    }
    
    @Test
    fun `checkOrder should return ContinueChecking when both parameters are equal ints`() {
        assert(checkOrder(2,2) == CheckResult.ContinueChecking)
    }
    
    @Test
    fun `checkOrder should return CorrectOrder when both parameters are lists and a leading item in the left list is in the correct order compared to the right list item`() {
        assert(checkOrder(listOf(1), listOf(2)) == CheckResult.CorrectOrder)
        assert(checkOrder(listOf(1,1), listOf(1,2)) == CheckResult.CorrectOrder)
        assert(checkOrder(parse("[1,1,3,1,1]"), parse("[1,1,5,1,1]")) == CheckResult.CorrectOrder)
    }

    @Test
    fun `checkOrder should return WrongOrder when both parameters are lists and a leading item in the left list is in the wrong order compared to the right list item`() {
        assert(checkOrder(listOf(2), listOf(1)) == CheckResult.WrongOrder)
        assert(checkOrder(listOf(1,2), listOf(1,1)) == CheckResult.WrongOrder)
        assert(checkOrder(parse("[1,1,5,1,1]"), parse("[1,1,3,1,1]")) == CheckResult.WrongOrder)
    }

    @Test
    fun `checkOrder should return CorrectOrder when both parameters are lists and the left list runs out of items first`() {
        assert(checkOrder(emptyList(), listOf(1)) == CheckResult.CorrectOrder)
        assert(checkOrder(listOf(1), listOf(1, 1)) == CheckResult.CorrectOrder)
    }

    @Test
    fun `checkOrder should return WrongOrder when both parameters are lists and the right list runs out of items first`() {
        assert(checkOrder(listOf(1), emptyList()) == CheckResult.WrongOrder)
        assert(checkOrder(listOf(1, 1), listOf(1)) == CheckResult.WrongOrder)
    }

    @Test
    fun `checkOrder should return ContinueChecking when both parameters are equivalent lists`() {
        assert(checkOrder(listOf(1), listOf(1)) == CheckResult.ContinueChecking)
        assert(checkOrder(listOf(1, 1), listOf(1,1)) == CheckResult.ContinueChecking)
    }

    @Test
    fun `checkOrder should promote an int to list when compared to a list`() {
        assert(checkOrder(listOf(1), 1) == checkOrder(listOf(1), listOf(1)))
        assert(checkOrder(1, listOf(1)) == checkOrder(listOf(1), listOf(1)))
    }

    @Test
    fun `checkOrder should return CorrectOrder for the first chunk of input`() {
        val chunks = sampleInput.windowed(3, 3)
        val chunk = chunks[0]
        val (left, right) = chunk
        assert(checkOrder(parse(left), parse(right)) == CheckResult.CorrectOrder)
    }

    @Test
    fun `checkOrder should return CorrectOrder for the second chunk of input`() {
        val chunks = sampleInput.windowed(3, 3)
        val chunk = chunks[1]
        val (left, right) = chunk
        assert(checkOrder(parse(left), parse(right)) == CheckResult.CorrectOrder)
    }

    @Test
    fun `checkOrder should return WrongOrder for the third chunk of input`() {
        val chunks = sampleInput.windowed(3, 3)
        val chunk = chunks[2]
        val (left, right) = chunk
        assert(checkOrder(parse(left), parse(right)) == CheckResult.WrongOrder)
    }

    @Test
    fun `checkOrder should return CorrectOrder for the fourth chunk of input`() {
        val chunks = sampleInput.windowed(3, 3)
        val chunk = chunks[3]
        val (left, right) = chunk
        assert(checkOrder(parse(left), parse(right)) == CheckResult.CorrectOrder)
    }

    @Test
    fun `checkOrder should return WrongOrder for the fifth chunk of input`() {
        val chunks = sampleInput.windowed(3, 3)
        val chunk = chunks[4]
        val (left, right) = chunk
        assert(checkOrder(parse(left), parse(right)) == CheckResult.WrongOrder)
    }

    @Test
    fun `checkOrder should return CorrectOrder for the sixth chunk of input`() {
        val chunks = sampleInput.windowed(3, 3)
        val chunk = chunks[5]
        val (left, right) = chunk
        assert(checkOrder(parse(left), parse(right)) == CheckResult.CorrectOrder)
    }

    @Test
    fun `checkOrder should return WrongOrder for the seventh chunk of input`() {
        val chunks = sampleInput.windowed(3, 3)
        val chunk = chunks[6]
        val (left, right) = chunk
        assert(checkOrder(parse(left), parse(right)) == CheckResult.WrongOrder)
    }

    @Test
    fun `checkOrder should return WrongOrder for the eighth chunk of input`() {
        val chunks = sampleInput.windowed(3, 3, true)
        val chunk = chunks[7]
        val (left, right) = chunk
        assert(checkOrder(parse(left), parse(right)) == CheckResult.WrongOrder)
    }
}