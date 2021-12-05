package util.extensions

fun main() {
    // isBitSet tests
    check(0b101.isBitSet(0)) { "Index 0 didn't work" }
    check(!0b101.isBitSet(1)) { "Index 1 didn't work" }
    check(0b101.isBitSet(2)) { "Index 1 didn't work" }

    println("Test OK")
}

fun Int.isBitSet(index: Int): Boolean = (this shr index) and 1 != 0

