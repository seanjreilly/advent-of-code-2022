package day10

import utils.readInput
import kotlin.math.abs

fun main() {
    val input = readInput("Day10")
    println(part1(input))
    println(part2(input))
}

fun part1(input: List<String>): Long {
    val cyclesToSample = setOf(20, 60, 100, 140, 180, 220).map { it - 1 }.toSet() //make it zero-based
    return calculateSignalStrengths(input)
        .filterIndexed { index, signalStrength -> index in cyclesToSample }
        .map { it.toLong() }
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

fun calculateSignalStrengths(input: List<String>) = sequence {
    calculateRegisterValues(input)
        .forEachIndexed { index, registerValue ->
            yield(registerValue * (index + 1))
        }
}

fun calculateRegisterValues(input: List<String>): Sequence<Int> = sequence {
    var registerValue = 1 //the register always starts at 1
    input.forEach { line ->
        if (line == "noop") { //noop instruction
            yield(registerValue) //the value doesn't change during or after the cycle for a noop
            return@forEach
        }
        //addX instruction
        val incrementValue = line.substring(5).toInt()
        yield(registerValue) //the register doesn't change during the first cycle
        yield(registerValue) //or during the second cycle, only at the end of the cycle (will be output next time)
        registerValue += incrementValue
    }
}