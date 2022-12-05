package day05

import utils.readInput

fun main() {
    val input = readInput("Day05")
    println(part1(input))
    println(part2(input))
}

fun part1(input: List<String>): String {
    val stacks = parseStacks(input)
    val operations = parseOperations(input)
    operations.forEach { operation ->
        val from = stacks[operation.from]!!
        val to = stacks[operation.to]!!
        repeat(operation.numberToMove) {
            to.addLast(from.removeLast())
        }
    }
    return joinTopEntryOnEachStackInOrder(stacks)
}

fun part2(input: List<String>): String {
    return ""
}

data class Operation(val numberToMove:Int, val from:Int, val to:Int)

val operationRegex = """\s*move (\d+) from (\d+) to (\d+)""".toRegex()

fun parseOperations(input: List<String>): List<Operation> {
    val relevantInput = input.dropWhile { it.isNotBlank() }.drop(1) //skip the blank line
    return relevantInput.map {
        val(numberToMove, from, to) = operationRegex.matchEntire(it)!!.destructured
        Operation(numberToMove.toInt(), from.toInt(), to.toInt())
    }
}

fun parseStacks(input: List<String>): Map<Int, ArrayDeque<Char>> {
    /*
        Reverse the relevant part of the list before processing
        This means we know offsets before grabbing stack items,
        and that we can insert items in a natural way for a stack.
     */
    val relevantInput = input.takeWhile { it.isNotBlank() }.asReversed()
    val stackPositions = relevantInput.first()
        .mapIndexed { index, char -> Pair(index, char) }
        .filter { it.second.isDigit() }
        .associate { Pair(it.second.digitToInt(), it.first) } //the map id to the string index

    val result = stackPositions.keys.associateWith { ArrayDeque<Char>() }
    relevantInput
        .drop(1) //we've already used this line
        .forEach { line ->
            result.entries.forEach { (key, value) ->
                val item = line[stackPositions[key]!!]
                if (item.isLetter()) {
                    value.addLast(item)
                }
            }
        }

    return result
}

private fun joinTopEntryOnEachStackInOrder(stacks: Map<Int, ArrayDeque<Char>>) = stacks.entries
    .sortedBy { it.key }
    .map { it.value.last() }
    .joinToString("")
