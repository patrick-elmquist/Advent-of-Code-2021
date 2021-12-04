import util.Input
import util.day
import util.extensions.splitOnBlank
import util.extensions.toInts
import util.print

// answer #1: 35670
// answer #2: not 35904, too high

private const val SIZE = 5
private const val MARKED = -1

fun main() {
    day(n = 4, failFastAssertion = true) {
        testInput assert 4512
        solution(expected = 35670) { input ->
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

        testInput assert 1924
        solution(expected = 22704) { input ->
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
        it.flatMap { board -> board.trim().split("""\s+""".toRegex()).toInts() }.toMutableList()
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

private val testInput = """
   7,4,9,5,11,17,23,2,0,14,21,24,10,16,13,6,15,25,12,22,18,20,8,19,3,26,1

22 13 17 11  0
 8  2 23  4 24
21  9 14 16  7
 6 10  3 18  5
 1 12 20 15 19

 3 15  0  2 22
 9 18 13 17  5
19  8  7 25 23
20 11 10 24  4
14 21 16 12  6

14 21 17 24  4
10 16 15  9 19
18  8 23 26 20
22 11 13  6  5
 2  0 12  3  7 """.trimStart()