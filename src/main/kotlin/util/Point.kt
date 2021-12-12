package util

data class Point(val x: Int, val y: Int)

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
