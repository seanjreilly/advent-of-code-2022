package day11

import utils.readInput

fun main() {
    val input = readInput("Day11")
    println(part1(input))
    println(part2(input))
}

fun part1(input: List<String>): Long {
    val monkeys = parseMonkeys(input)
    processRounds(monkeys, 20, ProblemMode.PART1)
    return monkeys
        .map { it.totalNumberOfItemsConsidered }
        .sortedDescending()
        .take(2)
        .reduce(Long::times)
}

fun part2(input: List<String>): Long {
    return 0
}

enum class Operator(val operation: (Long, Long) -> Long) {
    PLUS(Long::plus),
    TIMES(Long::times)
}

data class SimpleExpression(val first: Operand, val operator: Operator, val second: Operand) {
    fun evaluate(item: Item): Long {
        return operator.operation.invoke(first.getValue(item), second.getValue(item))
    }

    companion object {
        private val parseRegex = """(old|\d+) ([*+]) (old|\d+)""".toRegex()
        fun parse(rawExpression: String): SimpleExpression {
            val (rawFirstOperand, rawOperator, rawSecondOperand) = parseRegex.matchEntire(rawExpression)!!.destructured
            val operator = when (rawOperator) {
                "+" -> Operator.PLUS
                "*" -> Operator.TIMES
                else -> { throw IllegalArgumentException("Unexpected operator $rawOperator") }
            }
            return SimpleExpression(parseOperand(rawFirstOperand), operator, parseOperand(rawSecondOperand))
        }

        private fun parseOperand(raw: String) : Operand {
            return when (raw) {
                "old" -> Old
                else -> {
                    Constant(raw.toInt())
                }
            }
        }
    }
}

//region operands
sealed interface Operand {
    fun getValue(item: Item): Long
}

data class Constant(private val value: Long) : Operand {
    override fun getValue(item: Item) = value

    constructor(value:Int) : this(value.toLong())
}

object Old : Operand {
    override fun getValue(item: Item) = item.worryLevel
}
//endregion

data class Item(val worryLevel: Long)

fun parseMonkeys(input: List<String>): List<Monkey> {
    return input
        .windowed(7, 7, true) //the last monkey doesn't have a trailing blank line
        .map { chunk -> parseMonkey(chunk) }
}

private fun parseMonkey(chunk: List<String>): Monkey {
    //ignore the first line — it just contains the monkey id, which is always the same as the position, so we don't need it
    /*
        The general pattern is to trim each line to avoid indentation,
        skip the prefix for each line with substring,
        and then process the remainder
     */
    val rawItemWorryLevels = chunk[1]
        .trim()
        .substring("Starting items: ".length)
        .split(',')
        .map { it.trim().toInt() }
    val rawExpression = chunk[2]
        .trim()
        .substring("Operation: new = ".length)
    val divisibleBy = chunk[3]
        .trim()
        .substring("Test: divisible by ".length)
        .toInt()
    val testPassesIndex = chunk[4]
        .trim()
        .substring("If true: throw to monkey ".length)
        .toInt()
    val testFailsIndex = chunk[5]
        .trim()
        .substring("If false: throw to monkey ".length)
        .toInt()
    return Monkey(rawItemWorryLevels, rawExpression, divisibleBy, testPassesIndex, testFailsIndex)
}

data class Monkey(
    val items: MutableList<Item>, //the starting items the monkey has
    val operation: SimpleExpression, //the operation expression — how worry is affected when the monkey examines an item
    val divisibleBy: Long, //the test is always "is divisible by a divisor", so just track the divisor
    val testPassesIndex: Int, //the index of the monkey the item is passed to if the test succeeds
    val testFailsIndex: Int, //the index of the monkey the item is passed to if the test fails
) {
    fun considerItems(worryAdjuster: WorryAdjuster): List<Pair<Item, Int>> {
        totalNumberOfItemsConsidered += items.size
        val oldItems = items.toList()
        items.clear()
        return oldItems
            .map { oldItem ->
                var worryLevel = operation.evaluate(oldItem) //the monkey inspects the item and edits the worry level inspection
                worryLevel = worryAdjuster.invoke(worryLevel) //the post-inspection adjustment is applied
                Item(worryLevel)
            }
            .map {
                val destination = if ((it.worryLevel % divisibleBy) == 0L) { testPassesIndex } else { testFailsIndex }
                Pair(it, destination)
            }
            .toList()
    }

    var totalNumberOfItemsConsidered = 0L
        private set

    constructor(itemWorryLevels: List<Int>, rawOperation: String, divisibleBy:Int, testPassesIndex: Int, testFailsIndex: Int):
        this(
            itemWorryLevels.map { Item(it.toLong()) }.toMutableList(),
            SimpleExpression.parse(rawOperation),
            divisibleBy.toLong(),
            testPassesIndex,
            testFailsIndex
        )
}

fun processRounds(monkeys: List<Monkey>, numberOfRounds: Int, problemMode:ProblemMode = ProblemMode.PART1) {
    val worryAdjuster = when (problemMode) {
        ProblemMode.PART1 -> PART1_WORRY_ADJUSTER
        ProblemMode.PART2 -> TODO()
    }
    repeat(numberOfRounds) { round ->
        if (round > 0 && (round % 50 == 0)) {
            println("Processing round $round")
        }
        monkeys.forEach { monkey ->
            monkey.considerItems(worryAdjuster).forEach { (item, destinationMonkey) ->
                monkeys[destinationMonkey].items.add(item)
            }
        }
    }
}

enum class ProblemMode {
    PART1,
    PART2
}

typealias WorryAdjuster = (Long) -> Long
val PART1_WORRY_ADJUSTER: WorryAdjuster = { it / 3L }