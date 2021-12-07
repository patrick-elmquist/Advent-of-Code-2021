import util.Input
import util.day
import util.extensions.toInts
import kotlin.math.abs

// answer #1: 342730
// answer #2: 92335207

fun main() {
    day(n = 7) {
        solution(expected = 342730) { input ->
            val (crabs, positions) = input.parse()
            positions.minOfOrNull { crabs.constantCostToPoint(it) }
        }

        solution(expected = 92335207) { input ->
            val (crabs, positions) = input.parse()
            positions.minOfOrNull { crabs.increasingCostToPoint(it) }
        }
    }
}

private fun Input.parse(): Pair<List<Int>, IntRange> =
    lines.first().split(",").toInts().sorted().let { crabs ->
        crabs to (crabs.first() .. crabs.last())
    }

private fun List<Int>.constantCostToPoint(target: Int): Int =
    sumOf { abs(target - it) }

private fun List<Int>.increasingCostToPoint(target: Int): Int =
    map { abs(target - it) }.sumOf { it * (it + 1) / 2 }
