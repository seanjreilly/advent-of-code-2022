package day02

import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

class Day02Test {
    private val sampleInput = """
        A Y
        B X
        C Z
    """.trimIndent().lines()

    @Test
    fun `part1 should calculate the plays and outcomes for each line and return the total score`() {
        assert(part1(sampleInput) == 15)
    }
    
    @Nested
    inner class YourPlayTest {
        @Test
        fun `Rock should have a score of 1`() {
            assert(YourPlay.ROCK.score == 1)
        }

        @Test
        fun `Paper should have a score of 2`() {
            assert(YourPlay.PAPER.score == 2)
        }

        @Test
        fun `Scissors should have a score of 3`() {
            assert(YourPlay.SCISSORS.score == 3)
        }

        @Test
        fun `Paper should beat Rock`() {
            assert(YourPlay.PAPER.outcome(OpponentsPlay.ROCK) == Outcome.Win)
            assert(YourPlay.ROCK.outcome(OpponentsPlay.PAPER) == Outcome.Loss)
        }

        @Test
        fun `Rock should beat Scissors`() {
            assert(YourPlay.ROCK.outcome(OpponentsPlay.SCISSORS) == Outcome.Win)
            assert(YourPlay.SCISSORS.outcome(OpponentsPlay.ROCK) == Outcome.Loss)
        }

        @Test
        fun `Scissors should beat Paper`() {
            assert(YourPlay.SCISSORS.outcome(OpponentsPlay.PAPER) == Outcome.Win)
            assert(YourPlay.PAPER.outcome(OpponentsPlay.SCISSORS) == Outcome.Loss)
        }

        @Test
        fun `Rock, Paper, and Scissors should draw with themselves`() {
            assert(YourPlay.ROCK.outcome(OpponentsPlay.ROCK) == Outcome.Draw)
            assert(YourPlay.PAPER.outcome(OpponentsPlay.PAPER) == Outcome.Draw)
            assert(YourPlay.SCISSORS.outcome(OpponentsPlay.SCISSORS) == Outcome.Draw)
        }
    }

    @Test
    fun `score should return the score for the selected plus the score for the outcome`() {
        assert(score(YourPlay.PAPER, Outcome.Win) == 8)
        assert(score(YourPlay.ROCK, Outcome.Loss) == 1)
        assert(score(YourPlay.SCISSORS, Outcome.Draw) == 6)
    }

}

