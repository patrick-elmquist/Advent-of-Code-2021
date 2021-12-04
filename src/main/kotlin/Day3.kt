import util.day

// answer #1: 4006064
// answer #2: 5941884

fun main() {
    day(n = 3) {
        testInput assert 198
        solution(expected = 4006064) { input ->
            input.lines.calculateGammaRate() * input.lines.calculateEpsilonRate()
        }

        testInput assert 230
        solution(expected = 5941884) { input ->
            input.lines.determineOxygenRating() * input.lines.determineCo2Rating()
        }
    }
}

private fun List<String>.calculateGammaRate(): Int =
    first().indices.fold(0) { acc, index -> acc shl 1 or processColumn(index).mostCommon() }

private fun List<String>.calculateEpsilonRate(): Int =
    first().indices.fold(0) { acc, index -> acc shl 1 or processColumn(index).leastCommon() }

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
