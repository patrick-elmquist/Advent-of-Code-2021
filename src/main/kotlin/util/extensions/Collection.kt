package util.extensions

import util.Point

fun <T : Collection<String>> T.toInts() = map { it.toInt() }

fun <E : CharSequence, T : List<E>> T.splitOnBlank() =
    (indices.filter { get(it).isEmpty() } + listOf(size))
        .fold(mutableListOf<List<E>>() to 0) { (list, start), end ->
            list.add(subList(start, end))
            list to end + 1
        }.first.toList()

val Collection<Point>.xRange get() = map { it.x }.minToMax()

val Collection<Point>.yRange get() = map { it.y }.minToMax()

fun <T> T.repeat(n: Int, block: (T) -> T): T =
    repeat(n, this, block)

fun <T> repeat(n: Int, initial: T, block: (T) -> T): T =
    (0 until n).fold(initial) { acc, _ -> block(acc) }

