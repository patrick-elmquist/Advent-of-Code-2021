
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
            val (_, base) = localizeScanners(input.parse().toMutableList())
            base.beacons.size
        }

        part2(expected = 13210) { input ->
            val (scanners, _) = localizeScanners(input.parse().toMutableList())
            scanners.maxOf { a -> scanners.maxOf { b -> a.distance(b) } }
        }
    }
}

private fun localizeScanners(
    scanners: MutableList<Scanner>
): Pair<List<Point3D>, Scanner> {
    var base = scanners.removeFirst()
    val scannerPositions = mutableListOf(Point3D(0, 0, 0))
    loop@ while (scanners.isNotEmpty()) {
        for (scanner in scanners) {
            for (s in scanner.rotations) {
                val translation = base.findTranslation(s)
                if (translation != null) {
                    scannerPositions.add(translation)
                    base = base.merge(s, translation)
                    scanners.remove(scanner)
                    continue@loop
                }
            }
        }
    }
    return scannerPositions to base
}

private fun Input.parse() =
    lines.splitOnBlank().map { s ->
        val points = s.drop(1).map { it.split(",") }
        Scanner(points.map { (x, y, z) -> Point3D(x, y, z) }.toMutableList())
    }

private data class Scanner(val beacons: List<Point3D>) {
    val rotations: List<Scanner> by lazy {
        val rotations = MutableList(24) { mutableListOf<Point3D>() }
        beacons.forEach { beacon ->
            val r = beacon.rotations()
            (0 until 24).forEach { i -> rotations[i].add(r[i]) }
        }
        rotations.map { s -> Scanner(s) }
    }

    fun findTranslation(peer: Scanner): Point3D? =
        beacons.flatMap { c -> peer.beacons.map { d -> c - d } }
            .groupingBy { it }
            .eachCount()
            .entries
            .firstOrNull { it.value >= 12 }
            ?.key

    fun merge(scanner: Scanner, translation: Point3D) =
        Scanner(beacons + scanner.beacons
            .map { beacons -> (beacons + translation) }
            .filter { it !in beacons }
        )
}

private fun Point3D.rotations(): List<Point3D> =
    buildList {
        var point = this@rotations
        repeat(2) {
            repeat(3) {
                point = point.roll()
                add(point)
                repeat(3) {
                    point = point.turn()
                    add(point)
                }
            }
            point = point.roll().turn().roll()
        }
    }

private fun Point3D.roll() =
    Point3D(x, z, -y)

private fun Point3D.turn() =
    Point3D(-y, x, z)

fun Point3D.distance(peer: Point3D) =
    abs(peer.x - x) + abs(peer.y - y) + abs(peer.z - z)
