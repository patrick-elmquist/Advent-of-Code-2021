package common

import kotlinx.coroutines.*
import kotlin.time.TimedValue
import kotlin.time.measureTimedValue

typealias Solution = suspend CoroutineScope.(Input) -> Any?

class AnswerSheet {
    private val _solutions = mutableListOf<Solution>()
    val solutions: List<Solution> = _solutions
    fun solution(solution: Solution) = _solutions.add(solution)
}

fun day(
    input: Input,
    inParallel: Boolean = false,
    block: AnswerSheet.() -> Unit,
) = runBlocking {
    collectSolutions(block = block)
        .evaluate(input = input, inParallel = inParallel)
        .printResults()
}

fun day(
    n: Int,
    inParallel: Boolean = false,
    block: AnswerSheet.() -> Unit
) = day(Input(day = n), inParallel = inParallel, block = block)

private fun collectSolutions(block: AnswerSheet.() -> Unit) =
    AnswerSheet().apply(block).solutions

private suspend fun Collection<Solution>.evaluate(
    input: Input,
    inParallel: Boolean
): TimedValue<List<TimedValue<Any?>>> = coroutineScope {
    measureTimedValue {
        if (inParallel) {
            this@evaluate.map { solution ->
                async {
                    measureTimedValue { solution(input) }
                }
            }.awaitAll()
        } else {
            this@evaluate.map { solution ->
                measureTimedValue { solution(input) }
            }
        }
    }
}

private fun TimedValue<List<TimedValue<Any?>>>.printResults() {
    val (results, totalDuration) = this
    results.mapIndexed { index, (answer, time) ->
        println("Answer: #${index + 1}: ${answer ?: "Failed"} (${time.inWholeMilliseconds}ms)")
    }
    println("Total duration: ${totalDuration.inWholeMilliseconds}ms")
}
