import util.day
import util.extensions.getBit
import util.extensions.isBitSet

// answer #1: 4006064
// answer #2: 5941884

fun main() {
    day(n = 3) {
        solution(expected = 4006064) { input ->
            val ints = input.lines.map { it.toInt(radix = 2) }
            val bits = input.lines.first().length
            ints.calculateGammaRate(bits) * ints.calculateEpsilonRate(bits)
        }

        solution(expected = 5941884) { input ->
            val ints = input.lines.map { it.toInt(radix = 2) }
            val bits = input.lines.first().length
            ints.determineOxygenRating(index = bits - 1) * ints.determineCo2Rating(index = bits - 1)
        }
    }
}

private fun List<Int>.calculateGammaRate(bits: Int): Int =
    ((bits - 1) downTo 0).fold(0) { acc, index -> acc shl 1 or processColumn(index).mostCommon() }

private fun List<Int>.calculateEpsilonRate(bits: Int): Int =
    ((bits - 1) downTo 0).fold(0) { acc, index -> acc shl 1 or processColumn(index).leastCommon() }

private fun List<Int>.determineOxygenRating(index: Int): Int {
    if (size == 1) return first()
    val value = processColumn(index).mostCommon()
    return filter { it.getBit(index) == value }.determineOxygenRating(index - 1)
}

private fun List<Int>.determineCo2Rating(index: Int): Int {
    if (size == 1) return first()
    val value = processColumn(index).leastCommon()
    return filter { it.getBit(index) == value }.determineCo2Rating(index - 1)
}

private fun List<Int>.processColumn(index: Int): OnesAndZeros =
    count { it.isBitSet(index) }.let { ones -> ones to size - ones }

typealias OnesAndZeros = Pair<Int, Int>
private fun OnesAndZeros.mostCommon(): Int = if (first < second) 0 else 1
private fun OnesAndZeros.leastCommon(): Int = if (first < second) 1 else 0
