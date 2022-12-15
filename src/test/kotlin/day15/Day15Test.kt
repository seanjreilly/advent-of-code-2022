package day15

import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import utils.Point

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
    
    @Nested
    inner class SensorTest {
        @Test
        fun `pointsWithNoUnknownBeacons should return a sequence containing the sensor location, the closest beacon location, and all points closer in manhattan distance to the sensor than the closest beacon`() {
            val sensorLocation = Point(8, 7)
            val closedBeaconLocation = Point(2, 10)
            val sensor = Sensor(sensorLocation, closestBeacon = closedBeaconLocation)

            val expectedResults = setOf(
                sensorLocation,
                Point(8, -2),

                Point(7, -1),
                Point(8, -1),
                Point(9, -1),

                Point(6, 0),
                Point(7, 0),
                Point(8, 0),
                Point(9, 0),
                Point(10, 0),

                Point(5, 1),
                Point(6, 1),
                Point(7, 1),
                Point(8, 1),
                Point(9, 1),
                Point(10, 1),
                Point(11, 1),

                Point(4, 2),
                Point(5, 2),
                Point(6, 2),
                Point(7, 2),
                Point(8, 2),
                Point(9, 2),
                Point(10, 2),
                Point(11, 2),
                Point(12, 2),

                Point(3, 3),
                Point(4, 3),
                Point(5, 3),
                Point(6, 3),
                Point(7, 3),
                Point(8, 3),
                Point(9, 3),
                Point(10, 3),
                Point(11, 3),
                Point(12, 3),
                Point(13, 3),

                Point(2, 4),
                Point(3, 4),
                Point(4, 4),
                Point(5, 4),
                Point(6, 4),
                Point(7, 4),
                Point(8, 4),
                Point(9, 4),
                Point(10, 4),
                Point(11, 4),
                Point(12, 4),
                Point(13, 4),
                Point(14, 4),

                Point(1, 5),
                Point(2, 5),
                Point(3, 5),
                Point(4, 5),
                Point(5, 5),
                Point(6, 5),
                Point(7, 5),
                Point(8, 5),
                Point(9, 5),
                Point(10, 5),
                Point(11, 5),
                Point(12, 5),
                Point(13, 5),
                Point(14, 5),
                Point(15, 5),

                Point(0, 6),
                Point(1, 6),
                Point(2, 6),
                Point(3, 6),
                Point(4, 6),
                Point(5, 6),
                Point(6, 6),
                Point(7, 6),
                Point(8, 6),
                Point(9, 6),
                Point(10, 6),
                Point(11, 6),
                Point(12, 6),
                Point(13, 6),
                Point(14, 6),
                Point(15, 6),
                Point(16, 6),

                Point(-1, 7),
                Point(0, 7),
                Point(1, 7),
                Point(2, 7),
                Point(3, 7),
                Point(4, 7),
                Point(5, 7),
                Point(6, 7),
                Point(7, 7),
                Point(8, 7),
                Point(9, 7),
                Point(10, 7),
                Point(11, 7),
                Point(12, 7),
                Point(13, 7),
                Point(14, 7),
                Point(15, 7),
                Point(16, 7),
                Point(17, 7),

                Point(0, 8),
                Point(1, 8),
                Point(2, 8),
                Point(3, 8),
                Point(4, 8),
                Point(5, 8),
                Point(6, 8),
                Point(7, 8),
                Point(8, 8),
                Point(9, 8),
                Point(10, 8),
                Point(11, 8),
                Point(12, 8),
                Point(13, 8),
                Point(14, 8),
                Point(15, 8),
                Point(16, 8),

                Point(1, 9),
                Point(2, 9),
                Point(3, 9),
                Point(4, 9),
                Point(5, 9),
                Point(6, 9),
                Point(7, 9),
                Point(8, 9),
                Point(9, 9),
                Point(10, 9),
                Point(11, 9),
                Point(12, 9),
                Point(13, 9),
                Point(14, 9),
                Point(15, 9),

                Point(2, 10),
                Point(3, 10),
                Point(4, 10),
                Point(5, 10),
                Point(6, 10),
                Point(7, 10),
                Point(8, 10),
                Point(9, 10),
                Point(10, 10),
                Point(11, 10),
                Point(12, 10),
                Point(13, 10),
                Point(14, 10),

                Point(3, 11),
                Point(4, 11),
                Point(5, 11),
                Point(6, 11),
                Point(7, 11),
                Point(8, 11),
                Point(9, 11),
                Point(10, 11),
                Point(11, 11),
                Point(12, 11),
                Point(13, 11),

                Point(4, 12),
                Point(5, 12),
                Point(6, 12),
                Point(7, 12),
                Point(8, 12),
                Point(9, 12),
                Point(10, 12),
                Point(11, 12),
                Point(12, 12),

                Point(5, 13),
                Point(6, 13),
                Point(7, 13),
                Point(8, 13),
                Point(9, 13),
                Point(10, 13),
                Point(11, 13),

                Point(6, 14),
                Point(7, 14),
                Point(8, 14),
                Point(9, 14),
                Point(10, 14),

                Point(7, 15),
                Point(8, 15),
                Point(9, 15),

                Point(8, 16),
            )

            val result:Sequence<Point> = sensor.findPointsWithNoUnknownBeacons()

            expectedResults.forEach {
                assert(it in result)
            }
            assert(expectedResults.size == result.toSet().size) //the sequence can contain duplicate points
        }
    }
}