import util.Input
import util.day

// answer #1: 3563
// answer #2: 105453

fun main() {
    day(n = 12) {
        part1(expected = 3563) { input ->
            val caveMap = input.parseCaveMap()
            val allowedToVisit = caveMap.keys.filter { name -> name.all { it.isLowerCase() } }.toList()
            caveMap.countPathsToEndFrom(cave = "start", allowedToVisit).size
        }

        part2(expected = 105453) { input ->
            val caveMap = input.parseCaveMap()
            val smallCaves = caveMap.keys.filter { name -> name.all { it.isLowerCase() } }

            smallCaves
                .filter { it != "start" && it != "end" }
                .flatMap { cave ->
                    val allowedToVisit = smallCaves.toList() + cave
                    caveMap.countPathsToEndFrom(cave = "start", allowedToVisit)
                }
                .distinct()
                .count()
        }
    }
}

private fun Input.parseCaveMap(): Map<String, List<String>> =
    lines.flatMap { it.split("-").let { (a, b) -> listOf(a to b, b to a) } }
        .groupBy({ it.first }, { it.second })

private fun Map<String, List<String>>.countPathsToEndFrom(
    cave: String,
    allowedToVisit: List<String>
): List<String> {
    if (cave == "end") return listOf("end")

    val isSmallCave = cave.all { it.isLowerCase() }

    if (isSmallCave && cave !in allowedToVisit) return emptyList()

    val updatedAllowList = if (isSmallCave) allowedToVisit - cave else allowedToVisit

    return requireNotNull(get(cave))
        .flatMap { countPathsToEndFrom(it, updatedAllowList) }
        .distinct()
        .filter { it.isNotEmpty() }
        .map { "$cave-$it" }
}
