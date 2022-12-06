package day06

import org.junit.jupiter.api.Test

class Day06Test {

    @Test
    fun `findMarker should return the number of characters read before N unique characters are read`() {
        assert(findMarker("mjqjpqmgbljsphdztnvjfqwrcgsmlb", 4) == 7)
        assert(findMarker("bvwbjplbgvbhsrlpgdmjqwftvncz", 4) == 5)
        assert(findMarker("nppdvjthqldpwncqszvftbrmjlhg", 4) == 6)
        assert(findMarker("nznrnfrfntjfmvfwmzdfjlvtqnbhcprsg", 4) == 10)
        assert(findMarker("zcfzfwzzqfrljwzlrfnpqdbhtmscgvjw", 4) == 11)

        assert(findMarker("mjqjpqmgbljsphdztnvjfqwrcgsmlb", 14) == 19)
        assert(findMarker("bvwbjplbgvbhsrlpgdmjqwftvncz", 14) == 23)
        assert(findMarker("nppdvjthqldpwncqszvftbrmjlhg", 14) == 23)
        assert(findMarker("nznrnfrfntjfmvfwmzdfjlvtqnbhcprsg", 14) == 29)
        assert(findMarker("zcfzfwzzqfrljwzlrfnpqdbhtmscgvjw", 14) == 26)
    }

    @Test
    fun `part1 should return the number of characters read before 4 unique characters are read`() {
        assert(part1("mjqjpqmgbljsphdztnvjfqwrcgsmlb") == 7)
    }

    @Test
    fun `part2 should return the number of characters read before 14 unique characters are read`() {
        assert(part2("mjqjpqmgbljsphdztnvjfqwrcgsmlb") == 19)
    }
}