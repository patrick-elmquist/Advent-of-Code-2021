package common

fun <T : Collection<String>> T.toInts() = map { it.toInt() }
