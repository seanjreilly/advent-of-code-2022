package day20

import utils.readInput
import kotlin.system.measureTimeMillis

fun main() {
    val elapsed = measureTimeMillis {
        val input = readInput("Day20")
        println(part1(input))
        println(part2(input))
    }
    println()
    println("Elapsed time: $elapsed ms.")
}

fun part1(input: List<String>): Int {
    val mix = parse(input).mix()
    val index = mix.indexOf(0)
    fun safeIndex(value: Int) = Math.floorMod(index + value, mix.size)
    return mix[safeIndex(1000)] + mix[safeIndex(2000)] + mix[safeIndex(3000)]
}

fun part2(input: List<String>): Long {
    return 0
}

fun parse(input: List<String>): List<Int> = input.map { it.toInt() }

fun List<Int>.mix(): List<Int> {
    //using a mutable ArrayList is actually faster than using a LinkedList
    val linkedList = this.mapIndexed { index, value -> Pair(index, value) }.toMutableList()
    this.forEachIndexed { originalIndex, value ->
        if (value ==0) { return@forEachIndexed }

        val index = linkedList.indexOf(Pair(originalIndex, value))
        val element = linkedList.removeAt(index)
        val newIndex = (index + value).mod(linkedList.size)
        linkedList.add(newIndex, element)
    }
    return linkedList.map { it.second }
}
