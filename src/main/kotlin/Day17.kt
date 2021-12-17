
import util.Input
import util.Point
import util.day
import util.extensions.match
import kotlin.math.max

// answer #1: 10011
// answer #2: 2994

fun main() {
    day(n = 17) {

        part1(expected = 10011) { input ->
            val (_, targetY) = input.parseTarget()
            val maxY = -targetY.first - 1
            (maxY + 1) * maxY / 2
        }

        part2(expected = 2994) { input ->
            val (targetX, targetY) = input.parseTarget()
            val minX = calculateMinX(from = targetX.first)
            val maxX = targetX.last
            val minMaxX = minX..maxX

            val minY = targetY.first
            val maxY = -targetY.first - 1
            val minMaxY = minY..maxY

            val start = Point(0, 0)
            minMaxX.sumOf { x ->
                minMaxY.count { y ->
                    evaluate(start, Point(x, y), targetX, targetY)
                }
            }
        }
    }
}

private fun Input.parseTarget(): Pair<IntRange, IntRange> =
    """target area: x=(-?\d+)..(-?\d+), y=(-?\d+)..(-?\d+)""".toRegex()
        .match(single) { (x1, x2, y1, y2) ->
            x1.toInt()..x2.toInt() to y1.toInt()..y2.toInt()
        }

private fun evaluate(
    point: Point,
    velocity: Point,
    targetX: IntRange,
    targetY: IntRange
): Boolean =
    when {
        point.x in targetX && point.y in targetY -> true
        point.x > targetX.last -> false
        point.y < targetY.first -> false
        else -> evaluate(
            point = point + velocity,
            velocity = Point(max(0, velocity.x - 1), velocity.y - 1),
            targetX,
            targetY
        )
    }

private fun calculateMinX(from: Int): Int {
    var step = 1
    var x = from
    while (x > 0) x -= step++
    return step - 1
}
