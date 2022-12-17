package day02

import utils.readInput
import kotlin.system.measureTimeMillis

fun main() {
    val elapsed = measureTimeMillis {
        val input = readInput("Day02")
        println(part1(input))
        println(part2(input))
    }
    println()
    println("Elapsed time: $elapsed ms.")
}

fun part1(input: List<String>): Int {
    return input
        .map { parseRawValuesPart1(it) }
        .map { Pair(it.second, it.second.outcome(it.first)) }
        .sumOf { score(it.first, it.second) }
}

fun part2(input: List<String>): Int {
    return input
        .map { parseRawValuesPart2(it) }
        .map { Pair(it.first.findPlayForDesiredOutcome(it.second), it.second) }
        .sumOf { score(it.first, it.second) }
}

fun score(yourPlay: YourPlay, outcome: Outcome): Int {
    return yourPlay.score + outcome.score
}

private fun parseRawValuesPart1(line: String): Pair<OpponentsPlay, YourPlay> {
    val (rawFirst, rawSecond) = line.split(' ', limit = 2)
    val first = OpponentsPlay.values().first { it.rawValue == rawFirst }
    val second = YourPlay.values().first { it.rawValue == rawSecond }
    return Pair(first, second)
}

private fun parseRawValuesPart2(line: String): Pair<OpponentsPlay, Outcome> {
    val (rawFirst, rawSecond) = line.split(' ', limit = 2)
    val first = OpponentsPlay.values().first { it.rawValue == rawFirst }
    val second = Outcome.values().first { it.rawValue == rawSecond }
    return Pair(first, second)
}

enum class OpponentsPlay(val rawValue: String) {
    ROCK("A"), PAPER("B"), SCISSORS("C");

    val losesTo = YourPlay.values()[(this.ordinal + 1) % 3] //wraparound for scissors loses to rock
    val beats = YourPlay.values()[(this.ordinal + 2) % 3] //wraparound for paper beats rock and scissors beats paper
    val draws = YourPlay.values()[this.ordinal]

    fun findPlayForDesiredOutcome(desiredOutcome: Outcome): YourPlay {
        return when(desiredOutcome) {
            Outcome.Win -> this.losesTo
            Outcome.Loss -> this.beats
            Outcome.Draw -> this.draws
        }
    }
}

enum class YourPlay(val rawValue: String) {
    ROCK("X"), PAPER("Y"), SCISSORS("Z");
    val score = ordinal + 1
    fun outcome(opponentsPlay: OpponentsPlay) : Outcome {
        return when (this) {
            opponentsPlay.beats -> Outcome.Loss
            opponentsPlay.losesTo -> Outcome.Win
            else -> Outcome.Draw
        }
    }
}

enum class Outcome(val rawValue: String, val score: Int) {
    Win("Z",6), Loss("X", 0), Draw("Y", 3)
}