package day04

import utils.readInput
import kotlin.system.measureTimeMillis

fun main() {
    val elapsed = measureTimeMillis {
        val input = readInput("Day04")
        println(part1(input))
        println(part2(input))
    }
    println()
    println("Elapsed time: $elapsed ms.")
}

fun part1(input: List<String>): Int {
    return input
        .map(::parseLine)
        .count { (first, second) -> first.contains(second) || second.contains(first) }
}

fun part2(input: List<String>): Int {
    return input
        .map(::parseLine)
        .count { (first, second) -> first.overlaps(second) || second.overlaps(first)}
}

fun parseLine(line: String): Pair<IntRange, IntRange> {
    fun toRange(part:String): IntRange {
        val (low, high) = part.split('-').map(String::toInt)
        return low..high
    }
    val (first, second) = line.split(',').map(::toRange)
    return Pair(first, second)
}

fun IntRange.contains(otherRange: IntRange) = this.contains(otherRange.first) && this.contains(otherRange.last)

fun IntRange.overlaps(otherRange: IntRange) = this.contains(otherRange.first) || this.contains(otherRange.last)