
import util.Input
import util.day
import util.extensions.match
import kotlin.math.max
import kotlin.math.min

// answer #1: 589411
// answer #2: 1130514303649907

fun main() {
    day(n = 22) {
        part1(expected = 589411L) { input ->
            val smallRange = -50..50
            input.parseInstructions()
                .filter { (c, _) ->
                    c.x.first in smallRange && c.x.last in smallRange &&
                            c.y.first in smallRange && c.y.last in smallRange &&
                            c.z.first in smallRange && c.z.last in smallRange
                }
                .reboot()
        }

        part2(expected = 1130514303649907L) { input ->
            input.parseInstructions().reboot()
        }
    }
}

private fun Input.parseInstructions() = lines.map { Cuboid.withState(it) }

private fun List<Pair<Cuboid, String>>.reboot(): Long =
    fold(emptyList<Cuboid>()) { cuboids, (cuboid, state) ->
        buildList {
            addAll(cuboids)
            // Check for intersections with previously added cuboids
            // and add new cuboids representing any intersection
            addAll(
                cuboids.filter { cuboid.intersect(it) }
                    .map { cuboid.intersection(it) }
            )
            // The off case is already handled by the intersection cuboids
            if (state == "on") add(cuboid)
        }
    }
        .sumOf { it.volume * it.sign }

private data class Cuboid(
    val x: LongRange,
    val y: LongRange,
    val z: LongRange,
    val sign: Int
) {
    val volume: Long = (x.last - x.first + 1) *
            (y.last - y.first + 1) *
            (z.last - z.first + 1)

    fun intersect(other: Cuboid): Boolean =
        x.first <= other.x.last && x.last >= other.x.first &&
                y.first <= other.y.last && y.last >= other.y.first &&
                z.first <= other.z.last && z.last >= other.z.first

    fun intersection(other: Cuboid): Cuboid {
        val xRange = max(x.first, other.x.first)..min(x.last, other.x.last)
        val yRange = max(y.first, other.y.first)..min(y.last, other.y.last)
        val zRange = max(z.first, other.z.first)..min(z.last, other.z.last)

        val sign = when  {
            sign == other.sign -> -sign
            sign == 1 && other.sign == -1 -> 1
            else -> sign * other.sign
        }

        return Cuboid(xRange, yRange, zRange, sign)
    }

    companion object {
        fun withState(line: String): Pair<Cuboid, String> =
            regex.match(line) { (state, x1, x2, y1, y2, z1, z2) ->
                Cuboid(
                    x1.toLong()..x2.toLong(),
                    y1.toLong()..y2.toLong(),
                    z1.toLong()..z2.toLong(),
                    if (state == "off") -1 else 1
                ) to state
            }
    }
}

private val regex =  """(\w+) x=(-?\d+)..(-?\d+),y=(-?\d+)..(-?\d+),z=(-?\d+)..(-?\d+)""".toRegex()