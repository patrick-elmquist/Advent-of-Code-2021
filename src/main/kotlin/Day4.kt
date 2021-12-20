import util.Input
import util.day
import util.extensions.splitOnBlank
import util.extensions.toInts

// answer #1: 35670
// answer #2: 22704

private const val SIZE = 5
private const val MARKED = -1

private val WHITE_SPACE_REGEX = "\\s+".toRegex()

fun main() {
    day(n = 4) {
        part1(expected = 35670) { input ->
            val (numbers, boards) = input.parseNumbersAndBoards()

            val (winner, finalNumber) = numbers.firstNotNullOf { number ->
                boards.filter { number in it }
                    .firstOrNull { board ->
                        val index = board.indexOf(number)
                        board.markNumber(index)
                        board.hasBingo(index)
                    }?.let { it to number }
            }

            winner.filter { it != MARKED }.sum() * finalNumber
        }

        part2(expected = 22704) { input ->
            val (numbers, boards) = input.parseNumbersAndBoards()

            val winners = mutableListOf<Pair<List<Int>,Int>>()
            numbers.forEach { number ->
                boards
                    .filter { board -> board !in winners.map { it.first } }
                    .filter { number in it }
                    .forEach { board ->
                        val index = board.indexOf(number)
                        board.markNumber(index)
                        if (board.hasBingo(index)) winners.add(board to number)
                    }
            }

            winners.last().let { (winner, finalNumber) -> winner.filter { it != MARKED }.sum() * finalNumber }
        }
    }
}

private fun Input.parseNumbersAndBoards(): Pair<List<Int>, List<MutableList<Int>>> {
    val numbers = lines.first().split(",").toInts()
    val boards = lines.drop(2).splitOnBlank().map {
        it.flatMap { board -> board.trim().split(WHITE_SPACE_REGEX).toInts() }.toMutableList()
    }
    return numbers to boards
}

private fun MutableList<Int>.markNumber(index: Int) = set(index, MARKED)

private fun List<Int>.hasBingo(index: Int): Boolean {
    val row = index / SIZE
    val col = index % SIZE
    val rows = chunked(SIZE)
    return rows[row].all { it == MARKED } || rows.all { it[col] == MARKED }
}
