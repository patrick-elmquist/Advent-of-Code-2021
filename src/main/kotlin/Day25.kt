
import util.Point
import util.day

// answer #1: 516
// answer #2:

fun main() {
    day(n = 25) {
        part1(expected = 516) { input ->
            val state = input.lines.flatMapIndexed { y, line ->
                line.mapIndexedNotNull { x, c ->
                    if (c != '.') Point(x, y) to c else null
                }
            }.toMap()

            val width = input.lines.first().length
            val height = input.lines.size

            state.sequence(width, height).count()
        }
    }
}

private fun Map<Point, Char>.sequence(width: Int, height: Int) =
    generateSequence(this) { state ->
        val newState = state.toMutableMap()

        val movingRight = state
            .filter { it.value == '>' }
            .mapNotNull { (p, _) ->
                val next = p.copy(x = (p.x + 1) % width)
                if (next !in state) p to next else null
            }
        movingRight.forEach { (from, to) -> newState[to] = newState.remove(from)!! }

        val movingDown = newState
            .filter { it.value == 'v' }
            .mapNotNull { (p, _) ->
                val next = p.copy(y = (p.y + 1) % height)
                if (next !in newState) p to next else null
            }
        movingDown.forEach { (from, to) -> newState[to] = newState.remove(from)!! }

        if (movingDown.size + movingRight.size > 0) newState else null
    }