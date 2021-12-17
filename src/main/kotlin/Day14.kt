
import util.day
import util.extensions.splitOnBlank

// answer #1: 2587
// answer #2: 3318837563123

fun main() {
    day(n = 14) {
        testInput expect 1588
        part1(expected = 2587) { input ->
            val (template, rules) = input.lines.splitOnBlank().let { (t, r) ->
                t.first() to r.associate {
                    it.split(" -> ").let { it.first() to it.last().first() }
                }
            }

            var t = template
            repeat(10) {
                t = t.toList().windowed(2, partialWindows = true) { window ->
                    if (window.size == 1) {
                        listOf(window.first())
                    } else {
                        val toBeInserted = rules.getValue(window.joinToString(""))
                        listOf(window.first(), toBeInserted)
                    }
                }.flatten().joinToString("")
            }

            val result = t.groupingBy { it }.eachCount()

            result.values.maxOf { it } - result.values.minOf { it }
        }

        testInput expect 2188189693529L
        part2(expected = 3318837563123L) { input ->
            val (template, rules) = input.lines.splitOnBlank().let { (t, r) ->
                t.first() to r.associate {
                    it.split(" -> ").let { it.first() to it.last().first() }
                }
            }

            var map = mutableMapOf<String, Long>()
            template.zipWithNext().map { "${it.first}${it.second}" }.forEach {
                map[it] = map.getOrDefault(it, 0) + 1L
            }
            repeat(40) {
                val keys = map.keys
                val newMap = mutableMapOf<String, Long>()
                keys.forEach { key ->
                    val next = "${key.first()}${rules.getValue(key)}"
                    val next2 = "${rules.getValue(key)}${key.last()}"
                    newMap[next] = newMap.getOrDefault(next, 0L) + map.getValue(key)
                    newMap[next2] = newMap.getOrDefault(next2, 0L) + map.getValue(key)
                }
                map = newMap
            }
            val countMap = mutableMapOf<Char, Long>()
            map.forEach { (key, value) ->
                key.take(1).forEach { c ->
                    countMap[c] = countMap.getOrDefault(c, 0L) + value
                }
            }
            countMap[template.last()] = countMap.getOrDefault(template.last(), 0L) + 1L

            countMap.values.maxOf { it } - countMap.values.minOf { it }
        }
    }
}

private val testInput = """
NNCB

CH -> B
HH -> N
CB -> H
NH -> C
HB -> C
HC -> B
HN -> C
NN -> C
BH -> H
NC -> B
NB -> B
BN -> B
BB -> N
BC -> B
CC -> N
CN -> C
""".trimIndent()
