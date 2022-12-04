package day04

import utils.readInput

fun main() {
    val input = readInput("Day04")
    println(part1(input))
    println(part2(input))
}

fun part1(input: List<String>): Int {
    return input
        .map(::parseLine)
        .filter { (first, second) -> first.contains(second) || second.contains(first) }
        .count()
}

fun part2(input: List<String>): Long {
    return 0
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