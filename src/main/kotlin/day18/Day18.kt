package day18

import utils.readInput
import kotlin.system.measureTimeMillis

fun main() {
    val elapsed = measureTimeMillis {
        val input = readInput("Day18")
        println(part1(input))
        println(part2(input))
    }
    println()
    println("Elapsed time: $elapsed ms.")
}

fun part1(input: List<String>): Int {
    return countUnconnectedSides(parse(input))
}

fun part2(input: List<String>): Int {
    val points = parse(input)
    val interiorPoints = findInteriorPoints(points)
    return countUnconnectedSides(points + interiorPoints)
}

fun countUnconnectedSides(points: Set<Point3D>): Int {
    return points.sumOf { point ->
        val neighboursThatExist = point.neighbours().filter { it in points }
        6 - neighboursThatExist.size
    }
}

fun findInteriorPoints(points: Set<Point3D>): Set<Point3D> {
    //calculate bounding box
    val xBounds = points.minOf { it.x }..points.maxOf { it.x }
    val yBounds = points.minOf { it.y }..points.maxOf { it.y }
    val zBounds = points.minOf { it.z }..points.maxOf { it.z }

    val pointsToCheck = xBounds
        .flatMap { x ->
            yBounds.flatMap { y ->
                zBounds.map { z -> Point3D(x, y, z) }
            }
        }
        .filter { it !in points }

    val knownInteriorPoints = mutableSetOf<Point3D>()
    val knownExteriorPoints = mutableSetOf<Point3D>()

    point@for (potentialPoint in pointsToCheck) {
        //check if this point is an interior point or not
        val alreadyChecked = mutableSetOf<Point3D>()
        val queue = ArrayDeque<Point3D>()
        queue.addLast(potentialPoint)
        while (queue.isNotEmpty()) {
            val point = queue.removeFirst()
            if (point in alreadyChecked) {
                continue
            }
            alreadyChecked += point

            if (point in knownExteriorPoints) {
                //we've found the outside
                knownExteriorPoints += potentialPoint
                continue@point
            }

            if (point in knownInteriorPoints) {
                //we've found the inside
                knownInteriorPoints += potentialPoint
                continue@point
            }

            if (point.x !in xBounds && point.y !in yBounds && point.z !in zBounds) {
                //we've found the outside
                knownExteriorPoints += potentialPoint
                continue@point
            }
            point.neighbours().filter { it !in points }.forEach { queue.addLast(it) }
        }
        //we've found the inside
        knownInteriorPoints += potentialPoint
        continue@point
    }

    return knownInteriorPoints
}

fun parse(input: List<String>): Set<Point3D> {
    return input
        .map { it.split(",") }
        .map { rawPoint -> rawPoint.map { it.toInt() } }
        .map { (x,y,z) -> Point3D(x,y,z) }
        .toSet()
}

data class Point3D(val x: Int, val y: Int, val z: Int) {
    fun neighbours(): Set<Point3D> {
        return setOf(
            Point3D(x + 1, y, z),
            Point3D(x - 1, y, z),
            Point3D(x, y + 1, z),
            Point3D(x, y - 1, z),
            Point3D(x, y, z + 1),
            Point3D(x, y, z - 1),
        )
    }
}