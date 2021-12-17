import util.Input
import util.Point
import util.day
import util.neighbors

// answer #1: 508
// answer #2: 1564640

fun main() {
    day(n = 9) {
        part1(expected = 508) { input ->
            val points = input.parsePointHeightMap()
            points.filter { (point, height) ->
                point.neighbors()
                    .mapNotNull { points[it] }
                    .all { height < it }
            }.values.sumOf { height -> 1 + height }
        }

        part2(expected = 1564640) { input ->
            val points = input.parsePointHeightMap()
            val visited = mutableSetOf<Point>()
            points.keys
                .map { point -> points.calculateBasinForPoint(point, visited) }
                .sorted()
                .takeLast(3)
                .reduce { a, b -> a * b }
        }
    }
}

private fun Input.parsePointHeightMap(): Map<Point, Int> =
    lines.flatMapIndexed { y, row ->
        row.mapIndexed { x, height -> Point(x, y) to height.digitToInt() }
    }.toMap()

private fun Map<Point, Int>.calculateBasinForPoint(point: Point, visited: MutableSet<Point>): Int =
    when {
        point in visited -> 0
        point !in this -> 0
        get(point) == 9 -> 0
        else -> {
            visited += point
            1 + point.neighbors().sumOf { calculateBasinForPoint(it, visited) }
        }
    }
