
import Instruction.Add
import Instruction.Div
import Instruction.Eql
import Instruction.Inp
import Instruction.Mod
import Instruction.Mul
import util.day
import util.log

// answer #1:
// answer #2:

// Z must always be > 0 to not break the Mod
fun main() {
    day(n = 24) {
        part1 { input ->
            val instructions = input.lines.filter { it.isNotEmpty() }
                .map { val split = it.split(" ")
                val reg = split[1].first()
                val value = split.getOrNull(2)?.let { Value(it) }
                if (value == null) {
                    Inp(reg)
                } else {
                    when (split.first()) {
                        "add" -> Add(reg, value)
                        "mul" -> Mul(reg, value)
                        "div" -> Div(reg, value)
                        "mod" -> Mod(reg, value)
                        "eql" -> Eql(reg, value)
                        else -> error("Wtf...${split.first()}")
                    }
                }
            }

            val alu = Alu()
            val input = "55555555555555".toList().map { it.digitToInt().toLong() }.log()
            alu.run(input, instructions).log().getValue('z')
        }
    }
}

private class Alu() {

    fun run(input: List<Long>, instructions: List<Instruction>): Map<Char, Long> {
        val variables = mutableMapOf(
            'w' to 0L,
            'x' to 0L,
            'y' to 0L,
            'z' to 0L
        )
        val inputs = input.toMutableList()
        instructions.forEach {
            if (it is Inp) variables['i'] = inputs.removeFirst().also {
                variables.log("var before input:$it :")
            }

            variables += it.run(variables)

            if (it is Inp) variables.remove('i').also {
                variables.log("var after input:$it :")
                println()
            }
        }

        return variables
    }
}

private sealed class Instruction() {
    fun run(memory: Map<Char, Long>): Map<Char, Long> = memory + execute(memory)

    protected abstract fun execute(memory: Map<Char, Long>): Pair<Char, Long>

    data class Inp(val a: Char) : Instruction() {
        override fun execute(memory: Map<Char, Long>) =
            a to memory.getValue('i')
    }

    data class Add(val a: Char, val value: Value) : Instruction() {
        override fun execute(memory: Map<Char, Long>) =
            a to (memory.getValue(a) + value.resolve(memory))
    }
    data class Mul(val a: Char, val value: Value) : Instruction() {
        override fun execute(memory: Map<Char, Long>) =
            a to (memory.getValue(a) * value.resolve(memory))
    }
    data class Div(val a: Char, val value: Value) : Instruction() {
        override fun execute(memory: Map<Char, Long>) =
            a to (memory.getValue(a).floorDiv(value.resolve(memory)))
    }
    data class Mod(val a: Char, val value: Value) : Instruction() {
        override fun execute(memory: Map<Char, Long>) =
            a to (memory.getValue(a) % value.resolve(memory))

    }
    data class Eql(val a: Char, val value: Value) : Instruction() {
        override fun execute(memory: Map<Char, Long>) =
            a to if (memory.getValue(a) == value.resolve(memory)) 1L else 0L
    }
}

private sealed class Value {
    abstract fun resolve(memory: Map<Char, Long>): Long
    data class Number(val value: Long) : Value() {
        override fun resolve(memory: Map<Char, Long>): Long = value
    }
    data class Variable(val variable: Char) : Value() {
        override fun resolve(memory: Map<Char, Long>): Long =
            memory.getValue(variable)
    }

    companion object {
        operator fun invoke(value: String): Value {
            val long = value.toLongOrNull()
            return if (long == null) {
                Variable(value.first())
            } else {
                Number(long)
            }
        }
    }
}