import util.day
import java.util.*
import kotlin.math.abs

// answer #1: 10411
// answer #2: 46721

private const val EMPTY = '.'

fun main() {
    day(n = 23) {
        part1(expected = 10411) { input ->
            solve(initial = State.from(input.lines))
        }

        part2(expected = 46721) { input ->
            val adjustedInput = input.lines.toMutableList().apply {
                add(3, "  #D#C#B#A#  ")
                add(4, "  #D#B#A#C#  ")
            }
            solve(initial = State.from(adjustedInput))
        }
    }
}

private fun solve(initial: State): Int {
    val toVisit = PriorityQueue<StateWithCost>().apply { add(StateWithCost(initial, 0)) }
    val visited = mutableSetOf<StateWithCost>()
    val currentCosts = mutableMapOf<State, Int>().withDefault { Int.MAX_VALUE }

    while (toVisit.isNotEmpty()) {
        val current = toVisit.poll().also { visited.add(it) }
        current.state.nextStates().forEach { next ->
            if (!visited.contains(next)) {
                val newCost = current.cost + next.cost
                if (newCost < currentCosts.getValue(next.state)) {
                    currentCosts[next.state] = newCost
                    toVisit.add(StateWithCost(next.state, newCost))
                }
            }
        }
    }

    return currentCosts.keys.first { it.isFinished }.let { currentCosts.getValue(it) }
}

private data class State(val input: List<List<Char>>) {
    private val hallway = input[0]
    private val rooms = input.drop(1)
    private val destinations = mapOf(
        'A' to Room('A', 2, rooms.map { row -> row[2] }),
        'B' to Room('B', 4, rooms.map { row -> row[4] }),
        'C' to Room('C', 6, rooms.map { row -> row[6] }),
        'D' to Room('D', 8, rooms.map { row -> row[8] })
    )

    private val Char.multiplier
        get() = when (this) {
            'A' -> 1
            'B' -> 10
            'C' -> 100
            'D' -> 1000
            else -> throw IllegalArgumentException()
        }

    private val allowedAndEmptyHallwayIndexes = listOf(0, 1, 3, 5, 7, 9, 10).filter { hallway[it] == EMPTY }

    val isFinished = destinations.values.all { it.hasOnlyValidOccupants }

    fun nextStates(): List<StateWithCost> = buildList {
        occupantsThatCanMove().forEach { (index, occupant) ->
            val room = destinations.getValue(occupant)
            if (hallwayPathIsClear(index, room.index)) {
                val y = room.content.lastIndexOf(EMPTY) + 1
                val cost = (abs(index - room.index) + y) * occupant.multiplier
                add(
                    StateWithCost(
                        State(input.map { it.toMutableList() }
                            .apply {
                                get(0)[index] = EMPTY
                                get(y)[room.index] = occupant
                            }),
                        cost
                    )
                )
            }
        }
        roomsWithWrongOccupant().forEach { room ->
            val (index, occupant) = room.content.withIndex().first { it.value != EMPTY }
            allowedAndEmptyHallwayIndexes.forEach { hallwayIndex ->
                if (hallwayPathIsClear(hallwayIndex, room.index)) {
                    val y = index + 1
                    val cost = (abs(room.index - hallwayIndex) + y) * occupant.multiplier
                    add(StateWithCost(State(
                        input.map { row -> row.toMutableList() }
                            .apply {
                                get(y)[room.index] = EMPTY
                                get(0)[hallwayIndex] = occupant
                            }
                    ), cost))
                }
            }
        }
    }

    private fun occupantsThatCanMove() =
        hallway.withIndex()
            .filter { (_, value) -> value != EMPTY }
            .filter { (_, occupant) -> destinations.getValue(occupant).isEmptyOrHasAllValidOccupants }

    private fun roomsWithWrongOccupant() =
        destinations.values.filter { it.hasOccupantOfWrongType }

    private fun hallwayPathIsClear(start: Int, end: Int) =
        hallway.slice(
            when (start > end) {
                true -> (start - 1) downTo end
                false -> (start + 1)..end
            }
        ).all { it == EMPTY }

    companion object {
        fun from(rawInput: List<String>) =
            State(
                rawInput.drop(1)
                    .dropLast(1)
                    .map {
                        it.drop(1).dropLast(1).toList()
                    }
            )
    }
}

private class StateWithCost(
    val state: State,
    val cost: Int
) : Comparable<StateWithCost> {
    override fun compareTo(other: StateWithCost) = cost.compareTo(other.cost)
}

private class Room(
    type: Char,
    val index: Int,
    val content: List<Char>
) {
    val hasOnlyValidOccupants = content.all { it == type }
    val isEmptyOrHasAllValidOccupants = content.all { it == EMPTY || it == type }
    val hasOccupantOfWrongType = !isEmptyOrHasAllValidOccupants
}