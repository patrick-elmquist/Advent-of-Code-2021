
import util.day
import java.util.PriorityQueue
import kotlin.math.sqrt

// answer #1: 656
// answer #2: 2979

fun main() {
    day(n = 15) {
        testInputLarge assert 40
        solution(expected = 656) { input ->
            val lines = input.lines
            val array = lines.flatMap { it.map { it.digitToInt() } }.toTypedArray()
            findLeastRiskyPath(array)
        }

        testInputLarge assert 315
        solution(expected = 2979) { input ->
            val lines = input.lines
            val extend = 5
            val width = lines.first().length
            val height = lines.size
            val array = Array(width * extend * height * extend) { 0 }
            lines.forEachIndexed { y, row ->
                row.forEachIndexed { x, c ->
                    (0 until extend).forEach { ey ->
                        (0 until extend).forEach { ex ->
                            val risk = (c.digitToInt() + ex + ey).let { if (it > 9) 1 + it % 10 else it }
                            array[(y + ey * height) * width * extend + (x + ex * width)] = risk
                        }
                    }
                }
            }

            findLeastRiskyPath(array)
        }
    }
}

private fun findLeastRiskyPath(matrix: Array<Int>): Int {
    val scratch = IntArray(matrix.size) { Int.MAX_VALUE }
    scratch[0] = 0

    val queue = PriorityQueue<Int>(compareBy { scratch[it] })
    queue.add(0)

    val size = sqrt(matrix.size.toFloat()).toInt()

    fun neighbors(index: Int): List<Int> =
        buildList {
            val x = index % size
            val y = index / size

            if (x - 1 in 0 until size) add(y * size + (x - 1))
            if (x + 1 in 0 until size) add(y * size + (x + 1))
            if (y - 1 in 0 until size) add((y - 1) * size + x)
            if (y + 1 in 0 until size) add((y + 1) * size + x)
        }

    while (queue.isNotEmpty()) {
        val index = queue.remove()
        neighbors(index).forEach { neighbor ->
            val risk = scratch[index] + matrix[neighbor]
            if (risk < scratch[neighbor]) {
                scratch[neighbor] = risk
                queue.add(neighbor)
            }
        }
    }

    return scratch.last()
}

private val testInputLarge = """
1163751742
1381373672
2136511328
3694931569
7463417111
1319128137
1359912421
3125421639
1293138521
2311944581
""".trimIndent()
