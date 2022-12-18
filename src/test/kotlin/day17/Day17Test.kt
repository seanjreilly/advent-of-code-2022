package day17

import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import utils.Point

class Day17Test {
    private val sampleInput = """>>><<><>><<<>><>>><<<>>><<<><<<>><>><<>>"""

    @Test
    fun `part1 should call simulateRock 2022 times and return the maximum rock height`() {
        assert(part1(sampleInput) == 3068)
    }

    @Nested
    inner class FallingRockTest {
        @Test
        fun `constructor should initialize the rock 2 squares from the left edge at the specified height and store points`() {
            val rock = FallingRock(HORIZONTAL_BAR_SHAPE, 3)

            val expectedPoints = setOf(Point(2,3), Point(3,3), Point(4,3), Point(5,3))
            assert(expectedPoints == rock.points)
        }

        private val validPoints = setOf(Point(0, 0), Point(6, 0), Point(3, 1000))

        @Test
        fun `valid should return true if all x-coordinates are in the range 0-6 and all y-coordinates are greater than 0`() {
            val rock = FallingRock(validPoints)

            assert(rock.valid)
        }

        @Test
        fun `isValid should return false if an x-coordinate is less than zero`() {
            val invalidPoints = validPoints + Point(-1,3)
            val rock = FallingRock(invalidPoints)

            assert(!rock.valid)
        }

        @Test
        fun `isValid should return false if an x-coordinate is greater than six`() {
            val invalidPoints = validPoints + Point(7,3)
            val rock = FallingRock(invalidPoints)

            assert(!rock.valid)
        }

        @Test
        fun `isValid should return false if a y-coordinate is less than zero`() {
            val invalidPoints = validPoints + Point(0,-1)
            val rock = FallingRock(invalidPoints)

            assert(!rock.valid)
        }

        @Test
        fun `left should return a new instance translated 1 square to the left`() {
            val rock = FallingRock(setOf(Point(1,0), Point(5,0), Point(1,1)))
            val result = rock.left()

            assert(result != rock)
            assert(result.points == setOf(Point(0,0), Point(4,0), Point(0,1)))
        }

        @Test
        fun `right should return a new instance translated 1 square to the right`() {
            val rock = FallingRock(setOf(Point(1,0), Point(5,0), Point(1,1)))
            val result:FallingRock = rock.right()

            assert(result != rock)
            assert(result.points == setOf(Point(2,0), Point(6,0), Point(2,1)))
        }

        @Test
        fun `drop should return this instance translated 1 square down`() {
            val rock = FallingRock(VERTICAL_BAR_SHAPE)
            assert(rock.valid) { "precondition: the rock must be in a valid position before the move"}

            val result = rock.down()
            assert(!result.valid)
            assert(result != rock)
            assert(result.points == VERTICAL_BAR_SHAPE.translate(0, -1))
        }
    }

    @Nested
    inner class SimulatorTest {
        @Test
        fun `constructor should create a new Simulator with sensible values`() {
            val simulator = Simulator(sampleInput)
            assert(simulator.fallenRocks == emptySet<Point>())
            assert(simulator.maximumRockHeight == 0)
        }

        @Test
        fun `simulateRock should drop a rock, simulate how it falls until it cannot fall any more, and update fallenRocks and maximumRockHeight`() {
            val simulator = Simulator(sampleInput)
            simulator.simulateRock()

            assert(simulator.fallenRocks == HORIZONTAL_BAR_SHAPE.translate(2, 0))
            assert(simulator.maximumRockHeight == 1)
        }

        @Test
        fun `simulateRock should iterate through jetPatterns repeatedly`() {
            val simulator = Simulator("<") //expect the piece to fall left repeatedly
            simulator.simulateRock()

            assert(simulator.fallenRocks == HORIZONTAL_BAR_SHAPE) //all the way to the left
        }

        @Test
        fun `simulateRock should not move a rock left or right if it would collide with a previous fallen rock`() {
            val simulator = Simulator(sampleInput)
            val rocksFallenAfterRound = mutableListOf<Set<Point>>()
            repeat(3) {
                simulator.simulateRock()
                rocksFallenAfterRound += simulator.fallenRocks - rocksFallenAfterRound.flatten().toSet()
            }

            val fallenEll = ELL_SHAPE.translate(0, 3)
            val lastFallenRocks = rocksFallenAfterRound.last()
            assert(lastFallenRocks == fallenEll)
        }

        @Test
        fun `simulateRock should iterate through shapes and raise the start height across invocations`() {
            val simulator = Simulator(sampleInput)
            val rocksFallenAfterRound = mutableListOf<Set<Point>>()
            repeat(5) {
                simulator.simulateRock()
                rocksFallenAfterRound += simulator.fallenRocks - rocksFallenAfterRound.flatten().toSet()
            }

            //the shapes that are expected to fall
            val fallenBar = HORIZONTAL_BAR_SHAPE.translate(2, 0)
            val fallenCross = CROSS_SHAPE.translate(2, 1)
            val fallenEll = ELL_SHAPE.translate(0, 3)
            val fallenVerticalBar = VERTICAL_BAR_SHAPE.translate(4, 3)
            val fallenSquare = SQUARE_SHAPE.translate(4, 7)

            assert(rocksFallenAfterRound[0] == fallenBar)
            assert(rocksFallenAfterRound[1] == fallenCross)
            assert(rocksFallenAfterRound[2] == fallenEll)
            assert(rocksFallenAfterRound[3] == fallenVerticalBar)
            assert(rocksFallenAfterRound[4] == fallenSquare)

            val expectedTotalNumberOfRocks = listOf(
                fallenBar,
                fallenCross,
                fallenEll,
                fallenVerticalBar,
                fallenSquare
            ).sumOf { it.size }
            assert(simulator.fallenRocks.size == expectedTotalNumberOfRocks)

            assert(simulator.maximumRockHeight == 9)
        }

        @Test
        fun `results should look good after calling simulateRock 10 times`() {
            //lightly edited output from the example
            val expectedOutput = """
                |.......|
                |.......|
                |.......|
                |....#..|
                |....#..|
                |....##.|
                |##..##.|
                |######.|
                |.###...|
                |..#....|
                |.####..|
                |....##.|
                |....##.|
                |....#..|
                |..#.#..|
                |..#.#..|
                |#####..|
                |..###..|
                |...#...|
                |..####.|
                +-------+
            """.trimIndent()

            val simulator = Simulator(sampleInput)
            repeat(10) { simulator.simulateRock() }

            assert(simulator.maximumRockHeight == 17)
            val fallenRocks = simulator.printFallenRocks()
            assert(fallenRocks == expectedOutput)
        }

        @Test
        fun `after calling simulateRock 2022 times, maximumRockHeight should be 3068`() {
            val simulator = Simulator(sampleInput)

            repeat(2022) { simulator.simulateRock() }

            assert(simulator.maximumRockHeight == 3068)

            println(simulator.printFallenRocks())
        }
    }
}