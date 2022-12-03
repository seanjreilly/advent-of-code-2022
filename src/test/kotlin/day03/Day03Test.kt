package day03

import org.junit.jupiter.api.Test

class Day03Test {
    private val sampleInput = """
        vJrwpWtwJgWrhcsFMMfFFhFp
        jqHRNqRjqzjGDLGLrsFMfFZSrLrFZsSL
        PmmdzqPrVvPwwTWBwg
        wMqvLMZHhHMvwLHjbvcjnnSBnvTQFn
        ttgJtRGJQctTZtZT
        CrZsJsPPZsGzwwsLwLmpwMDw
    """.trimIndent().lines()

    @Test
    fun `part1 should find the character that is in the first half and the second half of each line and return the sum of those priorities`() {
        assert(part1(sampleInput) == 157)
    }

    @Test
    fun `part2 should find the common character for each chunk of three lines and return the sum of those priorities`() {
        assert(part2(sampleInput) == 70)
    }

    @Test
    fun `findMatchingCharacter should return the character that is in all three lines`() {
        assert(findCommonCharacter("vJrwpWtwJgWrhcsFMMfFFhFp", "jqHRNqRjqzjGDLGLrsFMfFZSrLrFZsSL", "PmmdzqPrVvPwwTWBwg") == 'r')
        assert(findCommonCharacter("wMqvLMZHhHMvwLHjbvcjnnSBnvTQFn", "ttgJtRGJQctTZtZT", "CrZsJsPPZsGzwwsLwLmpwMDw") == 'Z')
    }

    @Test
    fun `priority should return scores of 1 to 26 for lowercase letters`() {
        val chars = 'a'..'z'
        chars.forEachIndexed { index, char ->
            assert(priority(char) == index + 1)
        }

        //check beginning and end to make sure the above loop is right
        assert(priority('a') == 1)
        assert(priority('z') == 26)
    }

    @Test
    fun `priority should return scores of 27 to 52 for uppercase letters`() {
        val chars = 'A'..'Z'
        chars.forEachIndexed { index, char ->
            assert(priority(char) == index + 27)
        }

        //check beginning and end to make sure the above loop is right
        assert(priority('A') == 27)
        assert(priority('Z') == 52)
    }

    @Test
    fun `priority should return expected scores for characters specifically documented in the instructions`() {
        assert(priority('p') == 16)
        assert(priority('L') == 38)
        assert(priority('P') == 42)
        assert(priority('v') == 22)
        assert(priority('t') == 20)
        assert(priority('s') == 19)
    }
}