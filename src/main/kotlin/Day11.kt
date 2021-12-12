import util.Point
import util.day
import util.neighbors

// answer #1: 1773
// answer #2: 494

fun main() {
    day(n = 11) {
        solution(expected = 1773) { input ->
            val matrix = input.lines
                .flatMapIndexed { y, row -> row.mapIndexed { x, c -> Point(x, y) to c.digitToInt() } }
                .toMap()
                .toMutableMap()

            generateSequence { matrix.step() }.take(100).sum()
        }

        solution(expected = 494) { input ->
            val matrix = input.lines
                .flatMapIndexed { y, row -> row.mapIndexed { x, c -> Point(x, y) to c.digitToInt() } }
                .toMap()
                .toMutableMap()

            1 + generateSequence { matrix.step().takeUnless { matrix.isAllZeros() } }.count()
        }
    }
}

private fun MutableMap<Point, Int>.step(): Int = incrementLevels().executeFlashes().resetFlashed()

private fun MutableMap<Point, Int>.isAllZeros(): Boolean = all { it.value == 0 }

private fun MutableMap<Point, Int>.incrementLevels(): MutableMap<Point, Int> =
    onEach { set(it.key, getValue(it.key) + 1) }

private fun MutableMap<Point, Int>.executeFlashes(): MutableMap<Point, Int> {
    val visited = mutableSetOf<Point>()
    val queue = filter { it.value > 9 }.map { it.key }.toMutableList()
    while (queue.isNotEmpty()) {
        val octopus = queue.removeFirst()

        if (octopus in visited) continue

        visited += octopus

        queue += octopus.neighbors(diagonal = true)
            .filter { neighbor -> neighbor in this }
            .onEach { neighbor -> set(neighbor, getValue(neighbor) + 1) }
            .filter { getValue(it) > 9 && it !in visited }
    }
    return this
}

private fun MutableMap<Point, Int>.resetFlashed(): Int = filter { it.value > 9 }.onEach { set(it.key, 0) }.count()
