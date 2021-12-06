package util.extensions

import java.util.*

fun <T : Collection<String>> T.toInts() = map { it.toInt() }

fun <E : CharSequence, T : List<E>> T.splitOnBlank() =
    (indices.filter { get(it).isEmpty() } + listOf(size))
        .fold(mutableListOf<List<E>>() to 0) { (list, start), end ->
            list.add(subList(start, end))
            list to end + 1
        }.first.toList()

fun <T> linkedList(size: Int, initial: (Int) -> T): LinkedList<T> =
    LinkedList<T>().apply { (0 until size).forEach { index -> add(initial(index)) } }
