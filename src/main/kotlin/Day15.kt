
import util.day
import java.util.PriorityQueue
import kotlin.math.sqrt

// answer #1: 656
// answer #2: 2979

fun main() {
    day(n = 15) {
        solution(expected = 656) { input ->
            val lines = input.lines
            val array = lines.flatMap { row -> row.map { it.digitToInt() } }.toIntArray()
            findLeastRiskyPath(array)
        }

        solution(expected = 2979) { input ->
            val extend = 5
            val size = input.lines.size
            val extendedSize = size * extend
            val array = IntArray(extendedSize * extendedSize) { 0 }

            fun Int.wrap(): Int = if (this > 9) 1 + this % 10 else this

            input.lines.forEachIndexed { y, row ->
                row.forEachIndexed { x, c ->
                    val risk = c.digitToInt()
                    (0 until extend).forEach { ey ->
                        (0 until extend).forEach { ex ->
                            val index = (y + ey * size) * extendedSize + (x + ex * size)
                            array[index] = (risk + ex + ey).wrap()
                        }
                    }
                }
            }

            findLeastRiskyPath(array)
        }
    }
}

private fun findLeastRiskyPath(array: IntArray): Int {
    val scratch = IntArray(array.size) { Int.MAX_VALUE }
    scratch[0] = 0

    val queue = PriorityQueue<Int>(compareBy { scratch[it] })
    queue.add(0)

    val size = sqrt(array.size.toFloat()).toInt()
    val validRange = 0 until size

    fun neighbors(index: Int): List<Int> =
        buildList {
            val x = index % size
            val y = index / size

            if (x - 1 in validRange) add(y * size + (x - 1))
            if (x + 1 in validRange) add(y * size + (x + 1))
            if (y - 1 in validRange) add((y - 1) * size + x)
            if (y + 1 in validRange) add((y + 1) * size + x)
        }

    while (queue.isNotEmpty()) {
        val index = queue.remove()
        neighbors(index).forEach { neighbor ->
            val risk = scratch[index] + array[neighbor]
            if (risk < scratch[neighbor]) {
                scratch[neighbor] = risk
                queue.add(neighbor)
            }
        }
    }

    return scratch.last()
}
