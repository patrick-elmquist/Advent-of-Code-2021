import util.Input
import util.day
import util.extensions.repeat
import util.extensions.splitOnBlank

// answer #1: 2587
// answer #2: 3318837563123

fun main() {
    day(n = 14) {
        part1(expected = 2587L) { input ->
            val (template, rules) = input.parseTemplateAndRules()
            val polymer = runInsertionSteps(n = 10, template, rules)
            polymer.values.maxOf { it } - polymer.values.minOf { it }
        }

        part2(expected = 3318837563123L) { input ->
            val (template, rules) = input.parseTemplateAndRules()
            val polymer = runInsertionSteps(n = 40, template, rules)
            polymer.values.maxOf { it } - polymer.values.minOf { it }
        }
    }
}

private fun Input.parseTemplateAndRules() =
    lines.splitOnBlank().let { (template, rules) ->
        template.first() to rules.associate { rule ->
            // TODO replace with regex
            rule.split(" -> ").let { it.first() to it.last().first() }
        }
    }

private fun runInsertionSteps(n: Int, template: String, rules: Map<String, Char>): Map<Char, Long> {
    // Add the counts from the initial template
    val initialCount = template.zipWithNext()
        .map { "${it.first}${it.second}" }
        .groupingBy { it }
        .eachCount()
        .mapValues { it.value.toLong() }

    // Apply the insertion steps N times
    val count = initialCount.repeat(n) { state ->
        state.keys
            .flatMap { key -> createPairs(key, rules, state) }
            .groupBy({ it.first }, { it.second })
            .mapValues { it.value.sum() }
    }

    return buildMap {
        count.forEach { (key, value) -> merge(key.first(), value, ::sum) }
        // Only the first char is counted in the forEach, add the last one manually
        merge(template.last(), 1L, ::sum)
    }
}

private fun createPairs(
    key: String,
    rules: Map<String, Char>,
    state: Map<String, Long>
): List<Pair<String, Long>> =
    buildList {
        val start = key.first()
        val end = key.last()
        val toInsert = rules.getValue(key)
        val count = state.getValue(key)
        add("$start$toInsert" to count)
        add("$toInsert$end" to count)
    }

private fun sum(a: Long, b: Long) = a + b
