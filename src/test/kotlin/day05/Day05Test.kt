package day05

import org.junit.jupiter.api.Test
import utils.stack.*

class Day05Test {
    private val sampleInput = """
            [D]    
        [N] [C]    
        [Z] [M] [P]
         1   2   3 
        
        move 1 from 2 to 1
        move 3 from 1 to 3
        move 2 from 2 to 1
        move 1 from 1 to 2
    """.trimIndent().lines()

    @Test
    fun `part1 should parse the stacks and operations, run the rearrangement procedure and return the top crate from each stack`() {
        assert(part1(sampleInput) == "CMZ")
    }

    @Test
    fun `part2 should parse the stacks and operations, run the rearrangement procedure moving multiple crates without reversing them, and return the top create from each stack`() {
        assert(part2(sampleInput) == "MCD")
    }

    @Test
    fun `parseStacks should return an map of ArrayDeques, stopping on a blank line`() {
        val result: Map<Int, Stack<Char>> = parseStacks(sampleInput)

        assert(result.size == 3)
        assert(result[1] == Stack(listOf('Z','N')))
        assert(result[2] == Stack(listOf('M','C', 'D')))
        assert(result[3] == Stack(listOf('P')))
    }

    @Test
    fun `parseOperations should return a list of Operations, starting after the blank line`() {
        val result: List<Operation> = parseOperations(sampleInput)

        assert(result == listOf(
            Operation(1, 2, 1),
            Operation(3, 1, 3),
            Operation(2, 2, 1),
            Operation(1, 1, 2)
        ))
    }
}

