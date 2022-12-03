package day03

import utils.readInput

fun main() {
    val input = readInput("Day03")
    println(part1(input))
    println(part2(input))
}

fun part1(input: List<String>): Int {
    return input
        .map {
            val midPoint = it.length / 2
            Pair(it.substring(0, midPoint), it.substring(midPoint))
        }
        .map { (firstHalf, secondHalf) ->
            val secondHalfSet = secondHalf.toSet()
            firstHalf.first { it in secondHalfSet }
        }
        .sumOf { priority(it) }
}

fun part2(input: List<String>): Long {
    return 0
}

fun priority(char: Char): Int {
    if (char.isLowerCase()) {
        return char - 'a' + 1
    }
    return char - 'A' + 27
}