package utils.stack

/**
 * A type alias and some extension functions to give ArrayDeque "stack-like" method names
 */
typealias Stack<T> = ArrayDeque<T>

fun <T> Stack<T>.push(element: T) = addLast(element)
fun <T> Stack<T>.pop() = removeLast()
fun <T> Stack<T>.peek() = this.last()