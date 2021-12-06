package util.extensions

fun main() {
    // isBitSet tests
    check(0b101.isBitSet(0)) { "Index 0 didn't work" }
    check(!0b101.isBitSet(1)) { "Index 1 didn't work" }
    check(0b101.isBitSet(2)) { "Index 1 didn't work" }

    println("Test OK")
}

fun Int.isBitSet(index: Int): Boolean = (this shr index) and 1 != 0

fun Int.getBit(index: Int): Int = if (isBitSet(index)) 1 else 0

/**
 * Creates a range of the two points, automatically handling the direction
 */
infix fun Int.safeRange(other: Int) = if (this < other) this..other else this downTo other
