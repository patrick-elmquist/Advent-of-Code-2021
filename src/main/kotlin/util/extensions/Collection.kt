package util.extensions

fun <T : Collection<String>> T.toInts() = map { it.toInt() }

fun <E : CharSequence, T : List<E>> T.splitOnBlank() =
    (indices.filter { get(it).isEmpty() } + listOf(size))
        .fold(mutableListOf<List<E>>() to 0) { (list, start), end ->
            list.add(subList(start, end))
            list to end + 1
        }.first.toList()
