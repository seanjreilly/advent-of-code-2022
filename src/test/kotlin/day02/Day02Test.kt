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
    fun `part1 should parse your play, your opponents play for each line, calculate the outcome, and then calculate the score for each line and return the total score`() {
        assert(part1(sampleInput) == 15)
    }

    @Test
    fun `part2 should parse your play and the desired outcome for each line, then calculate the score for each line and return the total score`() {
        assert(part2(sampleInput) == 12)
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

        @Test
        fun `findPlayForDesiredOutcome should return the play needed to get the desired outcome`() {
            OpponentsPlay.values().forEach { opponentsPlay ->
                Outcome.values().forEach { desiredOutcome ->
                    val suggestedPlay: YourPlay = opponentsPlay.findPlayForDesiredOutcome(desiredOutcome)
                    assert(suggestedPlay.outcome(opponentsPlay) == desiredOutcome)
                }
            }
        }
    }

    @Nested
    inner class OpponentsPlayTest {
        @Test
        fun `Paper should beat Rock, lose to Scissors, and draw itself`() {
            assert(OpponentsPlay.PAPER.beats == YourPlay.ROCK)
            assert(OpponentsPlay.PAPER.losesTo == YourPlay.SCISSORS)
            assert(OpponentsPlay.PAPER.draws == YourPlay.PAPER)
        }

        @Test
        fun `Rock should beat Scissors, lose to Paper, and draw itself`() {
            assert(OpponentsPlay.ROCK.beats == YourPlay.SCISSORS)
            assert(OpponentsPlay.ROCK.losesTo == YourPlay.PAPER)
            assert(OpponentsPlay.ROCK.draws == YourPlay.ROCK)
        }

        @Test
        fun `Scissors should beat Paper, lose to Rock, and draw itself`() {
            assert(OpponentsPlay.SCISSORS.beats == YourPlay.PAPER)
            assert(OpponentsPlay.SCISSORS.losesTo == YourPlay.ROCK)
            assert(OpponentsPlay.SCISSORS.draws == YourPlay.SCISSORS)
        }

    }

    @Test
    fun `score should return the score for the selected plus the score for the outcome`() {
        assert(score(YourPlay.PAPER, Outcome.Win) == 8)
        assert(score(YourPlay.ROCK, Outcome.Loss) == 1)
        assert(score(YourPlay.SCISSORS, Outcome.Draw) == 6)
    }

}

