package util

import util.extensions.minToMax

data class Point(val x: Int, val y: Int) {
    operator fun plus(point: Point) = Point(x + point.x, y + point.y)
    companion object
}

data class Point3D(val x: Int, val y: Int, val z: Int) {
    operator fun plus(point: Point3D) = Point3D(x + point.x, y + point.y, z + point.z)
    operator fun minus(point: Point3D) = Point3D(x - point.x, y - point.y, z - point.z)
    companion object
}

fun Point.neighbors(diagonal: Boolean = false, includeSelf: Boolean = false): List<Point> =
    buildList {
        if (diagonal) add(Point(x - 1, y - 1))
        add(copy(y = y - 1))
        if (diagonal) add(Point(x + 1, y - 1))

        add(copy(x = x - 1))
        if (includeSelf) add(this@neighbors)
        add(copy(x = x + 1))

        if (diagonal) add(Point(x - 1, y + 1))
        add(copy(y = y + 1))
        if (diagonal) add(Point(x + 1, y + 1))
    }

operator fun Point.Companion.invoke(x: String, y: String) =
    Point(x.toInt(), y.toInt())

val Collection<Point>.xRange get() = map { it.x }.minToMax()

val Collection<Point>.yRange get() = map { it.y }.minToMax()
