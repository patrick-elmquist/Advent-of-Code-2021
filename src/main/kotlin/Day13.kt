import Fold.*
import util.*
import util.extensions.match
import util.extensions.splitOnBlank

// answer #1: 618
// answer #2: ALREKFKU

fun main() {
    day(n = 13) {
        solution(expected = 618) { input ->
            val (sheet, folds) = input.parseSheetAndFolds()
            sheet.fold(folds.first()).count()
        }

        solution { input ->
            val (defaultSheet, folds) = input.parseSheetAndFolds()
            folds.fold(defaultSheet) { sheet, direction -> sheet.fold(direction) }.print()
            "Check print above!"
        }
    }
}

private fun Input.parseSheetAndFolds(): Pair<Set<Point>, List<Fold>> {
    val (dotsInput, foldInput) = lines.splitOnBlank()

    val dotRegex = """(\d+),(\d+)""".toRegex()
    val dots = dotsInput.map { dotRegex.match(it) { (x, y) -> Point(x, y) } }

    val foldRegex = """fold along ([xy])=(\d+)""".toRegex()
    val folds = foldInput.map { foldRegex.match(it) { (dir, lines) -> Fold(dir, lines) }}

    return dots.toSet() to folds
}

private fun Set<Point>.fold(dir: Fold): Set<Point> =
    when (dir) {
        is Up -> {
            val top = filter { (_, y) -> y < dir.line }.toSet()
            val bottom = filter { (_, y) -> y > dir.line }.toSet()
            top + bottom.map { (x, y) -> Point(x, dir.line - (y - dir.line)) }
        }
        is Left -> {
            val left = filter { (x, _) -> x < dir.line }.toSet()
            val right = filter { (x, _) -> x > dir.line }.toSet()
            left + right.map { (x, y) -> Point(dir.line - (x - dir.line), y) }
        }
    }

private fun Set<Point>.print() {
    yRange.forEach { y ->
        xRange.forEach { x ->
            print(if (Point(x, y) in this) "# " else ". ")
        }
        println()
    }
    println()
}

private sealed class Fold {
    abstract val line: Int
    data class Up(override val line: Int) : Fold()
    data class Left(override val line: Int) : Fold()
    companion object {
        operator fun invoke(dir: String, lines: String) = when (dir) {
            "x" -> Left(lines.toInt())
            "y" -> Up(lines.toInt())
            else -> TODO()
        }
    }
}
