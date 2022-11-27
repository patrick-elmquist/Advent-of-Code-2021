import util.day
import util.log

// answer #1:
// answer #2:

// Z must always be > 0 to not break the M
fun main() {
    day(n = 24) {
        part1 { input ->
            val allInstructions = input.lines.filter { it.isNotEmpty() }
                .map {
                    val split = it.split(" ")
                    val reg = split[1].first().toVariableName()
                    val value = split.getOrNull(2)?.let { Value(it) }
                    if (value == null) {
                        Instruction.Inp(reg)
                    } else {
                        when (split.first()) {
                            "add" -> Instruction.Add(reg, value)
                            "mul" -> Instruction.Mul(reg, value)
                            "div" -> Instruction.Div(reg, value)
                            "mod" -> Instruction.Mod(reg, value)
                            "eql" -> Instruction.Eql(reg, value)
                            else -> error("Wtf...${split.first()}")
                        }
                    }
                }
            val instructions = mutableListOf<List<Instruction>>()
            var current = mutableListOf<Instruction>()
            for (i in allInstructions) {
                if (i is Instruction.Inp) {
                    if (current.isEmpty()) {
                        current.add(i)
                    } else {
                        instructions.add(current.toList())
                        current = mutableListOf(i)
                    }
                } else {
                    current.add(i)
                }
            }
            instructions.add(current.toList())

            require(instructions.size == 14) {
                "Size was ${instructions.size}"
            }

            solve(
                IndexState(0, Alu((List(5) { 0L }))).log(),
                instructions,
                mutableSetOf(),
                0L
            )
        }
    }
}
var numNumbersProcessedPerSecond = 0.0
var numNumbersProcessed = 0
var startTime = System.currentTimeMillis()
private fun solve(state: IndexState, instructions: List<List<Instruction>>, dp: MutableSet<IndexState>, currNum: Long) {
    if (state in dp) return
    if (state.idx == 14) {
        numNumbersProcessed++
        val delta = System.currentTimeMillis() - startTime
        if (delta > 1000) {
            numNumbersProcessedPerSecond = numNumbersProcessed / delta.toDouble()
            numNumbersProcessed = 0
            startTime = System.currentTimeMillis()
            println("$currNum (qps=$numNumbersProcessedPerSecond)")
        }

        if (state.prevALU.get(VariableName.Z) == 0L) {
            TODO("FOUND z==0: ${state}, currNum: $currNum")
        }
        dp.add(state)
        return
    }
    for (i in 9L downTo 1L) {
        val currAlu = state.prevALU.toMutable()
        for (instruction in instructions[state.idx]) {
            currAlu.consume(instruction, i)
        }
        solve(
            IndexState(state.idx + 1, currAlu.toAlu()),
            instructions,
            dp,
            currNum * 10 + i
        )
    }
    dp.add(state)
}

private enum class VariableName { IN, X, Y, W, Z }

private fun Char.toVariableName(): VariableName {
    return when (this) {
        'x' -> VariableName.X
        'y' -> VariableName.Y
        'z' -> VariableName.Z
        'w' -> VariableName.W
        else -> error("Not supported: $this")
    }
}

private sealed class Value {
    abstract fun resolve(memory: List<Long>): Long
    data class Number(val value: Long) : Value() {
        override fun resolve(memory: List<Long>): Long = value
    }

    data class Variable(val variable: VariableName) : Value() {
        override fun resolve(memory: List<Long>): Long =
            memory[variable.ordinal]
    }

    companion object {
        operator fun invoke(value: String): Value {
            val long = value.toLongOrNull()
            return if (long == null) {
                Variable(value.first().toVariableName())
            } else {
                Number(long)
            }
        }
    }
}

private data class Alu(
    val memory: List<Long>
) {
    operator fun get(variable: VariableName) = memory[variable.ordinal]

    fun toMutable(): MutableAlu {
        return MutableAlu(memory.toList())
    }
}

private class MutableAlu(
    vars: List<Long>
) {
    private var memory = vars.toMutableList()

    fun toAlu() = Alu(memory.toList())

    operator fun get(a: VariableName) = memory[a.ordinal]

    fun consume(instruction: Instruction, input: Long) {
        if (instruction is Instruction.Inp) {
            memory[VariableName.IN.ordinal] = input
        }
        instruction(memory)
    }

    fun print() {
        println(
            memory.withIndex().joinToString {
                val varName = VariableName.values()[it.index]
                "$varName: ${it.value}"
            }
        )
    }
}

private data class IndexState(
    val idx: Int,
    val prevALU: Alu,
)

private sealed class Instruction {
    operator fun invoke(memory: MutableList<Long>): List<Long> =
        memory.apply {
            val (variable, value) = execute(memory)
            set(variable.ordinal, value)
        }

    protected abstract fun execute(memory: List<Long>): Pair<VariableName, Long>

    data class Inp(val a: VariableName) : Instruction() {
        override fun execute(memory: List<Long>) =
            a to memory[VariableName.IN.ordinal]
    }

    data class Add(val a: VariableName, val value: Value) : Instruction() {
        override fun execute(memory: List<Long>) =
            a to (memory[a.ordinal] + value.resolve(memory))
    }

    data class Mul(val a: VariableName, val value: Value) : Instruction() {
        override fun execute(memory: List<Long>) =
            a to (memory[a.ordinal] * value.resolve(memory))
    }

    data class Div(val a: VariableName, val value: Value) : Instruction() {
        override fun execute(memory: List<Long>) =
            a to (memory[a.ordinal].floorDiv(value.resolve(memory)))
    }

    data class Mod(val a: VariableName, val value: Value) : Instruction() {
        override fun execute(memory: List<Long>) =
            a to (memory[a.ordinal] % value.resolve(memory))

    }

    data class Eql(val a: VariableName, val value: Value) : Instruction() {
        override fun execute(memory: List<Long>) =
            a to if (memory[a.ordinal] == value.resolve(memory)) 1L else 0L
    }
}
