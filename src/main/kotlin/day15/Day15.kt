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
    return 0
}

data class Sensor(val location: Point, val closestBeacon: Point)

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