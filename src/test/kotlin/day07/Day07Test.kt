package day07

import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

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
    fun `part1 should parse the directory tree and return the sum of the total sizes of all directories with a total size of at most 100000 `() {
        assert(part1(sampleInput) == 95437L)
    }
    
    @Test
    fun `part2 should return the total size of the smallest directory that would take free space to at least 30000000`() {
        assert(part2(sampleInput) == 24933642L)
    }
    
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

    @Nested
    inner class DirTest {
        @Test
        fun `totalSize should return the total size of all files in this directory or a subdirectory`() {
            val e = Dir("e")
                .add(File("i", 584))
            val a = Dir("a")
                .add(File("f", 29116))
                .add(File("g", 2557))
                .add(File("h.lst", 62596))
                .add(e)
            val root = Dir("/")
                .add(a)

            assert(e.totalSize == 584L)
            assert(a.totalSize == 94853L)
            assert(root.totalSize == 94853L)
        }

        @Test
        fun `subDirs should return the set of this directory and all directories below this one`() {
            val rootDir = parseDirectoryTree(sampleInput)

            val result:Collection<Dir> = rootDir.subDirs()

            assert(result.size == 4)
            assert(result.contains(rootDir))
            assert(result.contains(rootDir.cd("a")))
            assert(result.contains(rootDir.cd("a").cd("e")))
            assert(result.contains(rootDir.cd("d")))
        }
    }
}