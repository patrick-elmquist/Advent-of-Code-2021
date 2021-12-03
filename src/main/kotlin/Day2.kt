import util.day

// answer #1: 1427868
// answer #2: 1568138742

fun main() {
    day(n = 2, inParallel = false) {
        solution { input ->
            input.lines
                .mapCommands()
                .fold(0 to 0) { (depth, distance), (command, value) ->
                    when (command) {
                        "forward" -> depth to (distance + value)
                        "up" -> (depth - value) to distance
                        "down" -> (depth + value) to distance
                        else -> TODO()
                    }
                }.let { (a, b) -> a * b }
        }

        solution { input ->
            input.lines
                .mapCommands()
                .fold(Triple(0, 0, 0)) { (depth, distance, aim), (command, value) ->
                    when (command) {
                        "forward" -> Triple(depth + value * aim, distance + value, aim)
                        "up" -> Triple(depth, distance, aim - value)
                        "down" -> Triple(depth, distance, aim + value)
                        else -> throw IllegalArgumentException()
                    }
                }.let { (a, b, _) -> a * b }
        }
    }
}

private fun List<String>.mapCommands()
    = map { it.split(" ").let { (command, value) -> command to value.toInt() } }
