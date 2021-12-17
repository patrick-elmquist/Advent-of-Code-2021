import util.Point
import util.day
import util.extensions.match

// answer #1: 7142
// answer #2: 20012

private val PARSE_REGEX = """(\d+),(\d+) -> (\d+),(\d+)""".toRegex()

fun main() {
    day(n = 5) {
        part1(expected = 7142) { input ->
            input.lines
                .parseStartAndEndPoints()
                .filter { (start, end) -> start.x == end.x || start.y == end.y }
                .mapLinesToPoints()
                .groupOverlappingPoints()
                .count { it.value > 1 }
        }

        part2(expected = 20012) { input ->
            input.lines.parseStartAndEndPoints()
                .mapLinesToPoints()
                .groupOverlappingPoints()
                .count { it.value > 1 }
        }
    }
}

private fun List<String>.parseStartAndEndPoints(): List<Pair<Point, Point>> =
    map { PARSE_REGEX.match(it) { (x1, y1, x2, y2) -> Point(x1.toInt(), y1.toInt()) to Point(x2.toInt(), y2.toInt()) } }

private fun List<Pair<Point, Point>>.mapLinesToPoints() =
    flatMap { (start, end) ->
        when {
            start.x == end.x -> (start.y range end.y).map { start.x to it }
            start.y == end.y -> (start.x range end.x).map { it to start.y }
            else -> (start.x range end.x).zip(start.y range end.y)
        }
    }

private fun List<Pair<Int, Int>>.groupOverlappingPoints() =
    groupingBy { it }.eachCount()

private infix fun Int.range(other: Int) =
    if (this < other) this..other else this downTo other
