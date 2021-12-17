import util.day

// answer #1: 315693
// answer #2: 1870887234

fun main() {
    day(n = 10) {
        part1(expected = 315693) { input ->
            val pointsLookup = mapOf(')' to 3, ']' to 57, '}' to 1197, '>' to 25137)
            input.lines
                .map { line -> line.analyzeChunks() }
                .filter { (corruptChunk, _) -> corruptChunk != null }
                .mapNotNull { (corruptChunk, _) -> pointsLookup[corruptChunk] }
                .sum()
        }

        part2(expected = 1870887234L) { input ->
            val pointsLookup = mapOf(')' to 1, ']' to 2, '}' to 3, '>' to 4)
            input.lines
                .map { it.analyzeChunks() }
                .filter { (corruptChunk, _) -> corruptChunk == null }
                .map { (_, missingEnds) ->
                    missingEnds.fold(0L) { score, c -> score * 5L + pointsLookup[c]!! }
                }
                .sorted()
                .let { scores -> scores[scores.size / 2] }
        }
    }
}

private fun String.analyzeChunks(): Pair<Char?, List<Char>> {
    val openChunks = mutableListOf<Pair<Chunk, Int>>()

    val corruptedChunk = firstOrNull { c ->
        val chunk = Chunk(c)
        if (c == chunk.open) {
            openChunks.add(chunk to openChunks.size)
        } else {
            openChunks.remove(chunk to openChunks.size - 1)
        }.not()
    }

    val missingEnds = openChunks.reversed().map { (chunk, _) -> chunk.close }

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
