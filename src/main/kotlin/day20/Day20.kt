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

fun part1(input: List<String>): Long {
    val mix = parse(input).mix()
    val index = mix.indexOf(0)
    fun safeIndex(value: Int) = Math.floorMod(index + value, mix.size)
    return mix[safeIndex(1000)] + mix[safeIndex(2000)] + mix[safeIndex(3000)]
}

fun part2(input: List<String>): Long {
    val mix = parse(input).map { it * DECRYPTION_KEY }.mix(10)
    val index = mix.indexOf(0)
    fun safeIndex(value: Int) = Math.floorMod(index + value, mix.size)
    return mix[safeIndex(1000)] + mix[safeIndex(2000)] + mix[safeIndex(3000)]
}

const val DECRYPTION_KEY = 811589153L

fun parse(input: List<String>): List<Long> = input.map { it.toLong() }

fun List<Long>.mix(rounds: Int = 1): List<Long> {
    //using a mutable ArrayList is actually faster than using a LinkedList
    val linkedList = this.mapIndexed { index, value -> Pair(index, value) }.toMutableList()
    repeat(rounds) {
        this.forEachIndexed { originalIndex, value ->
            if (value == 0L) {
                return@forEachIndexed
            }

            val index = linkedList.indexOf(Pair(originalIndex, value))
            val element = linkedList.removeAt(index)
            val newIndex = (index + value).mod(linkedList.size)
            linkedList.add(newIndex, element)
        }
    }
    return linkedList.map { it.second }
}
