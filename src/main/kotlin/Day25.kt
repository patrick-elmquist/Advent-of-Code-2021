
import util.Point
import util.day
import util.log

// answer #1:
// answer #2:

fun main() {
    day(n = 25) {
        """
v...>>.vv>
.vv>>.vv..
>>.>v>...v
>>v>>.>.v.
v>v.vv.v..
>.>>..v...
.vv..>.>v.
v.v..>>v.v
....v..v.>
""".trimIndent() expect 58
        part1 { input ->
            var state = input.lines.flatMapIndexed { y, line ->
                line.mapIndexedNotNull { x, c ->
                    if (c != '.') {
                        Point(x, y) to c
                    } else {
                        null
                    }
                }
            }.toMap()

            val width = input.lines.first().lastIndex.log("width:")
            val height = input.lines.lastIndex.log("height:")

            var counter = 0
            while (true) {
                val movingRight = state
                    .filter { it.value == '>' }
                    .mapNotNull { (p, _) ->
                        var next = p.copy(x = p.x + 1)
                        if (next.x > width) next = p.copy(x = 0)
                        if (next !in state) {
                            p to next
                        } else {
                            null
                        }
                    }

                val newState = state.toMutableMap()
                movingRight.forEach { (from, to) ->
                    val c = newState.remove(from)!!
                    newState[to] = c
                }

                val movingDown = newState
                    .filter { it.value == 'v' }
                    .mapNotNull { (p, _) ->
                        var next = p.copy(y = p.y + 1)
                        if (next.y > height) next = p.copy(y = 0)
                        if (next !in newState) {
                            p to next
                        } else {
                            null
                        }
                    }

                movingDown.forEach { (from, to) ->
                    val c = newState.remove(from)!!
                    newState[to] = c
                }


                state = newState
                counter ++
                if (movingDown.size + movingRight.size == 0) break
            }

            counter
        }
    }
}


private fun Map<Point, Char>.print(width: Int, height: Int) {
    (0..height).forEach { y ->
        (0..width).forEach { x ->
            val c = get(Point(x, y))
            print("${c ?: '.'} ")
        }
        println()
    }
}