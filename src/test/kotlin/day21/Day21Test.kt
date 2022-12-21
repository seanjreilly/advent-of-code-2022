package day21

import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

class Day21Test {
    private val sampleInput = """
        root: pppw + sjmn
        dbpl: 5
        cczh: sllz + lgvd
        zczc: 2
        ptdq: humn - dvpt
        dvpt: 3
        lfqf: 4
        humn: 5
        ljgn: 2
        sjmn: drzm * dbpl
        sllz: 4
        pppw: cczh / lfqf
        lgvd: ljgn * ptdq
        drzm: hmdt - zczc
        hmdt: 32
    """.trimIndent().lines()

    @Test
    fun `part1 should parse the monkeys and return the answer for 'root'`() {
        assert(part1(sampleInput) == 152L)
    }

    @Test
    fun `parse should return a map of monkey ids to monkeys`() {
        val result:Map<String, Monkey> = parse(sampleInput)

        assert(result.size == 15)
        assert(result["ljgn"] == NumberMonkey(2))
        assert(result["humn"] == NumberMonkey(5))
        assert(result["root"] == OperationMonkey("pppw", Operation.Plus, "sjmn"))
        assert(result["ptdq"] == OperationMonkey("humn", Operation.Minus, "dvpt"))
        assert(result["sjmn"] == OperationMonkey("drzm", Operation.Times, "dbpl"))
        assert(result["pppw"] == OperationMonkey("cczh", Operation.Divide, "lfqf"))

    }

    @Nested
    inner class NumberMonkeyTest {
        @Test
        fun `getNumber should return the literal value without using the monkey cache`() {
            val monkey = NumberMonkey(42)
            assert(monkey.getNumber(emptyMap()) == 42L)
        }
    }

    @Nested
    inner class OperationMonkeyTest {
        val sampleMonkeyCache = mapOf(
            "aaaa" to NumberMonkey(42),
            "bbbb" to NumberMonkey(7)
        )

        val sampleOperation = OperationMonkey("aaaa", Operation.Plus, "bbbb")

        @Test
        fun `getNumber should use the monkey cache to apply a plus operation`() {
            val operation = sampleOperation.copy(operation = Operation.Plus)
            val result = operation.getNumber(sampleMonkeyCache)
            assert(result == 49L)
        }

        @Test
        fun `getNumber should use the monkey cache to apply a times operation`() {
            val operation = sampleOperation.copy(operation = Operation.Times)
            val result = operation.getNumber(sampleMonkeyCache)
            assert(result == 42L * 7L)
        }

        @Test
        fun `getNumber should use the monkey cache to apply a divide operation`() {
            val operation = sampleOperation.copy(operation = Operation.Divide)
            val result = operation.getNumber(sampleMonkeyCache)
            assert(result == 6L)
        }

        @Test
        fun `getNumber should use the monkey cache to apply a minus operation`() {
            val operation = sampleOperation.copy(operation = Operation.Minus)
            val result = operation.getNumber(sampleMonkeyCache)
            assert(result == 42L - 7L)
        }

        @Test
        fun `getNumber should not recalculate the result once it's been calculated once`() {
            val operation = sampleOperation.copy(operation = Operation.Plus)
            val firstResult = operation.getNumber(sampleMonkeyCache)
            val secondResult = operation.getNumber(emptyMap())
            assert(secondResult == firstResult)
        }
    }
}