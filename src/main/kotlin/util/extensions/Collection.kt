package util.extensions

fun <T : Collection<String>> T.toInts() = map { it.toInt() }
