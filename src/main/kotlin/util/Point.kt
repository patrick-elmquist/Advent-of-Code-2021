package util

data class Point(val x: Int, val y: Int)

fun Point.neighbors(diagonal: Boolean = false): List<Point> =
    buildList {
        this += copy(x = x - 1)
        this += copy(x = x + 1)
        this += copy(y = y - 1)
        this += copy(y = y + 1)

        if (diagonal) {
            this += Point(x - 1, y - 1)
            this += Point(x - 1, y + 1)
            this += Point(x + 1, y - 1)
            this += Point(x + 1, y + 1)
        }
    }
