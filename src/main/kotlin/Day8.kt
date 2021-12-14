import util.Input
import util.day

// answer #1: 409
// answer #2: 1024649

fun main() {
    day(n = 8) {
        solution(expected = 409) { input ->
            input.parsePatternsAndOutputs()
                .sumOf { (_, output) ->
                    output.count { number -> number.size in listOf(2, 3, 4, 7) }
                }
        }

        solution(expected = 1024649) { input ->
            input.parsePatternsAndOutputs()
                .sumOf { (patterns, output) ->
                    val numbers = MutableList(10) { emptySet<Char>() }

                    with(patterns.toMutableList()) {
                        numbers[1] = pick { size == 2 }
                        numbers[4] = pick { size == 4 }
                        numbers[7] = pick { size == 3 }
                        numbers[8] = pick { size == 7 }
                        numbers[3] = pick { size == 5 && (it - numbers[1]).size == 3 }
                        numbers[9] = pick { size == 6 && (it - numbers[3]).size == 1 }
                        numbers[5] = pick { size == 5 && (it - numbers[9]).isEmpty() }
                        numbers[6] = pick { size == 6 && (it - numbers[5]).size == 1 }
                        numbers[2] = pick { size == 5 && (it - numbers[5]).size == 2 }
                        numbers[0] = pick { size == 6 && (it - numbers[8]).isEmpty() }
                    }

                    val valueToDigitMap = numbers.withIndex().associateBy({ it.value }, { it.index })
                    output.map { value -> valueToDigitMap[value] }.joinToString("").toInt()
                }
        }
    }
}

private fun Input.parsePatternsAndOutputs() =
    lines.map { line ->
        line.split(" | ").let { (patterns, output) ->
            patterns.split(" ").map { it.toSet() } to output.split(" ").map { it.toSet() }
        }
    }

private fun <T> MutableList<T>.pick(condition: T.(T) -> Boolean): T =
    removeAt(indexOfFirst { it.condition(it) })
