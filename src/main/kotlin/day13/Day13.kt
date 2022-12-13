package day13

import utils.readInput
import utils.stack.*

fun main() {
    val input = readInput("Day13")
    println(part1(input))
    println(part2(input))
}

fun part1(input: List<String>): Long {
    return 0
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

/*
fun checkOrder(left: List<Any>, right:Int) {
    println("comparing a List and an Int")
}

fun checkOrder(left: Int, right: List<Any>) {
    println("comparing an Int and a List")
}

fun checkOrder(left:List<Any>, right:List<Any>) {
    println("comparing a List and a List")
}

fun checkOrder(left: Int, right: Int) {
    println("comparing an Int and an Int")
}
enum class CheckResult {
    RightOrder,
    WrongOrder,
    ContinueChecking
}*/
