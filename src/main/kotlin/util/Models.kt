package util

import kotlin.time.Duration

data class Part(
    val number: Int,
    val algorithm: (Input) -> Any?,
    val expected: Any?,
    val tests: List<Test>
)

data class Test(
    val input: String,
    val expected: Any?
)

data class Answer(
    val number: Int,
    val output: Any?,
    val time: Duration
)

class Sheet {
    private val tests = mutableListOf<Test>()
    val parts: MutableList<Part> = mutableListOf()

    var expected: Any? = null
    var breakAdded: Boolean = false
    var ignore: Boolean = false

    infix fun String.expect(expected: Any?) {
        if (!breakAdded) {
            tests += Test(this, expected)
        }
    }

    fun part1(expected: Any? = null, block: (Input) -> Any?) {
        check(parts.isEmpty())
        if (!ignore) {
            parts += Part(1, block, expected, tests.toList())
        }
        ignore = breakAdded
        tests.clear()
    }

    fun part2(expected: Any? = null, block: (Input) -> Any?) {
        check(parts.single().number == 1)
        if (!ignore) {
            parts += Part(2, block, expected, tests.toList())
        }
        ignore = breakAdded
        tests.clear()
    }

    @Suppress("unused")
    fun stop() {
        breakAdded = true
    }

    @Suppress("unused")
    fun ignore() {
        ignore = true
    }
}
