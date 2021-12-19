
import util.Input
import util.Point3D
import util.day
import util.extensions.splitOnBlank
import kotlin.math.abs

// answer #1: 419
// answer #2: 13210

fun main() {
    day(n = 19) {
        part1(expected = 419) { input ->
            val pool = input.parsePool()
            val base = pool.removeFirst()
            val scanners = mutableListOf(Point3D(0, 0, 0))
            while (pool.isNotEmpty()) {
                outer@ for (sc in pool) {
                    for (t in sc.getAllRotations()) {
                        val trans = base.findTranslation(t)
                        if (trans != null) {
                            scanners.add(trans)
                            base.add(t, trans)
                            pool.remove(sc)
                            break@outer
                        }
                    }
                }
            }
            base.beacons.size
        }

        part2(expected = 13210) { input ->
            val pool = input.parsePool()
            val base = pool.removeFirst()
            val scanners = mutableListOf(Point3D(0, 0, 0))
            while (pool.isNotEmpty()) {
                outer@ for (sc in pool) {
                    for (t in sc.getAllRotations()) {
                        val trans = base.findTranslation(t)
                        if (trans != null) {
                            scanners.add(trans)
                            base.add(t, trans)
                            pool.remove(sc)
                            break@outer
                        }
                    }
                }
            }

            scanners.indices.maxOf { i ->
                scanners.indices.maxOf { j ->
                    scanners[i].distance(scanners[j])
                }
            }
        }
    }
}

private fun Input.parsePool() =
    lines.splitOnBlank().map { s ->
        val points = s.drop(1).map { it.split(",") }
        Scanner(points.map { (x, y, z) -> Point3D(x, y, z) }.toMutableList())
    }.toMutableList()

private data class Scanner(val beacons: MutableList<Point3D>) {
    fun findTranslation(peer: Scanner): Point3D? {
        val map = mutableMapOf<Point3D, Int>()
        beacons.forEach { c ->
            peer.beacons.forEach { d ->
                // TODO remember this one, need to use it a lot else where
                map.merge(c - d, 1, Integer::sum)
            }
        }
        return map.entries.firstOrNull { it.value >= 12 }?.key
    }

    fun getAllRotations(): List<Scanner> {
        val ret = MutableList<MutableList<Point3D>>(24) { mutableListOf() }
        beacons.forEach { c ->
            val x = c.sequence()
            (0 until 24).forEach { i -> ret[i].add(x[i]) }
        }
        return ret.map { s -> Scanner(s) }
    }

    fun add(t: Scanner, trans: Point3D) {
        t.beacons.forEach { c ->
            val v = c + trans
            if (!beacons.contains(v)) beacons.add(v)
        }
    }
}
private fun Point3D.sequence(): List<Point3D> {
    var v = this
    val r = mutableListOf<Point3D>()
    repeat(2) {
        repeat(3) {
            v = v.roll()
            r.add(v)
            repeat(3) {
                v = v.turn()
                r.add(v)
            }
        }
        v = v.roll().turn().roll()
    }
    return r.toList()
}

private fun Point3D.roll() = Point3D(x, z, -y)
private fun Point3D.turn() = Point3D(-y, x, z)

fun Point3D.distance(peer: Point3D) =
    abs(peer.x - x) + abs(peer.y - y) + abs(peer.z - z)

private operator fun Point3D.Companion.invoke(array: IntArray) =
    Point3D(array[0], array[1], array[2])

private operator fun Point3D.Companion.invoke(x: String, y: String, z: String) =
    Point3D(x.toInt(), y.toInt(), z.toInt())
