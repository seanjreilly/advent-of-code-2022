package day06

import org.junit.jupiter.api.Test

class Day06Test {

    @Test
    fun `findMarker should return the number of characters read before 4 unique characters are read`() {
        assert(findMarker("mjqjpqmgbljsphdztnvjfqwrcgsmlb") == 7)
        assert(findMarker("bvwbjplbgvbhsrlpgdmjqwftvncz") == 5)
        assert(findMarker("nppdvjthqldpwncqszvftbrmjlhg") == 6)
        assert(findMarker("nznrnfrfntjfmvfwmzdfjlvtqnbhcprsg") == 10)
        assert(findMarker("zcfzfwzzqfrljwzlrfnpqdbhtmscgvjw") == 11)
    }

    @Test
    fun `part1 should return the number of characters read before 4 unique characters are read`() {
     assert(part1(listOf("mjqjpqmgbljsphdztnvjfqwrcgsmlb")) == 7)
    }
}