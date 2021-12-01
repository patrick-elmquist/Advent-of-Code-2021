package util

import kotlinx.coroutines.*
import kotlin.time.TimedValue
import kotlin.time.measureTimedValue

fun day(
    n: Int,
    failFastAssertion: Boolean = false,
    inParallel: Boolean = false,
    block: AnswerSheet.() -> Unit,
) = runBlocking {
    collectSolutions(block = block)
        .runAssertions(failFast = failFastAssertion)
        .evaluate(input = Input(day = n), inParallel = inParallel)
        .printResults()
}

private fun collectSolutions(block: AnswerSheet.() -> Unit): AnswerSheet =
    AnswerSheet().apply(block)

private suspend fun AnswerSheet.runAssertions(
    failFast: Boolean
): AnswerSheet = coroutineScope {
    solutions.withIndex()
        .filter { (_, solution) -> solution.asserts.isNotEmpty() }
        .forEach { (index, solution) ->
            val (algorithm, asserts) = solution
            println("Asserts for Solution #${index + 1}")
            asserts.forEach { (testInput, expected) ->
                val input = Input(testInput.lines())
                val result = algorithm(input)
                val msg = when {
                    result == expected -> assertPassMessage(input)
                    failFast -> throw AssertionError(assertFailMessage(input, expected, result))
                    else -> assertFailMessage(input, expected, result)
                }
                print(msg)
            }
            println()
        }
    this@runAssertions
}

private suspend fun AnswerSheet.evaluate(
    input: Input,
    inParallel: Boolean
): TimedValue<List<TimedValue<Any?>>> = coroutineScope {
    measureTimedValue {
        if (inParallel) {
            solutions.map { (algorithm, _) ->
                async {
                    measureTimedValue { algorithm(input) }
                }
            }.awaitAll()
        } else {
            solutions.map { (algorithm, _) ->
                measureTimedValue { algorithm(input) }
            }
        }
    }
}

private fun TimedValue<List<TimedValue<Any?>>>.printResults() =
    print {
        val (results, totalDuration) = this@printResults
        results.forEachIndexed { index, (answer, time) ->
            appendLine("Answer: #${index + 1}: ${answer ?: "Failed"} (${time.inWholeMilliseconds}ms)")
        }
        appendLine("Total duration: ${totalDuration.inWholeMilliseconds}ms")
    }

private fun assertPassMessage(input: Input) = "PASS: ${input.lines}"

private fun assertFailMessage(input: Input, expected: Any, result: Any?): String =
    buildString {
        appendLine("FAIL: ${input.lines}")
        appendLine("  Expected: $expected Actual: $result")
    }

typealias Algorithm = suspend CoroutineScope.(Input) -> Any?

data class Solution(val algorithm: Algorithm, val asserts: Map<String, Any>)

class AnswerSheet {
    private val _solutions = mutableListOf<Solution>()
    val solutions: List<Solution> = _solutions

    private var asserts = mutableMapOf<String, Any>()

    fun solution(algorithm: Algorithm) {
        _solutions.add(Solution(algorithm, asserts.toMap()))
        asserts.clear()
    }

    @Suppress("unused")
    fun assert(vararg assertWith: Pair<String, Any>) {
        check(assertWith.distinctBy { it.first }.size == assertWith.size) {
            "Using same assert test input more than once!"
        }
        asserts.putAll(assertWith)
    }
}

