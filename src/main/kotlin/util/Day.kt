package util

import kotlinx.coroutines.*
import util.AssertStep.*
import kotlin.time.ExperimentalTime
import kotlin.time.TimedValue
import kotlin.time.measureTimedValue

fun day(
    n: Int,
    block: AnswerSheet.() -> Unit,
) = runBlocking {
    collectSolutions(block = block)
        .runAssertions()
        .onSuccess { validatedSolutions ->
            try {
                validatedSolutions.evaluate(input = Input(day = n)).printResults()
            } catch (e: Exception) {
                println(e.message)
            }
        }
        .onFailure { reason ->
            when (reason) {
                is BreakAssertionException -> println("BREAK")
                is AssertFailedException -> println(reason.message)
            }
        }
}

private fun collectSolutions(block: AnswerSheet.() -> Unit): AnswerSheet =
    AnswerSheet().apply(block)

private suspend fun AnswerSheet.runAssertions(): Result<AnswerSheet> = coroutineScope scope@{
    val result = solutions.withIndex()
        .filter { (_, solution) -> solution.asserts.isNotEmpty() }
        .fold(true) { overallResult, (index, solution) ->
            println()
            println("SOLUTION #${index + 1} ASSERTS")
            println("===================")

            val (algorithm, asserts) = solution
            asserts.fold(true) { solutionResult, step ->
                when (step) {
                    is Break -> return@scope Result.failure(BreakAssertionException)
                    is Assert -> {
                        val (testInput, expected) = step
                        val input = Input(testInput.lines())
                        val result = algorithm(input)
                        if (result == expected) {
                            print(passMessage(input))
                        } else if (failFastAssertion) {
                            return@scope Result.failure(AssertFailedException(failMessage(input, expected, result)))
                        } else {
                            print(failMessage(input, expected, result))
                        }
                        result == expected
                    }
                } && solutionResult
            } && overallResult
        }
    println()
    if (result) {
        Result.success(this@runAssertions)
    } else {
        Result.failure(AssertFailedException("One or more assertions failed"))
    }
}

@OptIn(ExperimentalTime::class)
private suspend fun AnswerSheet.evaluate(
    input: Input
): TimedValue<List<TimedValue<Any?>>> = coroutineScope {
    measureTimedValue {
        if (inParallel) {
            solutions.map { (algorithm, _, expected) ->
                async {
                    measureTimedValue {
                        algorithm(input).also {
                            check(expected == null || expected == it) { "Expected:$expected got:$it" }
                        }
                    }
                }
            }.awaitAll()
        } else {
            solutions.map { (algorithm, _, expected) ->
                measureTimedValue {
                    algorithm(input).also {
                        check(expected == null || expected == it) { "Expected:$expected got:$it" }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalTime::class)
private fun TimedValue<List<TimedValue<Any?>>>.printResults() =
    buildString {
        val (results, totalDuration) = this@printResults
        results.forEachIndexed { index, (answer, time) ->
            appendLine("Answer: #${index + 1}: ${answer ?: "Failed"} (${time.inWholeMilliseconds}ms)")
        }
        appendLine("Total duration: ${totalDuration.inWholeMilliseconds}ms")
    }.let { println(it) }

private fun passMessage(input: Input) = buildString { appendLine("PASS: ${input.lines}") }

private fun failMessage(input: Input, expected: Any, result: Any?): String =
    buildString {
        appendLine("FAIL: ${input.lines}")
        appendLine("  Expected: $expected Actual: $result")
    }

typealias Algorithm = suspend CoroutineScope.(Input) -> Any?

data class Solution(
    val algorithm: Algorithm,
    val asserts: List<AssertStep>,
    val expected: Any?
)

class AnswerSheet {
    private val _solutions = mutableListOf<Solution>()

    val solutions: List<Solution> = _solutions

    var failFastAssertion = false

    var inParallel = false

    private var asserts = mutableListOf<AssertStep>()

    fun solution(expected: Any? = null, algorithm: Algorithm) {
        _solutions.add(Solution(algorithm, asserts.toList(), expected))
        asserts.clear()
    }

    @Suppress("unused")
    fun stop() = asserts.add(Break)

    infix fun String.assert(result: Any) = asserts.add(Assert(this, result))
}

sealed class AssertStep {
    object Break : AssertStep()
    data class Assert(val input: String, val expected: Any) : AssertStep()
}

private sealed class AnswerException(message: String = "") : Exception(message)
private object BreakAssertionException : AnswerException()
private class AssertFailedException(msg: String) : AnswerException(msg)
