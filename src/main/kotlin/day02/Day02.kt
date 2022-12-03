package day02

import utils.readInput

fun main() {
    val input = readInput("Day02")
    println(part1(input))
    println(part2(input))
}

fun part1(input: List<String>): Int {
    return input
        .map { parseRawValues(it) }
        .map { Pair(it.second, it.second.outcome(it.first)) }
        .sumOf { score(it.first, it.second) }
}

fun part2(input: List<String>): Long {
    return 0
}

fun score(yourPlay: YourPlay, outcome: Outcome): Int {
    return yourPlay.score + outcome.score
}

private fun parseRawValues(line: String): Pair<OpponentsPlay, YourPlay> {
    val (rawFirst, rawSecond) = line.split(' ', limit = 2)
    val first = OpponentsPlay.values().first { it.rawValue == rawFirst }
    val second = YourPlay.values().first { it.rawValue == rawSecond }
    return Pair(first, second)
}

enum class OpponentsPlay(val rawValue: String) {
    ROCK("A"), PAPER("B"), SCISSORS("C")
}

enum class YourPlay(val rawValue: String) {
    ROCK("X"), PAPER("Y"), SCISSORS("Z");
    val score = ordinal + 1
    fun outcome(opponentsPlay: OpponentsPlay) : Outcome {
        //wraparound for rock beats scissors
        val losesTo = (OpponentsPlay.values() + OpponentsPlay.values().first())[this.ordinal + 1]

        if (opponentsPlay == losesTo) {
            return Outcome.Loss
        }
        if (opponentsPlay.ordinal == this.ordinal) {
            return Outcome.Draw
        }
        return Outcome.Win
    }
}

enum class Outcome(val score: Int) {
    Win(6), Loss(0), Draw(3)
}