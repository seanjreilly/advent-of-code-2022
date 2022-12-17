package day07

import utils.readInput
import utils.stack.*
import kotlin.system.measureTimeMillis

fun main() {
    val elapsed = measureTimeMillis {
        val input = readInput("Day07")
        println(part1(input))
        println(part2(input))
    }
    println()
    println("Elapsed time: $elapsed ms.")
}

fun part1(input: List<String>): Long {
    val rootDir = parseDirectoryTree(input)
    return rootDir
        .subDirs()
        .filter { it.totalSize <= 100000 }
        .sumOf { it.totalSize }
}

fun part2(input: List<String>): Long {
    val rootDir = parseDirectoryTree(input)
    val totalSpace = 70000000L
    val freeSpace = totalSpace - rootDir.totalSize
    val neededSpace = 30000000L - freeSpace

    return rootDir
        .subDirs()
        .filter { it.totalSize >= neededSpace }
        .minBy { it.totalSize }
        .totalSize
}

fun parseDirectoryTree(input: List<String>): Dir {
    val ignore: (MatchResult) -> Unit = {}

    val rootDirectory = Dir("/")
    var currentDirectory = rootDirectory
    val stack = Stack(listOf(rootDirectory))

    val regexes = listOf<Pair<Regex, (MatchResult) -> Unit>> (
        Pair("\\\$ cd /".toRegex(), ignore),
        Pair("\\\$ ls".toRegex(), ignore),
        Pair("dir (\\w+)".toRegex()) { currentDirectory.add(Dir(it.groupValues[1])) },
        Pair("(\\d+) ([\\w.]+)".toRegex()) {currentDirectory.add(File(it.groupValues[2], it.groupValues[1].toLong()))},
        Pair("\\\$ cd \\.\\.".toRegex()) { currentDirectory = stack.pop() },
        Pair("\\\$ cd (\\w+)".toRegex()) {
            stack.push(currentDirectory)
            currentDirectory = currentDirectory.cd(it.groupValues[1])
        }
    )

    input.drop(1) //we've already got '/' as the first directory
        .forEach { line ->
            val operation = regexes.asSequence()
                .map { Pair(it.first.matchEntire(line), it.second) }
                .find { it.first != null }!!
            operation.second(operation.first!!)
        }
    return rootDirectory
}

class Dir(val name: String) {
    private val files = mutableSetOf<File>()
    private val dirs = mutableSetOf<Dir>()

    fun add(file:File) = apply {
        files.add(file)
    }

    fun add(dir:Dir) = apply {
        dirs.add(dir)
    }

    fun cd(name: String) = dirs.first { it.name == name }

    fun subDirs(): Collection<Dir> = dirs.flatMap { it.subDirs() } + this

    val totalSize: Long by lazy {
        files.sumOf { it.size } + dirs.sumOf { it.totalSize }
    }

    //region equals and hash code
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Dir

        if (name != other.name) return false
        if (files != other.files) return false
        if (dirs != other.dirs) return false

        return true
    }

    override fun hashCode(): Int {
        return name.hashCode()
    }
    //endregion
}

data class File(val name:String, val size:Long)