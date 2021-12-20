
import util.Input
import util.Point
import util.day
import util.extensions.splitOnBlank
import util.neighbors
import util.xRange
import util.yRange

// answer #1: 5259
// answer #2: 15287

fun main() {
    day(n = 20) {
        part1(expected = 5259) { input ->
            val (lookup, image) = input.parseLookupAndImage()
            image.enhance(n = 2, lookup).count { it.value == 1 }
        }

        part2(expected = 15287) { input ->
            val (lookup, image) = input.parseLookupAndImage()
            image.enhance(n = 50, lookup).count { it.value == 1 }
        }
    }
}

private fun Input.parseLookupAndImage() =
    lines.splitOnBlank().let { (inLookup, inImage) ->
        // Translates # and . to 1 and 0
        fun Char.translate() = if (this == '#') 1 else 0

        val lookup = inLookup.first().map { c -> c.translate() }.toIntArray()
        val image = inImage.flatMapIndexed { y, row ->
            row.mapIndexedNotNull { x, c -> Point(x, y) to c.translate() }
        }.toMap()

        lookup to image
    }

private fun Map<Point, Int>.enhance(n: Int, lookup: IntArray) =
    repeatWithValue(times = n, initial = this to 0) { (image, void) ->
        val pixels = image.keys

        // Grow the canvas 1 step in each direction
        val widthIndices = pixels.xRange.let { it.first - 1..it.last + 1 }
        val heightIndices = pixels.yRange.let { it.first - 1..it.last + 1 }

        val enhancedImage = heightIndices.flatMap { y -> widthIndices.map { x -> Point(x, y) } }
            .associateWith { pixel ->
                val lookupIndex = pixel.neighbors(diagonal = true, includeSelf = true)
                    .map { image.getOrDefault(it, void) }
                    .toBinary()
                lookup[lookupIndex]
            }
            // Only keep pixels that are part of the actual image
            .filterKeys { point -> point.x in widthIndices && point.y in heightIndices }

        enhancedImage to calculateNextVoid(lookup, void)
    }.first

// Calculate a maxed out block to determine what it will end up as the next iteration
private fun calculateNextVoid(lookup: IntArray, void: Int): Int =
    (0 until 9).map { void }.toBinary().let { lookup[it] }

private fun <T> repeatWithValue(times: Int, initial: T, block: (T) -> T): T =
    (0 until times).fold(initial) { acc, _ -> block(acc) }

private fun List<Int>.toBinary() = joinToString("").toInt(2)
