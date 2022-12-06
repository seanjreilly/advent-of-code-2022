package day06

import utils.readInput

fun main() {
    val input = readInput("Day06")
    println(part1(input.first()))
    println(part2(input.first()))
}

fun part1(input: String): Int {
    return findMarker(input)
}

fun part2(input: String): Long {
    return 0
}

fun findMarker(input: String): Int {
    return (4..input.length)
        .map {
            val chunk = input.substring(it-4, it)
            Pair(it, chunk.toCharArray())
        }
        .filter { it.second.distinct().size == 4 }
        .map { it.first }
        .min()
}
