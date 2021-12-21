
import util.Input
import util.day
import kotlin.math.max

// answer #1: 864900
// answer #2: 575111835924670

fun main() {
    day(n = 21) {
        part1(expected = 864900L) { input ->
            val (p1, p2) = input.parsePlayers()

            var die = 0
            fun roll(): Int = (if (die + 1 > 100) 1 else die + 1).also { die = it }

            var game = Game(p1, p2, 0, 1000)
            while (!game.hasFinished) {
                game = game.movePlayer(roll() + roll() + roll())
            }

            // total rolls * loser score
            3 * game.turn * game.loser.score
        }

        part2(expected = 575111835924670L) { input ->
            val (p1, p2) = input.parsePlayers()

            var games = mapOf(Game(p1, p2, turn = 0, goal = 21) to 1L)
            while (games.keys.any { !it.hasFinished }) {
                val newGames = mutableMapOf<Game, Long>()
                for ((game, value) in games) {
                    if (game.hasFinished) {
                        newGames[game] = value
                    } else {
                        outcomes.forEach { (dice, count) ->
                            newGames.merge(game.movePlayer(dice), value * count, ::sum)
                        }
                    }
                }
                games = newGames
            }

            max(
                games.entries.filter { (key, _) -> key.winner == key.p1 }.sumOf { it.value },
                games.entries.filter { (key, _) -> key.winner == key.p2 }.sumOf { it.value }
            )
        }
    }
}

private val outcomes = mapOf(
    3 to 1,
    4 to 3,
    5 to 6,
    6 to 7,
    7 to 6,
    8 to 3,
    9 to 1
)

private fun Input.parsePlayers() =
    lines.mapIndexed { i, it -> Player(i + 1, it.split(" ").last().toInt()) }

private fun sum(a: Long, b: Long) = a + b

private data class Game(val p1: Player, val p2: Player, val turn: Int, val goal: Int) {
    val winner get() = if (p1.score >= goal) p1 else p2
    val loser get() = if (p1.score >= goal) p2 else p1
    val hasFinished get() = p1.score >= goal || p2.score >= goal

    fun movePlayer(steps: Int) =
        if (turn % 2 == 0) {
            copy(p1 = p1.move(steps), turn = turn + 1)
        } else {
            copy(p2 = p2.move(steps), turn = turn + 1)
        }
}

private data class Player(
    val n: Int,
    val position: Int,
    val score: Long = 0
) {
    fun move(steps: Int): Player {
        val updated = position + steps
        val wrapped = if (updated % 10 == 0) 10 else updated % 10
        return copy(position = wrapped, score = score + wrapped)
    }
}
