package day07

import org.junit.jupiter.api.Test
import utils.stack.*

class Day07Test {
    private val sampleInput = """
        ${'$'} cd /
        ${'$'} ls
        dir a
        14848514 b.txt
        8504156 c.dat
        dir d
        ${'$'} cd a
        ${'$'} ls
        dir e
        29116 f
        2557 g
        62596 h.lst
        ${'$'} cd e
        ${'$'} ls
        584 i
        ${'$'} cd ..
        ${'$'} cd ..
        ${'$'} cd d
        ${'$'} ls
        4060174 j
        8033020 d.log
        5626152 d.ext
        7214296 k
    """.trimIndent().lines()

    @Test
    fun `parseDirectoryTree should return a directory tree given input`() {
        val e = Dir("e")
            .add(File("i", 584))

        val d = Dir("d")
            .add(File("j", 4060174))
            .add(File("d.log", 8033020))
            .add(File("d.ext", 5626152))
            .add(File("k", 7214296))


        val a = Dir("a")
            .add(File("f", 29116))
            .add(File("g", 2557))
            .add(File("h.lst", 62596))
            .add(e)

        val expectedResult = Dir("/")
            .add(File("b.txt", 14848514))
            .add(File("c.dat", 8504156))
            .add(a)
            .add(d)

        val actualResult = parseDirectoryTree(sampleInput)

        assert(expectedResult == actualResult)
    }

}

private val IGNORE: (MatchResult) -> Unit = {}

fun parseDirectoryTree(input: List<String>): Dir {
    val rootDirectory = Dir("/")
    var currentDirectory = rootDirectory
    val stack = Stack(listOf(rootDirectory))

    val regexes = listOf<Pair<Regex, (MatchResult) -> Unit>> (
        Pair("\\\$ cd /".toRegex(), IGNORE),
        Pair("\\\$ ls".toRegex(), IGNORE),
        Pair("dir (\\w+)".toRegex()) { currentDirectory.add(Dir(it.groupValues[1])) },
        Pair("(\\d+) ([\\w\\.]+)".toRegex()) {currentDirectory.add(File(it.groupValues[2], it.groupValues[1].toLong()))},
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