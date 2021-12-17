import util.Input
import util.Point
import util.day
import util.neighbors

// answer #1: 1773
// answer #2: 494

private typealias Matrix = MutableMap<Point, Int>

fun main() {
    day(n = 11) {
        part1(expected = 1773) { input ->
            val matrix = input.parseMatrix()
            generateSequence { matrix.nextStep() }.take(100).sum()
        }

        part2(expected = 494) { input ->
            val matrix = input.parseMatrix()
            1 + generateSequence { matrix.nextStep().takeUnless { matrix.isAllZeros() } }.count()
        }
    }
}

private fun Input.parseMatrix() =
    lines.flatMapIndexed { y, row -> row.mapIndexed { x, c -> Point(x, y) to c.digitToInt() } }
        .toMap()
        .toMutableMap()

private fun Matrix.nextStep() = incrementLevels().executeFlashes().resetFlashed()

private fun Matrix.incrementLevels() = onEach { set(it.key, getValue(it.key) + 1) }

private fun Matrix.executeFlashes(): Matrix {
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

private fun Matrix.resetFlashed() = filter { it.value > 9 }.onEach { set(it.key, 0) }.count()

private fun Matrix.isAllZeros() = all { it.value == 0 }
