
import util.day
import util.log

// answer #1: 10411
// answer #2:

fun main() {
    day(n = 23) {
        part1 { input ->
            // solved by hand
        }

        part2 { input ->

            val rooms = listOf(
                Room(
                    "A",
                    listOf(
                        create("B"),
                        create("D"),
                        create("D"),
                        create("C")
                    )
                ),
                Room(
                    "B",
                    listOf(
                        create("B"),
                        create("C"),
                        create("B"),
                        create("C")
                    )
                ),
                Room(
                    "C",
                    listOf(
                        create("D"),
                        create("B"),
                        create("A"),
                        create("A")
                    )
                ),
                Room(
                    "D",
                    listOf(
                        create("D"),
                        create("A"),
                        create("C"),
                        create("A")
                    )
                ),
            )

            rooms.log()

            Unit
        }
    }
}

private typealias Rooms = List<Room>

private data class Hallway(val amphipods: List<Amphipods>)

private fun rec(rooms: Rooms, hallway: Hallway): Int {
    val hallwayEmpty = hallway.amphipods.isEmpty()
    val allMatched = rooms.all { room -> room.amphipods.all { it.type == room.name } }

    if (hallwayEmpty && allMatched) return 0


    TODO()
}
private val counter = mutableMapOf(
    "A" to 0,
    "B" to 0,
    "C" to 0,
    "D" to 0,
)
private fun create(type: String): Amphipods {
    val n = counter.merge(type, 1, Integer::sum) ?: error("wtf...")
    return Amphipods(type, n)
}
private data class Room(
    val name: String,
    val amphipods: List<Amphipods> = emptyList()
)
private data class Amphipods(
    val type: String,
    val n: Int
)

private val input = """
#############
#...........#
###B#B#D#D###
  #D#C#B#A#
  #D#B#A#C#
  #C#C#A#A#
  #########
""".trimIndent()

private val cost = mapOf(
    'A' to 1,
    'B' to 10,
    'C' to 100,
    'D' to 1000
)