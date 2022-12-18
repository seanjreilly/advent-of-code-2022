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

fun part2(input: List<String>): Long {
    return 0
}

fun countUnconnectedSides(points: Set<Point3D>): Int {
    return points.sumOf { point ->
        val neighboursThatExist = point.neighbours().filter { it in points }
        6 - neighboursThatExist.size
    }
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