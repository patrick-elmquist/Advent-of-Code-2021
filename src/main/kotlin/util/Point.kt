package util

data class Point(val x: Int, val y: Int)

fun Point.neighbors(): List<Point> = listOf(
    copy(x = x - 1),
    copy(x = x + 1),
    copy(y = y - 1),
    copy(y = y + 1),
)