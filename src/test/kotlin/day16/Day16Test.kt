package day16

import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

class Day16Test {
    private val sampleInput = """
        Valve AA has flow rate=0; tunnels lead to valves DD, II, BB
        Valve BB has flow rate=13; tunnels lead to valves CC, AA
        Valve CC has flow rate=2; tunnels lead to valves DD, BB
        Valve DD has flow rate=20; tunnels lead to valves CC, AA, EE
        Valve EE has flow rate=3; tunnels lead to valves FF, DD
        Valve FF has flow rate=0; tunnels lead to valves EE, GG
        Valve GG has flow rate=0; tunnels lead to valves FF, HH
        Valve HH has flow rate=22; tunnel leads to valve GG
        Valve II has flow rate=0; tunnels lead to valves AA, JJ
        Valve JJ has flow rate=21; tunnel leads to valve II
    """.trimIndent().lines()

    @Test
    fun `part1 should construct a valve layout and return the maximum pressure that can be relieved in 30 minutes`() {
        assert(part1(sampleInput) == 1651)
    }

    @Nested
    inner class ValveLayoutTest {

        @Test
        fun `constructor should take input and construct a valid Scenario instance`() {
            val valveLayout = ValveLayout(sampleInput)

            val expectedValves = setOf(
                Valve(Location("BB"), 13),
                Valve(Location("CC"), 2),
                Valve(Location("DD"), 20),
                Valve(Location("EE"), 3),
                Valve(Location("HH"), 22),
                Valve(Location("JJ"), 21),
            )

            val expectedTunnels = mapOf(
                Location("AA") to setOf(Location("DD"), Location("II"), Location("BB")),
                Location("BB") to setOf(Location("CC"), Location("AA")),
                Location("CC") to setOf(Location("DD"), Location("BB")),
                Location("DD") to setOf(Location("CC"), Location("AA"), Location("EE")),
                Location("EE") to setOf(Location("FF"), Location("DD")),
                Location("FF") to setOf(Location("EE"), Location("GG")),
                Location("GG") to setOf(Location("FF"), Location("HH")),
                Location("HH") to setOf(Location("GG")),
                Location("II") to setOf(Location("AA"), Location("JJ")),
                Location("JJ") to setOf(Location("II")),
            )

            expectedValves.forEach {
                assert(valveLayout.valves[it.location] == it)
            }
            assert(expectedValves.size == valveLayout.valves.size)

            expectedTunnels.forEach { (location, reachableLocations) ->
                assert(valveLayout.tunnels[location] == reachableLocations)
            }
            assert(expectedTunnels.size == valveLayout.tunnels.size)
        }
        
        @Test
        fun `findMaximumRelievablePressure should use BFS to find the most pressure that can be relieved using the rules`() {
            val valveLayout = ValveLayout(sampleInput)

            val result = valveLayout.findMaximumRelievablePressure()

            assert(result == 1651)
        }
    }
}