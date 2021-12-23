
import Number.Pair
import Number.Value
import util.Input
import util.day
import kotlin.math.ceil
import kotlin.math.floor
import kotlin.math.max

// answer #1: 3675
// answer #2: 4650

fun main() {
    day(n = 18) {
        part1(expected = 3675) { input ->
            input.parseNumbers().reduce(Number::sum).magnitude()
        }

        part2(expected = 4650) { input ->
            var max = Int.MIN_VALUE
            for ((i, a) in input.lines.withIndex()) {
                for ((j, b) in input.lines.withIndex()) {
                    if (i == j) continue
                    max = max(max, (a.parseNumber() + b.parseNumber()).magnitude())
                    max = max(max, (b.parseNumber() + a.parseNumber()).magnitude())
                }
            }
            max
        }
    }
}

private fun Number.findExplode(level: Int = 0): Pair? = when (this) {
    is Value -> null
    is Pair -> {
        if (level == 4) this
        else left.findExplode(level + 1) ?: right.findExplode(level + 1)
    }
}

private fun Number.findSplit(): Value? =
    when (this) {
        is Pair -> left.findSplit() ?: right.findSplit()
        is Value -> if (value >= 10) this else null
    }

private fun Value.split(): Pair =
    Pair(Value(floor(value / 2f).toInt()), Value(ceil(value / 2f).toInt()))

private fun Pair.explode(): Value {
    findParent(Pair::left)?.left?.rightMost()?.let { it.value += (left as Value).value }
    findParent(Pair::right)?.right?.leftMost()?.let { it.value += (right as Value).value }
    return Value(0)
}

private fun Pair.findParent(side: Pair.() -> Number): Pair? {
    var current = this

    while (current.parent != null) {
        if (current.parent!!.side() !== current) {
            return current.parent
        } else {
            current = current.parent!!
        }
    }

    return null
}

private fun Number.rightMost(): Value =
    when (this) {
        is Value -> this
        is Pair -> right.rightMost()
    }

private fun Number.leftMost(): Value =
    when (this) {
        is Value -> this
        is Pair -> left.leftMost()
    }

private fun Number.reduce(): Number {
    while (true) {
        val nodeToExplode = findExplode()
        if (nodeToExplode != null) {
            nodeToExplode.parent?.replace(nodeToExplode, nodeToExplode.explode())
            continue
        }

        val nodeToSplit = findSplit()
        if (nodeToSplit != null) {
            nodeToSplit.parent?.replace(nodeToSplit, nodeToSplit.split())
            continue
        }

        return this
    }
}

private fun Number.magnitude(): Int =
    when (this) {
        is Value -> value
        is Pair -> left.magnitude() * 3 + right.magnitude() * 2
    }

private sealed class Number(var parent: Pair? = null) {
    data class Value(var value: Int) : Number() {
        override fun toString(): String = "$value"
    }

    data class Pair(var left: Number, var right: Number) : Number() {
        init {
            left.parent = this
            right.parent = this
        }

        fun replace(old: Number, new: Number) {
            if (old == left) {
                left = new
            } else {
                right = new
            }
            new.parent = this
        }

        override fun toString(): String = "[$left,$right]"
    }

    operator fun plus(other: Number): Number = Pair(this, other).reduce()

    companion object {
        fun sum(a: Number, b: Number) = a + b
    }
}


private fun Input.parseNumbers(): List<Number> = lines.map { it.parseNumber() }

private fun String.parseNumber(): Number {
    if (!contains(",")) return Value(toInt())

    var level = 0
    var parsing = true
    val (left, right) = partition { c ->
        when (c) {
            '[' -> level++
            ']' -> level--
            ',' -> if (level == 0) parsing = false
        }
        parsing
    }

    return if (right.isNotEmpty()) {
        Pair(
            left = left.stripOuterBrackets().parseNumber(),
            right = right.drop(1).stripOuterBrackets().parseNumber()
        )
    } else {
        stripOuterBrackets().parseNumber()
    }
}

private fun String.stripOuterBrackets(): String = removeSurrounding(prefix = "[", suffix = "]")
