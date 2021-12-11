import util.Point
import util.day
import util.neighbors

// answer #1: 1773
// answer #2: 494

fun main() {
    day(n = 11) {
        solution(expected = 1773) { input ->
            val matrix = input.lines.flatMapIndexed { y, row ->
                row.mapIndexed { x, c ->
                    Point(x, y) to c.digitToInt()
                }
            }.toMap().toMutableMap()

            val width = input.lines.first().length
            val height = input.lines.size
            matrix.print(width, height)

            var flashCount = 0
            repeat(100) {
                flashCount += matrix.step(width, height)
            }

            flashCount
        }

       testInputLarge assert 195
        solution(expected = 494) { input ->
            val matrix = input.lines.flatMapIndexed { y, row ->
                row.mapIndexed { x, c ->
                    Point(x, y) to c.digitToInt()
                }
            }.toMap().toMutableMap()

            val width = input.lines.first().length
            val height = input.lines.size

            var count = 0
            while (!matrix.check()) {
                matrix.step(width, height)
                count += 1
            }

            count
        }
    }
}

private fun MutableMap<Point, Int>.check(): Boolean = all { it.value == 0 }

private fun MutableMap<Point, Int>.step(width: Int, height: Int): Int {
    val matrix = this
    matrix.inc()
    val highlights = matrix.filter { it.value > 9 }.map { it.key }.toMutableList()
    val visited = mutableSetOf<Point>()
    while (highlights.isNotEmpty()) {
        val octopus = highlights.removeFirst()

        if (octopus in visited) continue

        visited += octopus

        octopus.neighbors(diagonal = true)
            .filter { it in matrix }
            .onEach { matrix[it] = 1 + (matrix[it] ?: 0) }
            .filter { matrix.getOrDefault(it, 0) > 9 }
            .filter { it !in visited }
            .forEach { highlights.add(it) }
    }

    matrix.filter { it.value > 9 }
        .forEach {
            matrix[it.key] = 0
        }

    matrix.print(width, height)
    return visited.size
}
private fun MutableMap<Point, Int>.print(width: Int, height: Int) {
    if (true) return
    (0 until height).forEach { y ->
        (0 until width).forEach { x ->
            print(get(Point(x, y)).toString() + " ")
        }
        println()
    }
    println()
}
private fun MutableMap<Point, Int>.inc() = forEach {
   set(it.key, getOrDefault(it.key, 0) + 1)
}

private val testInput = """
    11111
    19991
    19191
    19991
    11111
""".trimIndent()

private val testInputLarge = """
    5483143223
    2745854711
    5264556173
    6141336146
    6357385478
    4167524645
    2176841721
    6882881134
    4846848554
    5283751526
""".trimIndent()