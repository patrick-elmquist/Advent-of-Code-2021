@file:Suppress("EXPERIMENTAL_IS_NOT_ENABLED")

package util

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.runBlocking
import util.AssertStep.Break
import util.AssertStep.EqualTo
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

private inline fun collectSolutions(block: AnswerSheet.() -> Unit): AnswerSheet =
    AnswerSheet().apply(block)

private suspend fun AnswerSheet.runAssertions(): Result<AnswerSheet> = coroutineScope scope@{
    val result = solutions.withIndex()
        .filter { (_, solution) -> solution.asserts.isNotEmpty() }
        .fold(true) { overallResult, (index, solution) ->
            println("SOLUTION #${index + 1} ASSERTS")
            println("===================")

            val (algorithm, asserts) = solution
            val solutionAsserts = asserts.fold(true) { solutionResult, step ->
                when (step) {
                    is Break -> return@scope Result.failure(BreakAssertionException)
                    is EqualTo -> {
                        val input = Input(step.input.lines())
                        val result = algorithm(input)
                        when {
                            result == step.expected -> print(pass(input))
                            failFast ->
                                return@scope Result.failure(AssertFailedException(fail(input, step.expected, result)))
                            else -> print(fail(input, step.expected, result))
                        }
                        result == step.expected
                    }
                } && solutionResult
            }
            println()
            solutionAsserts && overallResult
        }

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
        solutions.map { (algorithm, _, expected) ->
            measureTimedValue {
                algorithm(input).also {
                    if (failFast) {
                        check(expected == null || expected == it) { "FAIL Expected:$expected got:$it" }
                    } else if (expected != null && expected != it) {
                        println("FAIL Expected:$expected got:$it")
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
        append("Total duration: ${totalDuration.inWholeMilliseconds}ms")
    }.let { println(it) }

private fun pass(input: Input) =
    buildString {
        appendLine("PASS: ${input.lines}")
    }

private fun fail(input: Input, expected: Any, result: Any?): String =
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

    var failFast = false

    var ignore = false

    private var asserts = mutableListOf<AssertStep>()

    fun solution(expected: Any? = null, algorithm: Algorithm) {
        if (!ignore) {
            _solutions.add(Solution(algorithm, asserts.toList(), expected))
        }
        ignore = false
        asserts.clear()
    }

    @Suppress("unused")
    fun stop() = asserts.add(Break)

    infix fun String.assert(result: Any) = asserts.add(EqualTo(this, result))
}

sealed class AssertStep {
    object Break : AssertStep()
    data class EqualTo(val input: String, val expected: Any) : AssertStep()
}

private sealed class AnswerException(message: String = "") : Exception(message)
private object BreakAssertionException : AnswerException()
private class AssertFailedException(msg: String) : AnswerException(msg)
