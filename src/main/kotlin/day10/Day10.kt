package day10

import utils.readInput
import kotlin.math.abs
import kotlin.system.measureTimeMillis

fun main() {
    val elapsed = measureTimeMillis {
        val input = readInput("Day10")
        println(part1(input))
        println(part2(input))
    }
    println()
    println("Elapsed time: $elapsed ms.")
}

fun part1(input: List<String>): Int {
    val cyclesToSample = setOf(20, 60, 100, 140, 180, 220).map { it - 1 }.toSet() //make it zero-based
    return calculateSignalStrengths(input)
        .filterIndexed { index, _ -> index in cyclesToSample }
        .sum()

}

fun part2(input: List<String>): String {
    return calculateRegisterValues(input)
        .mapIndexed { index, registerValue -> Pair((index % 40), registerValue) }
        .map { if (abs(it.first - it.second) <= 1) { '#' } else { '.'} }
        .chunked(40)
        .map { it.joinToString("") }
        .joinToString("\n")
}

fun calculateSignalStrengths(input: List<String>): Sequence<Int> {
    return calculateRegisterValues(input)
        .mapIndexed { index, registerValue -> registerValue * (index + 1)  } //add 1 because the index is zero-based but cycle count starts at 1
}

fun calculateRegisterValues(input: List<String>): Sequence<Int> = sequence {
    var registerValue = 1 //the register always starts at 1
    input.forEach { instruction ->
        when (instruction) {
            "noop" -> yield(registerValue)
            else -> { //addX instruction
                val incrementValue = instruction.substring(5).toInt()
                yield(registerValue) //the register doesn't change during the first cycle
                yield(registerValue) //or during the second cycle, only at the end of the cycle (will be output next time)
                registerValue += incrementValue
            }
        }
    }
}