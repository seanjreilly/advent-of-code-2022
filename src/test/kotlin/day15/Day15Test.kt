package day15

import org.junit.jupiter.api.Test
import utils.Point
import utils.readInput

class Day15Test {
    private val sampleInput = """
        Sensor at x=2, y=18: closest beacon is at x=-2, y=15
        Sensor at x=9, y=16: closest beacon is at x=10, y=16
        Sensor at x=13, y=2: closest beacon is at x=15, y=3
        Sensor at x=12, y=14: closest beacon is at x=10, y=16
        Sensor at x=10, y=20: closest beacon is at x=10, y=16
        Sensor at x=14, y=17: closest beacon is at x=10, y=16
        Sensor at x=8, y=7: closest beacon is at x=2, y=10
        Sensor at x=2, y=0: closest beacon is at x=2, y=10
        Sensor at x=0, y=11: closest beacon is at x=2, y=10
        Sensor at x=20, y=14: closest beacon is at x=25, y=17
        Sensor at x=17, y=20: closest beacon is at x=21, y=22
        Sensor at x=16, y=7: closest beacon is at x=15, y=3
        Sensor at x=14, y=3: closest beacon is at x=15, y=3
        Sensor at x=20, y=1: closest beacon is at x=15, y=3
    """.trimIndent().lines()

    /*
        The first line of the actual input file. The beacon is a *long* way
        from the sensor
     */
    private val bigSampleInput = """
        Sensor at x=2300471, y=2016823: closest beacon is at x=2687171, y=2822745
    """.trimIndent().lines()

    @Test
    fun `part1 should call countPositionsWithNoBeaconsInRow with y = 2000000`() {
        assert(part1(bigSampleInput) == 2351599)
    }

    @Test
    fun `part2 should return the tuning frequency for the big result`() {
        val hugeInput = readInput("Day15")
        val expectedPoint = Point(3103499, 3391794)
        val expectedResult = (expectedPoint.x.toLong() * 4000000L) + expectedPoint.y.toLong()
        assert(part2(hugeInput) == expectedResult)
    }
    
    @Test
    fun `countPositionsWithNoBeaconsInRow should return the number of positions with a given y coordinate that cannot contain a beacon`() {
        val sensors = parse(sampleInput)
        assert(countPositionsWithNoBeaconsInRow(sensors, 10) == 26)
    }

    @Test
    fun `countPositionsWithNoBeaconsInRow should work with a sensor that is _really_ far from the closest beacon`() {
        val sensors = parse(bigSampleInput)
        assert(countPositionsWithNoBeaconsInRow(sensors, 2000000) == 2351599)
    }

    @Test
    fun `findBeacon should return the only point in the bounding box that isn't covered by any sensor`() {
        val sensors = parse(sampleInput)
        assert(findBeacon(sensors, 20) == Point(14,11))
    }

    @Test
    fun `findBeacon should return with really big input`() {
        val sensors = parse(readInput("Day15"))
        assert(findBeacon(sensors, 4000000) == Point(3103499, 3391794))
    }

    @Test
    fun `parse should return a list of Sensor instances`() {
        val result: List<Sensor> = parse(sampleInput)

        val expectedResult = listOf(
            Sensor(Point(2, 18), closestBeacon = Point(-2, 15)),
            Sensor(Point(9, 16), closestBeacon = Point(10, 16)),
            Sensor(Point(13, 2), closestBeacon = Point(15, 3)),
            Sensor(Point(12, 14), closestBeacon = Point(10, 16)),
            Sensor(Point(10, 20), closestBeacon = Point(10, 16)),
            Sensor(Point(14, 17), closestBeacon = Point(10, 16)),
            Sensor(Point(8, 7), closestBeacon = Point(2, 10)),
            Sensor(Point(2, 0), closestBeacon = Point(2, 10)),
            Sensor(Point(0, 11), closestBeacon = Point(2, 10)),
            Sensor(Point(20, 14), closestBeacon = Point(25, 17)),
            Sensor(Point(17, 20), closestBeacon = Point(21, 22)),
            Sensor(Point(16, 7), closestBeacon = Point(15, 3)),
            Sensor(Point(14, 3), closestBeacon = Point(15, 3)),
            Sensor(Point(20, 1), closestBeacon = Point(15, 3))
        )

        expectedResult.forEach {
            assert(it in result)
        }
        assert(result.size == expectedResult.size)
    }
    
    @Test
    fun `findPointsOnRowWithManhattanDistance should return the points (there will be either 2, 1, or 0) on the row that are a given 'manhattan distance' from a specific point`() {
        val point = Point(8,7)
        val manhattanDistance = 9

        //use sets to determine sequence equivalence
        assert(point.findPointsOnRowWithManhattanDistance(7, manhattanDistance = manhattanDistance).toSet() == setOf(Point(-1,7), Point(17,7)))
        assert(point.findPointsOnRowWithManhattanDistance(8, manhattanDistance = manhattanDistance).toSet() == setOf(Point(0,8), Point(16,8)))
        assert(point.findPointsOnRowWithManhattanDistance(9, manhattanDistance = manhattanDistance).toSet() == setOf(Point(1,9), Point(15,9)))
        assert(point.findPointsOnRowWithManhattanDistance(10, manhattanDistance = manhattanDistance).toSet() == setOf(Point(2,10), Point(14,10)))
        //all the way up to y = 16, which has 1 point
        assert(point.findPointsOnRowWithManhattanDistance(16, manhattanDistance = manhattanDistance).toSet() == setOf(Point(8,16)))

        //after this it should return no points
        assert(point.findPointsOnRowWithManhattanDistance(17, manhattanDistance = manhattanDistance).toSet() == emptySet<Point>())
    }
}

