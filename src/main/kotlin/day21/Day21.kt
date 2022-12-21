package day21

import utils.readInput
import java.math.RoundingMode
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
    val f = F(input)

    //let's see if the result is linear
    val x1 = 0L
    val x2 = 100_000_000_000_000L //we don't *quite* get the same slope with every x value, so use a YUUUGE value for x2

    val y1 = f(x1)
    val y2 = f(x2)

    //find the slope ( y2-y1/x2-x1, noting that x1 is zero) and the x-intercept
    val m = (y2 - y1).toBigDecimal().divide(x2.toBigDecimal(), 128, RoundingMode.HALF_EVEN) //use huge precision because the slope varies slightly as the x values rise
    val b = f(0)

    //predict the x that will have a y of 0. Using y = mx + b this is x = b/-m
    val predictedX = (b.toBigDecimal() / m.negate()).toLong()

    //rounding issues: try predictedX +/- a small amount just to be sure, predictedX, and predictedX + 1 — the lowest value that gives the correct result is the winner
    val xValuesToTry = (predictedX - 3).. (predictedX + 3)
    xValuesToTry.forEach {
        val actualValue = f(it)
        if (actualValue == 0L) { //verify by running with the value and hopefully getting 0
            return it
        }
    }
    throw Exception("Expected $predictedX +/- 3 to result in a value of 0, but it actually yielded ${f(xValuesToTry.first)} - ${f(xValuesToTry.last)}. Is the function actually linear?")
}

class F(val input: List<String>) {
    operator fun invoke(x: Long): Long {
        val monkeyCache = parse(input).toMutableMap()
        val oldRoot = monkeyCache["root"]!! as OperationMonkey
        //turn the final operation into a minus instead of an equals — then the check is that that result is zero...
        val newRoot = OperationMonkey(oldRoot.monkeyAId, Operation.Minus, oldRoot.monkeyBId)
        monkeyCache["humn"] = NumberMonkey(x)
        return newRoot.getNumber(monkeyCache)
    }
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
    private var resolvedValue: Long? = null
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