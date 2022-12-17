package day01

import utils.readInput
import kotlin.system.measureTimeMillis

fun main() {
    val elapsed = measureTimeMillis {
        val input = readInput("Day01")
        println(part1(input))
        println(part2(input))
    }
    println()
    println("Elapsed time: $elapsed ms.")
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