import util.Input
import util.day
import util.extensions.linkedList
import java.util.*

// answer #1: 360761
// answer #2: 1632779838045

private const val RESET_INDEX = 6

fun main() {
    day(n = 6) {
        solution(expected = 360761L) { input ->
            algorithm(input, days = 80)
        }

        solution(expected = 1632779838045L) { input ->
            algorithm(input, days = 256)
        }
    }
}

private fun algorithm(input: Input, days: Int): Long {
    val cycles = linkedList(9) { 0L }

    input.lines.first().split(",").map { it.toInt() }
        .groupingBy { it }
        .eachCount()
        .forEach { (index, count) -> cycles[index] = count.toLong() }

    repeat(days) { cycles.nextDay() }

    return cycles.sumOf { it }
}

private fun LinkedList<Long>.nextDay() =
    removeFirst().let { newFishCount ->
        set(RESET_INDEX, get(RESET_INDEX) + newFishCount)
        add(newFishCount)
    }
