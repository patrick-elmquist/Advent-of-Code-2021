import util.day

// answer #1: 1228
// answer #2: 1257

fun main() {
    day(n = 1) {
        part1(expected = 1228) { input ->
            input.ints.zipWithNext().count { (a, b) -> b > a }
        }

        part2(expected = 1257) { input ->
            input.ints.windowed(size = 4).zipWithNext().count { (a, b) -> b.sum() > a.sum() }
        }
    }
}
