package util.extensions

fun main() {
    // isBitSet tests
    0b101.isBitSet(0) assert true
    0b101.isBitSet(1) assert false
    0b101.isBitSet(2) assert true

    (1 safeRange 5) assert (1 .. 5)
    (5 safeRange 1) assert (5 downTo 1)

    println("Test OK")
}

fun Int.isBitSet(index: Int): Boolean = (this shr index) and 1 != 0

fun Int.getBit(index: Int): Int = if (isBitSet(index)) 1 else 0

/**
 * Creates a range of the two points, automatically handling the direction
 */
infix fun Int.safeRange(other: Int) = if (this < other) this..other else this downTo other

private infix fun <T> T.assert(expected: T) = assert(this == expected) { "Assert failed: $this != $expected" }
