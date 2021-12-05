import util.day
import util.extensions.isBitSet
import util.print

// answer #1: 4006064
// answer #2: 5941884
fun main() {
    day(n = 3) {
        val test = listOf(7, 0, 5)
        check(test.processColumnInt(0) == 2 to 1) { "Index 0 didn't work" }
        check(test.processColumnInt(1) == 1 to 2) { "Index 1 didn't work" }
        check(test.processColumnInt(2) == 2 to 1) { "Index 2 didn't work" }

        testInput assert 198
        solution(expected = 4006064) { input ->
            val ints = input.lines.map { it.toInt(2) }
            val bits = input.lines.first().length
            ints.calculateGammaRate(bits) * ints.calculateEpsilonRate(bits)
        }

        testInput assert 230
        solution(expected = 5941884) { input ->
            input.lines.determineOxygenRating() * input.lines.determineCo2Rating()
        }
    }
}

private fun List<Int>.calculateGammaRate(bits: Int): Int =
    ((bits - 1) downTo 0).fold(0) { acc, index -> acc shl 1 or processColumnInt(index).mostCommon() }

private fun List<Int>.calculateEpsilonRate(bits: Int): Int =
    ((bits - 1) downTo 0).fold(0) { acc, index -> acc shl 1 or processColumnInt(index).leastCommon() }

private fun List<String>.determineOxygenRating(index: Int = 0): Int {
    if (size == 1) return first().toInt(radix = 2)
    val value = processColumn(index).mostCommon()
    return filter { it[index].digitToInt() == value }.determineOxygenRating(index + 1)
}

private fun List<String>.determineCo2Rating(index: Int = 0): Int {
    if (size == 1) return first().toInt(radix = 2)
    val value = processColumn(index).leastCommon()
    return filter { it[index].digitToInt() == value }.determineCo2Rating(index + 1)
}

private fun List<String>.processColumn(index: Int): OnesAndZeros =
    sumOf { row -> row[index].digitToInt() }.let { ones -> ones to size - ones }

private fun List<Int>.processColumnInt(index: Int): OnesAndZeros =
    count { it.isBitSet(index) }.let { ones -> ones to size - ones }

private fun OnesAndZeros.mostCommon(): Int = if (first < second) 0 else 1

private fun OnesAndZeros.leastCommon(): Int = if (first < second) 1 else 0

typealias OnesAndZeros = Pair<Int, Int>

private val testInput = """
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
