package day06

import utils.readInput

fun main() {
    val input = readInput("Day06")
    println(part1(input.first()))
    println(part2(input.first()))
}

fun part1(input: String): Int {
    return findMarker(input, 4)
}

fun part2(input: String): Int {
    return findMarker(input, 14)
}

fun findMarker(input: String, chunkSize: Int): Int {
    return input.asSequence()
        .windowed(chunkSize, 1)
        .mapIndexed { index, chunk -> Pair(index, chunk)}
        .filter { it.second.distinct().size == chunkSize }
        .map { it.first }
        .first() + chunkSize //the windows start chunkSize characters in
}
