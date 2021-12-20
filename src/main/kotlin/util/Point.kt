package util

/**
 * Class representing a point in 2 dimensions
 */
data class Point(val x: Int, val y: Int) {
    constructor(x: String, y: String) : this(x.toInt(), y.toInt())

    operator fun plus(point: Point) = Point(x + point.x, y + point.y)
    operator fun minus(point: Point) = Point(x - point.x, y - point.y)

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

/**
 * Class representing a point in 3 dimensions
 */
data class Point3D(val x: Int, val y: Int, val z: Int) {
    constructor(x: String, y: String, z: String) : this(x.toInt(), y.toInt(), z.toInt())
    constructor(array: IntArray) : this(array[0], array[1], array[2])

    operator fun plus(point: Point3D) = Point3D(x + point.x, y + point.y, z + point.z)
    operator fun minus(point: Point3D) = Point3D(x - point.x, y - point.y, z - point.z)

    companion object
}

