package util

import util.extensions.minToMax

data class Point(val x: Int, val y: Int) {
    operator fun plus(point: Point) = Point(x + point.x, y + point.y)
    companion object
}

fun Point.neighbors(diagonal: Boolean = false): List<Point> =
    buildList {
        add(copy(x = x - 1))
        add(copy(x = x + 1))
        add(copy(y = y - 1))
        add(copy(y = y + 1))

        if (diagonal) {
            add(Point(x - 1, y - 1))
            add(Point(x - 1, y + 1))
            add(Point(x + 1, y - 1))
            add(Point(x + 1, y + 1))
        }
    }

operator fun Point.Companion.invoke(x: String, y: String) =
    Point(x.toInt(), y.toInt())

val Collection<Point>.xRange get() = map { it.x }.minToMax()

val Collection<Point>.yRange get() = map { it.y }.minToMax()
