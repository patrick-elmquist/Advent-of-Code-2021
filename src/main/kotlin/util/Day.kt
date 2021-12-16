@file:Suppress("EXPERIMENTAL_IS_NOT_ENABLED")

package util

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.runBlocking
import util.Assert.Break
import util.Assert.EqualTo
import util.Out.fail
import util.Out.pass
import kotlin.time.Duration
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
                println("Exception: ${e.message}")
                e.printStackTrace()
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
    val solutionsWithAsserts = solutions.filter { solution -> solution.asserts.isNotEmpty() }
    val result = solutionsWithAsserts.fold(true) { overallResult, solution ->
        val (number, _, asserts) = solution
        println("SOLUTION #${number} ASSERTS")
        println("===================")

        asserts.map { test -> solution.assert(test) }
            .all { result ->
                result.onSuccess {
                    println(it)
                }.onFailure {
                    println(it.message)
                    if (failFast) return@scope Result.failure(AssertFailedException(it.message ?: "null"))
                }
                result.isSuccess
            } && overallResult
    }

    if (result) {
        Result.success(this@runAssertions)
    } else {
        Result.failure(AssertFailedException("One or more assertions failed"))
    }
}

private suspend fun Solution.assert(test: Assert): Result<String> = coroutineScope {
    when (test) {
        is Break -> Result.failure(BreakAssertionException)
        is EqualTo -> {
            val input = Input(test.input.lines())
            when (val result = algorithm(input)) {
                test.expected -> Result.success(pass(input))
                else -> Result.failure(AssertFailedException(fail(input, test.expected, result)))
            }
        }
    }
}

@OptIn(ExperimentalTime::class)
private suspend fun AnswerSheet.evaluate(
    input: Input
): TimedValue<List<Answer>> = coroutineScope {
    measureTimedValue { solutions.map { evaluateSolution(it, input) } }
}

@OptIn(ExperimentalTime::class)
private suspend fun AnswerSheet.evaluateSolution(
    solution: Solution,
    input: Input
): Answer = coroutineScope {
    val (_, algorithm, _, expected) = solution
    val result = measureTimedValue {
        algorithm(input).also {
            if (failFast) {
                check(expected == null || expected == it) { "FAIL Expected:$expected got:$it" }
            } else if (expected != null && expected != it) {
                println("FAIL Expected:$expected got:$it")
            }
        }
    }
    Answer(solution.number, result.value, result.duration)
}

@OptIn(ExperimentalTime::class)
private fun TimedValue<List<Answer>>.printResults() =
    buildString {
        val (results, totalDuration) = this@printResults
        results.forEach { (number, output, time) ->
            appendLine("Answer: #${number}: ${output ?: "Failed"} (${time.inWholeMilliseconds}ms)")
        }
        append("Total duration: ${totalDuration.inWholeMilliseconds}ms")
    }.let { println(it) }

object Out {
    fun pass(input: Input) =
        buildString {
            appendLine("PASS: ${input.lines}")
        }

    fun fail(input: Input, expected: Any, result: Any?): String =
        buildString {
            appendLine("FAIL: ${input.lines}")
            appendLine("  Expected: $expected Actual: $result")
        }
}

typealias Algorithm = suspend CoroutineScope.(Input) -> Any?

data class Solution(
    val number: Int,
    val algorithm: Algorithm,
    val asserts: List<Assert>,
    val expected: Any?
)

private data class Answer(
    val number: Int,
    val output: Any?,
    val time: Duration
)

class AnswerSheet {
    private val _solutions = mutableListOf<Solution>()
    private var asserts = mutableListOf<Assert>()
    private var ignore = false
    private var solutionCalls = 0

    val solutions: List<Solution> = _solutions
    var failFast = false

    fun solution(expected: Any? = null, algorithm: Algorithm) {
        solutionCalls++
        if (!ignore) {
            _solutions.add(Solution(solutionCalls, algorithm, asserts.toList(), expected))
        }
        ignore = false
        asserts.clear()
    }

    @Suppress("unused")
    fun ignore() {
        ignore = true
    }

    @Suppress("unused")
    fun stop() = asserts.add(Break)

    infix fun String.assert(result: Any) = asserts.add(EqualTo(this, result))
}

sealed class Assert {
    object Break : Assert()
    data class EqualTo(val input: String, val expected: Any) : Assert()
}

private sealed class AnswerException(message: String = "") : Exception(message)
private object BreakAssertionException : AnswerException()
private class AssertFailedException(msg: String) : AnswerException(msg)
