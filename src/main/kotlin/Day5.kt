import util.Point
import util.day
import util.extensions.safeRange

// answer #1: 7142
// answer #2: 20012

private val regex = """(\d+),(\d+) -> (\d+),(\d+)""".toRegex()

fun main() {
    day(n = 5) {
        solution(expected = 7142) { input ->
            input.lines
                .parseStartAndEndPoints()
                .filter { (start, end) -> start.x == end.x || start.y == end.y }
                .mapLinesToPoints()
                .groupOverlappingPoints()
                .count { it.value > 1 }
        }

        solution(expected = 20012) { input ->
            input.lines.parseStartAndEndPoints()
                .mapLinesToPoints()
                .groupOverlappingPoints()
                .count { it.value > 1 }
        }
    }
}

private fun List<String>.parseStartAndEndPoints(): List<Pair<Point, Point>> =
    mapNotNull { regex.matchEntire(it)?.destructured }
        .map { (x1, y1, x2, y2) -> Point(x1.toInt(), y1.toInt()) to Point(x2.toInt(), y2.toInt()) }

private fun List<Pair<Point, Point>>.mapLinesToPoints() =
    flatMap { (start, end) ->
        when {
            start.x == end.x -> (start.y safeRange end.y).map { start.x to it }
            start.y == end.y -> (start.x safeRange end.x).map { it to start.y }
            else -> {
                val xRange = (start.x safeRange end.x).toList()
                val yRange = (start.y safeRange end.y).toList()
                List(xRange.size) { index -> xRange[index] to yRange[index] }
            }
        }
    }

private fun List<Pair<Int, Int>>.groupOverlappingPoints() = groupingBy { it }.eachCount()
