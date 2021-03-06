package util

import util.extensions.toInts
import java.io.File

data class Input(val lines: List<String>) {
    val ints by lazy { lines.toInts() }
    val single by lazy { lines.single() }

    constructor(day: Int) : this(File("./assets/input-day-$day.txt"))
    constructor(file: File) : this(file.readLines())
}
