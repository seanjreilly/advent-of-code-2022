package day13

import utils.readInput
import utils.stack.*

fun main() {
    val input = readInput("Day13")
    println(part1(input))
    println(part2(input))
}

fun part1(input: List<String>): Long {
    return input.windowed(3, 3, true) //skip the blank lines
        .map { lines ->
            val (left, right) = lines
            checkOrder(parse(left), parse(right))
        }
        .mapIndexed(::Pair)
        .filter { it.second == CheckResult.CorrectOrder }
        .sumOf { it.first.toLong() + 1 } //the problem considers the indices to be 1-based
}

fun part2(input: List<String>): Long {
    return 0
}

fun parse(input:String) : List<Any> {
    return parseListContents(input.substring(1, input.length - 1))
}

private fun parseListContents(input:String) : List<Any> {
    if (input.isEmpty()) {
        return emptyList()
    }

    //use the stack to read until a top-level comma is reached
    val stack = Stack<Char>()
    val result = mutableListOf<Any>()
    var startIndex = 0

    fun parseChunk(chunk:String) {
        //the chunk is either a list or an int
        if (chunk.startsWith('[')) {
            result.add(parse(chunk))
        } else {
            result.add(chunk.toInt())
        }
    }

    input.forEachIndexed { index, char ->
        when (char) {
            '[' -> stack.push('[')
            ']' -> stack.pop()
            ',' -> {
                if (stack.isEmpty()) {
                    val chunk = input.substring(startIndex, index)
                    parseChunk(chunk)
                    startIndex = index + 1
                }
            }
        }
    }
    val chunk = input.substring(startIndex, input.length)
    parseChunk(chunk)
    return result
}

fun checkOrder(left: List<Any>, right:Int) : CheckResult {
    return checkOrder(left, listOf(right))
}


fun checkOrder(left: Int, right: List<Any>) : CheckResult {
    return checkOrder(listOf(left), right)
}

@Suppress("UNCHECKED_CAST")
fun checkOrder(left:List<Any>, right:List<Any>): CheckResult {
    if (left.isEmpty()) {
        return if (right.isNotEmpty()) { CheckResult.CorrectOrder } else { CheckResult.ContinueChecking }
    }
    if (right.isEmpty()) {
        return CheckResult.WrongOrder
    }
    val result: CheckResult
    val leftItem = left.first()
    val rightItem = right.first()
    if (leftItem is Int) {
        if (rightItem is Int) {
            result = checkOrder(leftItem, rightItem)
        } else {
            result = checkOrder(leftItem, rightItem as List<Any>)
        }
    } else {
         if (rightItem is Int) {
             result = checkOrder(leftItem as List<Any>, rightItem)
         } else {
             result = checkOrder(leftItem as List<Any>, rightItem as List<Any>)
         }
    }
    if (result != CheckResult.ContinueChecking) {
        return result
    }
    return checkOrder(left.drop(1), right.drop(1))
}


fun checkOrder(left: Int, right: Int) : CheckResult {
    return when {
        left < right -> CheckResult.CorrectOrder
        right < left -> CheckResult.WrongOrder
        else -> CheckResult.ContinueChecking
    }
}
enum class CheckResult {
    CorrectOrder,
    WrongOrder,
    ContinueChecking
}