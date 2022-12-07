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