import util.day
import util.print

// answer #1: 4006064
// answer #2: 5941884

fun main() {
    day(n = 3, failFastAssertion = true) {
        testInput assert 198
        solution(expected = 4006064) { input ->
            val gamma = input.lines.calculateMostCommon()
            val epsilon = input.lines.calculateLeastCommon()
            gamma * epsilon
        }

        testInput assert 230
        solution(expected = 5941884) { input ->
            val oxygen = input.lines.rec(0, mostCommon = true, onEqual = 1)
            val co2 = input.lines.rec(0, mostCommon = false, onEqual = 0)
            oxygen * co2
        }
    }
}

fun List<String>.calculateMostCommon(): Int =
    first().indices.fold(0) { acc, index -> acc shl 1 or processColumn(index).mostCommon() }

fun List<String>.calculateLeastCommon(): Int =
    first().indices.fold(0) { acc, index -> acc shl 1 or processColumn(index).leastCommon() }

data class Result(val ones: Int, val zeros: Int) {
    fun mostCommon(prefer: Int = 1): Int =
        when {
            ones > zeros -> 1
            ones < zeros -> 0
            else -> prefer
        }
    fun leastCommon(prefer: Int = 0): Int =
        when {
            ones > zeros -> 0
            ones < zeros -> 1
            else -> prefer
        }
}

private fun List<String>.processColumn(index: Int): Result {
    val (ones, zeros) = fold(0 to 0) { (ones, zeros), row ->
        if (row[index].digitToInt() == 0) {
            ones to zeros + 1
        } else {
            ones + 1 to zeros
        }
    }
    return Result(ones, zeros)
}

fun List<String>.rec(index: Int, mostCommon: Boolean, onEqual: Int): Int {
    if (size == 1) return first().toInt(radix = 2)

    val value = processColumn(index).let {
        if (mostCommon) {
            it.mostCommon(onEqual)
        } else {
            it.leastCommon(onEqual)
        }
    }

    return filter { it[index].digitToInt() == value }.rec(index + 1, mostCommon, onEqual)
}

val testInput = """
    00100
    11110
    10110
    10111
    10101
    01111
    00111
    11100
    10000
    11001
    00010
    01010
""".trimIndent()

