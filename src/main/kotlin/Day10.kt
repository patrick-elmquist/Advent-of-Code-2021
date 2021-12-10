import util.day

// answer #1: 315693
// answer #2: 1870887234

fun main() {
    day(n = 10) {

        solution(expected = 315693) { input ->
            val pointsLookup = mapOf(')' to 3, ']' to 57, '}' to 1197, '>' to 25137)
            input.lines
                .map { line -> line.analyseChunks() }
                .filter { (corruptChunk, _) -> corruptChunk != null }
                .mapNotNull { (corruptChunk, _) -> pointsLookup[corruptChunk] }
                .sum()
        }

        solution(expected = 1870887234L) { input ->
            val pointsLookup = mapOf(')' to 1, ']' to 2, '}' to 3, '>' to 4)
            input.lines
                .map { it.analyseChunks() }
                .filter { (corruptChunk, _) -> corruptChunk == null }
                .map { (_, missingEnds) ->
                    missingEnds.fold(0L) { score, c -> score * 5L + pointsLookup[c]!! }
                }
                .sorted()
                .let { scores -> scores[scores.size / 2] }
        }
    }
}

private fun String.analyseChunks(): Pair<Char?, List<Char>> {
    val list: MutableList<Pair<Chunk, Int>> = mutableListOf()

    val corruptedChunk = firstOrNull { c ->
        val chunk = Chunk(c)
        if (c == chunk.open) {
            list.add(chunk to list.size)
            false
        } else {
            !list.remove(chunk to list.size - 1)
        }
    }

    val missingEnds = list.reversed().map { (chunk, _) -> chunk.close }

    return corruptedChunk to missingEnds
}

private enum class Chunk(val open: Char, val close: Char) {
    Parentheses('(', ')'),
    SquareBrackets('[', ']'),
    CurlyBrackets('{', '}'),
    AngleBrackets('<', '>');
    companion object {
        operator fun invoke(c: Char) = values().first { c == it.open || c == it.close }
    }
}
