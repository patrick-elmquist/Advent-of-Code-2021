import util.Input
import util.day

// answer #1: 74929995999389
// answer #2: 11118151637112

fun main() {
    day(n = 24) {
        part1(expected = 74929995999389L) { input ->
            solve(
                SeenState(0, Alu.new()),
                input.parseInstructions().group(),
                numberRange = 9 downTo 1
            )
        }
        part2(expected = 11118151637112L) { input ->
            solve(
                SeenState(0, Alu.new()),
                input.parseInstructions().group(),
                numberRange = 1..9
            )
        }
    }
}

private fun Input.parseInstructions(): List<Instruction> =
    lines.filter { it.isNotEmpty() }
        .map { line ->
            val split = line.split(" ")
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

private fun List<Instruction>.group(): List<List<Instruction>> {
    val instructions = mutableListOf<List<Instruction>>()
    var current = mutableListOf<Instruction>()
    for (i in this) {
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
    return instructions
}

// rewrite returning result
private fun solve(
    state: SeenState,
    instructions: List<List<Instruction>>,
    numberRange: IntProgression,
    seenStates: MutableSet<SeenState> = mutableSetOf(), // should probably be a map?
    currNum: Long = 0L
) {
    if (state in seenStates) return
    if (state.numberIndex == 14) {
        if (state.alu[VariableName.Z] == 0L) {
            TODO("FOUND z==0: ${state}, currNum: $currNum")
        } else {
            seenStates.add(state)
        }
        return
    }
    for (i in numberRange) {
        solve(
            SeenState(
                numberIndex = state.numberIndex + 1,
                alu = state.alu.run(instructions[state.numberIndex], i)
            ),
            instructions,
            numberRange,
            seenStates,
            currNum * 10 + i,
        )
    }
    seenStates.add(state)
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

private sealed class Value(private val resolve: List<Long>.() -> Long) {
    data class Number(val value: Long) : Value({ value })

    data class Variable(val variable: VariableName) : Value({ get(variable.ordinal) })

    fun resolve(memory: List<Long>) = memory.resolve()

    companion object {
        operator fun invoke(value: String): Value {
            val long = value.toLongOrNull()
            return if (long != null) {
                Number(long)
            } else {
                Variable(value.first().toVariableName())
            }
        }
    }
}

private data class Alu(val vars: List<Long>) {
    operator fun get(variable: VariableName) = vars[variable.ordinal]

    fun run(instructions: List<Instruction>, input: Int): Alu {
        val memory = vars.toMutableList()
        for (instruction in instructions) {
            if (instruction is Instruction.Inp) {
                memory[VariableName.IN.ordinal] = input.toLong()
            }
            instruction(memory)
        }
        return Alu(memory.toList())
    }

    companion object {
        fun new() = Alu(List(5) {0L} )
    }
}

private data class SeenState(
    val numberIndex: Int,
    val alu: Alu = Alu(List(5) { 0L }),
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
