package day11

import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

class Day11Test {
    val sampleInput = """
        Monkey 0:
          Starting items: 79, 98
          Operation: new = old * 19
          Test: divisible by 23
            If true: throw to monkey 2
            If false: throw to monkey 3
        
        Monkey 1:
          Starting items: 54, 65, 75, 74
          Operation: new = old + 6
          Test: divisible by 19
            If true: throw to monkey 2
            If false: throw to monkey 0
        
        Monkey 2:
          Starting items: 79, 60, 97
          Operation: new = old * old
          Test: divisible by 13
            If true: throw to monkey 1
            If false: throw to monkey 3
        
        Monkey 3:
          Starting items: 74
          Operation: new = old + 3
          Test: divisible by 17
            If true: throw to monkey 0
            If false: throw to monkey 1
    """.trimIndent().lines()

    @Test
    fun `processRounds should pass items from monkey to monkey for the specified number of rounds in part1 mode`() {
        val monkeys = parseMonkeys(sampleInput)
        processRounds(monkeys, 20)

        assert(monkeys[0].items == listOf(10, 12, 14, 26, 34).map { it.toLong() }.map(::Item))
        assert(monkeys[1].items == listOf(245, 93, 53, 199, 115).map { it.toLong() }.map(::Item))
        assert(monkeys[2].items == emptyList<Item>())
        assert(monkeys[3].items == emptyList<Item>())

        assert(monkeys[0].totalNumberOfItemsConsidered == 101L)
        assert(monkeys[1].totalNumberOfItemsConsidered == 95L)
        assert(monkeys[2].totalNumberOfItemsConsidered == 7L)
        assert(monkeys[3].totalNumberOfItemsConsidered == 105L)
    }

    @Test
    fun `processRounds should also work for an extended number of rounds in Part 2 mode`() {
        val monkeys = parseMonkeys(sampleInput)
        processRounds(monkeys, 10000, ProblemMode.PART2)

        assert(monkeys[0].totalNumberOfItemsConsidered == 52166L)
        assert(monkeys[1].totalNumberOfItemsConsidered == 47830L)
        assert(monkeys[2].totalNumberOfItemsConsidered == 1938L)
        assert(monkeys[3].totalNumberOfItemsConsidered == 52013L)
    }
    
    @Test
    fun `part1 should process 20 rounds in part 1 mode and then return the product of the largest 2 numbers of items considered`() {
        assert(part1(sampleInput) == 10605L)
    }

    @Test
    fun `part2 should process 20 rounds in part 2 mode and then return the product of the largest 2 numbers of items considered`() {
        assert(part2(sampleInput) == 2713310158L)
    }

    @Test
    fun `parseMonkeys should return a list of Monkey instances`() {
        val monkeys: List<Monkey> = parseMonkeys(sampleInput)

        val expectedResult = listOf(
            Monkey(listOf(79,98),"old * 19", 23, 2, 3),
            Monkey(listOf(54, 65, 75, 74), "old + 6", 19, 2, 0),
            Monkey(listOf(79, 60, 97), "old * old", 13, 1, 3),
            Monkey(listOf(74), "old + 3", 17, 0, 1)
        )

        assert(monkeys.size == 4)
        assert(monkeys.first() == expectedResult.first())
        assert(monkeys[1] == expectedResult[1])
        assert(monkeys[2] == expectedResult[2])
        assert(monkeys[3] == expectedResult[3])
    }

    @Nested
    inner class SimpleExpressionTest {
        @Test
        fun `parse should return an addition expression given an expression in String form`() {
            val rawExpression = "old + 3"
            val result = SimpleExpression.parse(rawExpression)

            val expectedResult = SimpleExpression(Old, Operator.PLUS, Constant(3))
            assert(expectedResult == result)
        }

        @Test
        fun `parse should successfully parse all expressions in the simple example`() {
            assert(SimpleExpression.parse("old * 19") == SimpleExpression(Old, Operator.TIMES, Constant(19)))
            assert(SimpleExpression.parse("old + 6") == SimpleExpression(Old, Operator.PLUS, Constant(6)))
            assert(SimpleExpression.parse("old * old") == SimpleExpression(Old, Operator.TIMES, Old))
            assert(SimpleExpression.parse("old + 3") == SimpleExpression(Old, Operator.PLUS, Constant(3)))
        }

        @Test
        fun `evaluate should correctly evaluate an expression given a value`() {
            val item = Item(2)
            assert(SimpleExpression(Old, Operator.TIMES, Constant(19)).evaluate(item) == 38L)
            assert(SimpleExpression(Old, Operator.PLUS, Constant(6)).evaluate(item) == 8L)
            assert(SimpleExpression(Old, Operator.TIMES, Old).evaluate(item) == 4L)
            assert(SimpleExpression(Old, Operator.PLUS, Constant(3)).evaluate(item) == 5L)

            //and try one with a different item just to be sure
            assert(SimpleExpression(Old, Operator.TIMES, Old).evaluate(Item(9)) == 81L)
        }
    }

    @Nested
    inner class MonkeyTest {
        @Test
        fun `considerItems should return a list of modified items and destination monkeys`() {
            val monkey = Monkey(
                listOf(54, 65, 75, 51), //modify Monkey 1 from the example slightly so we trigger it's true destination
                "old + 6",
                19,
                2,
                0
            )

            val results:List<Pair<Item, Int>> = monkey.considerItems(PART1_WORRY_ADJUSTER)

            val expectedResults = listOf(
                Pair(Item(20), 0),
                Pair(Item(23), 0),
                Pair(Item(27), 0),
                Pair(Item(19), 2)
            )

            assert(results.size == expectedResults.size)
            expectedResults.forEachIndexed { index, expectedResult ->
                assert(expectedResult.first == results[index].first)
                assert(expectedResult.second == results[index].second)
            }
        }

        @Test
        fun `considerItems should clear the current items the monkey has`() {
            val monkey = parseMonkeys(sampleInput)[1]
            assert(monkey.items.isNotEmpty()) { "precondition: the monkey must have items before we consider"}

            monkey.considerItems(PART1_WORRY_ADJUSTER)

            assert(monkey.items.isEmpty())
        }

        @Test()
        fun `considerItems should increment the total number of items this monkey has considered every time it is called`() {
            val monkey = parseMonkeys(sampleInput)[1]
            val initialItemsSize = monkey.items.size
            assert(initialItemsSize > 0) { "precondition: the monkey must have items before we consider"}

            monkey.considerItems(PART1_WORRY_ADJUSTER)

            assert(monkey.totalNumberOfItemsConsidered == initialItemsSize.toLong())

            //give the monkey two more items
            val extraItems = listOf(Item(1), Item(2))
            monkey.items.addAll(extraItems)

            monkey.considerItems(PART1_WORRY_ADJUSTER)
            assert(monkey.totalNumberOfItemsConsidered == (initialItemsSize + extraItems.size).toLong())
        }
    }
}