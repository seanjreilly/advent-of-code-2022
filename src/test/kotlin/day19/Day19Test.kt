package day19

import org.junit.jupiter.api.Test

class Day19Test {
    private val sampleInput = """
        Blueprint 1: Each ore robot costs 4 ore. Each clay robot costs 2 ore. Each obsidian robot costs 3 ore and 14 clay. Each geode robot costs 2 ore and 7 obsidian.
        Blueprint 2: Each ore robot costs 2 ore. Each clay robot costs 3 ore. Each obsidian robot costs 3 ore and 8 clay. Each geode robot costs 3 ore and 12 obsidian.
    """.trimIndent().lines()

    @Test
    fun `part1 should calculate the maximum geodes produced by each blueprint in 24 minutes, multiply that by the blueprint id for each blueprint and return the sum`() {
        assert(part1(sampleInput) == 33)
    }

    @Test
    fun `part2 should calculate the maximum geodes produce by the first 3 blueprints in 32 minutes, and return the product`() {
        val input = sampleInput + sampleInput.first() //need to stretch the input to 3 lines

        assert(part2(input) == 56 * 62 * 56)
    }

    @Test
    fun `parse should produce a list of Blueprint instances, 1 per line`() {
        val blueprints = parse(sampleInput)

        val expectedBlueprints = listOf(
            Blueprint(1, Ore(4), Ore(2), Pair(Ore(3), Clay(14)), Pair(Ore(2), Obsidian(7))),
            Blueprint(2, Ore(2), Ore(3), Pair(Ore(3), Clay(8)), Pair(Ore(3), Obsidian(12))),
        )

        expectedBlueprints.forEach { expected ->
            assert(expected in blueprints)
        }
        assert(expectedBlueprints.size == blueprints.size)
    }

    @Test
    fun `calculateMaximumGeodes should return the number of geodes that can be returned using a blueprint in 24 minutes`() {
        val minutesAllowed = 24
        val (blueprint1, blueprint2) = parse(sampleInput)

        assert(calculateMaximumGeodes(blueprint1, minutesAllowed) == 9)
        assert(calculateMaximumGeodes(blueprint2, minutesAllowed) == 12)
    }

    @Test
    fun `calculateMaximumGeodes should return the number of geodes that can be returned using a blueprint in 2324 minutes`() {
        val minutesAllowed = 32
        val (blueprint1, blueprint2) = parse(sampleInput)

        assert(calculateMaximumGeodes(blueprint1, minutesAllowed) == 56)
        assert(calculateMaximumGeodes(blueprint2, minutesAllowed) == 62)
    }
}