package day21

import utils.readInput
import kotlin.system.measureTimeMillis

fun main() {
    val elapsed = measureTimeMillis {
        val input = readInput("Day21")
        println(part1(input))
        println(part2(input))
    }
    println()
    println("Elapsed time: $elapsed ms.")
}

fun part1(input: List<String>): Long {
    return parse(input).let { it["root"]!!.getNumber(it) }
}

fun part2(input: List<String>): Long {
    return 0
}

fun parse(input: List<String>): Map<String, Monkey> {
    val firstStageRegex = """(\w{4}): (.+)$""".toRegex()
    val operationRegex = """(\w{4}) ([+*/-]) (\w{4})""".toRegex()

    fun parseOperationMonkey(remainder: String): OperationMonkey {
        val (monkeyA, rawOperation, monkeyB) = operationRegex.matchEntire(remainder)!!.destructured
        return OperationMonkey(monkeyA, Operation(rawOperation), monkeyB)
    }

    fun parseMonkey(remainder: String) : Monkey =
        remainder.toLongOrNull()?.let { NumberMonkey(it) } ?: parseOperationMonkey(remainder)

    return input.associate {
        val (monkeyId, remainder) = firstStageRegex.matchEntire(it)!!.destructured
        val monkey = parseMonkey(remainder)
        Pair(monkeyId, monkey)
    }
}

sealed interface Monkey {
    fun getNumber(monkeyCache: Map<String, Monkey>): Long
}

data class NumberMonkey(val number: Long) : Monkey {
    override fun getNumber(monkeyCache: Map<String, Monkey>) = number
}

data class OperationMonkey(val monkeyAId: String, val operation: Operation, val monkeyBId: String) : Monkey {
    var resolvedValue: Long? = null
    override fun getNumber(monkeyCache: Map<String, Monkey>): Long {
        if (resolvedValue != null) { return resolvedValue!! }

        val monkeyAValue = getValueFromMonkey(monkeyAId, monkeyCache)
        val monkeyBValue = getValueFromMonkey(monkeyBId, monkeyCache)
        resolvedValue = operation.operation.invoke(monkeyAValue, monkeyBValue)
        return resolvedValue!!
    }

    private fun getValueFromMonkey(monkeyId: String, monkeyCache: Map<String, Monkey>) =
        monkeyCache[monkeyId]!!.getNumber(monkeyCache)
}

enum class Operation(val symbol: Char, val operation: (Long, Long) -> Long) {
    Plus('+', Long::plus),
    Times('*', Long::times),
    Divide('/', Long::div),
    Minus('-', Long::minus);

    companion object {
        operator fun invoke(symbol: String): Operation = Operation.values().first { it.symbol == symbol.first() }
    }
}