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

            expectedValves.forEach {
                assert(valveLayout.valves[it.location] == it)
            }
            assert(expectedValves.size == valveLayout.valves.size)

//
//            val expectedJourneys = mapOf(
//                Pair(Location("AA"), Location("BB")) to 1,
//                Pair(Location("AA"), Location("CC")) to 2,
//                Pair(Location("AA"), Location("DD")) to 1,
//                Pair(Location("AA"), Location("EE")) to 2,
//                Pair(Location("AA"), Location("HH")) to 5,
//                Pair(Location("AA"), Location("JJ")) to 2,
//
//                Pair(Location("BB"), Location("CC")) to 1,
//                Pair(Location("BB"), Location("DD")) to 2,
//                Pair(Location("BB"), Location("EE")) to 3,
//                Pair(Location("BB"), Location("HH")) to 6,
//                Pair(Location("BB"), Location("JJ")) to 3,
//
//                Pair(Location("CC"), Location("BB")) to 1,
//                Pair(Location("CC"), Location("DD")) to 1,
//                Pair(Location("CC"), Location("EE")) to 2,
//                Pair(Location("CC"), Location("HH")) to 5,
//                Pair(Location("CC"), Location("JJ")) to 4,
//
//                Pair(Location("DD"), Location("BB")) to 2,
//                Pair(Location("DD"), Location("CC")) to 1,
//                Pair(Location("DD"), Location("EE")) to 1,
//                Pair(Location("DD"), Location("HH")) to 4,
//                Pair(Location("DD"), Location("JJ")) to 3,
//
//                Pair(Location("EE"), Location("BB")) to 3,
//                Pair(Location("EE"), Location("CC")) to 2,
//                Pair(Location("EE"), Location("DD")) to 1,
//                Pair(Location("EE"), Location("HH")) to 3,
//                Pair(Location("EE"), Location("JJ")) to 4,
//
//                Pair(Location("HH"), Location("BB")) to 6,
//                Pair(Location("HH"), Location("CC")) to 5,
//                Pair(Location("HH"), Location("DD")) to 4,
//                Pair(Location("HH"), Location("EE")) to 3,
//                Pair(Location("HH"), Location("JJ")) to 7,
//
//                Pair(Location("JJ"), Location("BB")) to 3,
//                Pair(Location("JJ"), Location("CC")) to 5,
//                Pair(Location("JJ"), Location("DD")) to 3,
//                Pair(Location("JJ"), Location("EE")) to 4,
//                Pair(Location("JJ"), Location("HH")) to 7,
//            )
//
//            expectedJourneys.forEach { (locations, distance) ->
//                assert(valveLayout.journeys[locations] == distance)
//            }
//            assert(valveLayout.journeys.size == expectedJourneys.size)
//
//            //there should be a journey from AA to every valve
//            expectedValves.forEach { valve ->
//                assert(Pair(Location("AA"), valve.location) in valveLayout.journeys.keys) { "There should be a journey from the start to every active valve" }
//            }
//
//            //there should be a journey from every valve to every other valve
//            expectedValves.forEach { firstValve ->
//                expectedValves.forEach { secondValve ->
//                    assert(Pair(firstValve.location, secondValve.location) in valveLayout.journeys.keys) { "There should be a journey from every valve to every other valve" }
//                }
//            }

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