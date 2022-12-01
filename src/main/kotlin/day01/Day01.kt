package day01

import utils.readInput

fun main() {
    val input = readInput("Day01")
    println(part1(input))
    println(part2(input))
}

fun part1(input: List<String>): Int {
    return calculateSums(input).max()
}

fun part2(input: List<String>): Int {
    return calculateSums(input).sortedDescending().take(3).sum()
}

fun calculateSums(originalInput: List<String>): List<Int> {
    val chunks = mutableListOf<List<String>>()
    var input = originalInput
    while (input.isNotEmpty()) {
        val chunk = input.takeWhile { it != "" }
        input = input.drop(chunk.size + 1)
        chunks += chunk
    }
    return chunks.map { it.map { it.toInt() }.sum() }
}