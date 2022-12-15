package day15

import utils.Point
import utils.readInput
import kotlin.math.abs

fun main() {
    val input = readInput("Day15")
    println(part1(input))
    println(part2(input))
}

fun part1(input: List<String>): Int {
    return countPositionsWithNoBeaconsInRow(parse(input), 2000000)
}

fun part2(input: List<String>): Long {
    val coordinateLimit = 4000000
    val location = findBeacon(parse(input), coordinateLimit)
    return location.x.toLong() * coordinateLimit.toLong() + location.y.toLong()
}

data class Sensor(val location: Point, val closestBeacon: Point) {
    val manhattanDistance = location.manhattanDistance(closestBeacon)
}

fun countPositionsWithNoBeaconsInRow(sensors: List<Sensor>, y: Int): Int {
    val result = sensors.asSequence()
    .flatMap {sensor ->
        val yOffset = abs(y - sensor.location.y)
        val manhattanDistanceBetweenSensorAndBeacon = sensor.location.manhattanDistance(sensor.closestBeacon)
        val manhattanDistancesToConsider = yOffset..manhattanDistanceBetweenSensorAndBeacon

        manhattanDistancesToConsider.asSequence()
            .flatMap { sensor.location.findPointsOnRowWithManhattanDistance(y, it) }
    }.toSet()

    //the answer is not supposed to include any actual beacon locations
    val beaconsOnY = sensors.map { it.closestBeacon }.filter { it.y == y }.toSet()
    return (result - beaconsOnY).size
}

fun findBeacon(sensors: List<Sensor>, maxCoordinateValue: Int): Point {
    /*
    the signal is in only 1 square, so it has to be 1 square beyond the "bounding diamond" for
    a sensor — otherwise there could be more than 1 square the signal could be in.

    This is a manhattan distance of n+1 (where n is the manhattan distance of the closest beacon).

    So:
        - For each sensor:
            - consider the points at manhattan distance 1 further than the beacon in the bounding box
            - find a point outside the bounding diamond for every other sensor
        - if nothing, there's no answer — throw an exception
     */
    val boundingBox = 0..maxCoordinateValue

    sensors.forEach { sensor ->
        val otherSensors = sensors.filter { it != sensor }
        sensor.location.pointsWithManhattanDistance(sensor.manhattanDistance + 1)
            .filter { it.x in boundingBox && it.y in boundingBox }
            .forEach { candidate ->
                otherSensors.firstOrNull { it.location.manhattanDistance(candidate) <= it.manhattanDistance }
                    ?: return candidate
            }
    }

    throw IllegalStateException("No point found that isn't within a sensor's distance")
}

fun Point.findPointsOnRowWithManhattanDistance(y: Int, manhattanDistance: Int): Sequence<Point> {
    val yOffset = abs(y - this.y)
    val x = this.x

    return when {
        yOffset > manhattanDistance -> emptySequence()
        yOffset == manhattanDistance -> sequenceOf(Point(x, y))
        else ->  {
            val xOffset = manhattanDistance - yOffset
            sequenceOf(Point(x + xOffset, y), Point(x - xOffset, y))
        }
    }
}

private val parseRegex = """Sensor at x=(-?\d+), y=(-?\d+): closest beacon is at x=(-?\d+), y=(-?\d+)""".toRegex()
fun parse(input: List<String>): List<Sensor> {
    return input
        .map { parseRegex.matchEntire(it)!!.groupValues.drop(1)}
        .map { it.map (String::toInt)}
        .map { it.windowed(2, 2).map {(x,y) -> Point(x,y) } }
        .map { (sensorLocation, closestBeaconLocation) -> Sensor(sensorLocation, closestBeacon = closestBeaconLocation) }
}