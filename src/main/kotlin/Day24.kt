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
            ).getOrThrow()
        }
        part2(expected = 11118151637112L) { input ->
            solve(
                SeenState(0, Alu.new()),
                input.parseInstructions().group(),
                numberRange = 1..9
            ).getOrThrow()
        }
    }
}

private fun solve(
    state: SeenState,
    instructions: List<List<Instruction>>,
    numberRange: IntProgression,
    seenStates: MutableSet<SeenState> = mutableSetOf(),
    currNum: Long = 0L
): Result {
    when {
        state in seenStates -> return Result.Failure
        state.numberIndex == 14 -> {
            return if (state.alu[Register.Z] == 0L) {
                Result.Success(currNum)
            } else {
                seenStates.add(state)
                Result.Failure
            }
        }
        else -> {
            for (i in numberRange) {
                val result = solve(
                    SeenState(
                        numberIndex = state.numberIndex + 1,
                        alu = state.alu.run(instructions[state.numberIndex], i)
                    ),
                    instructions,
                    numberRange,
                    seenStates,
                    currNum * 10 + i,
                )
                if (result is Result.Success) {
                    return result
                }
            }
            seenStates.add(state)
            return Result.Failure
        }
    }
}

private fun Input.parseInstructions(): List<Instruction> =
    lines.filter { it.isNotEmpty() }
        .map { line ->
            val split = line.split(" ")
            val reg = Register(split[1].first())
            val value = split.getOrNull(2)?.let { Value(it) }
            if (value == null) {
                Instruction.Inp(reg, Register.IN)
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

private sealed class Result {
    data class Success(val number: Long) : Result()
    object Failure : Result()

    fun getOrThrow(): Long {
        return when (this) {
            is Success -> number
            is Failure -> error("The result was a failure")
        }
    }
}

private enum class Register {
    IN, X, Y, W, Z;
    companion object {
        operator fun invoke(char: Char) =
            when (char) {
                'x' -> X
                'y' -> Y
                'z' -> Z
                'w' -> W
                else -> error("Not supported: $this")
            }
    }
}

private sealed class Value(private val resolve: List<Long>.() -> Long) {
    data class Number(val value: Long) : Value({ value })

    data class Variable(val register: Register) : Value({ get(register.ordinal) })

    fun resolve(memory: List<Long>) = memory.resolve()

    companion object {
        operator fun invoke(value: String): Value {
            val long = value.toLongOrNull()
            return if (long != null) {
                Number(long)
            } else {
                Variable(Register(value.first()))
            }
        }
    }
}

private data class Alu(val vars: List<Long>) {
    operator fun get(variable: Register) = vars[variable.ordinal]

    fun run(instructions: List<Instruction>, input: Int): Alu {
        val memory = vars.toMutableList()
        for (instruction in instructions) {
            if (instruction is Instruction.Inp) {
                memory[Register.IN.ordinal] = input.toLong()
            }
            instruction(memory)
        }
        return Alu(memory.toList())
    }

    companion object {
        fun new() = Alu(List(5) { 0L })
    }
}

private data class SeenState(
    val numberIndex: Int,
    val alu: Alu
)

private sealed class Instruction {
    operator fun invoke(memory: MutableList<Long>): List<Long> =
        memory.apply {
            val (variable, value) = execute(memory)
            set(variable.ordinal, value)
        }

    protected abstract fun execute(memory: List<Long>): Pair<Register, Long>

    data class Inp(val a: Register, val readFrom: Register) : Instruction() {
        override fun execute(memory: List<Long>) =
            a to memory[readFrom.ordinal]
    }

    data class Add(val a: Register, val value: Value) : Instruction() {
        override fun execute(memory: List<Long>) =
            a to (memory[a.ordinal] + value.resolve(memory))
    }

    data class Mul(val a: Register, val value: Value) : Instruction() {
        override fun execute(memory: List<Long>) =
            a to (memory[a.ordinal] * value.resolve(memory))
    }

    data class Div(val a: Register, val value: Value) : Instruction() {
        override fun execute(memory: List<Long>) =
            a to (memory[a.ordinal].floorDiv(value.resolve(memory)))
    }

    data class Mod(val a: Register, val value: Value) : Instruction() {
        override fun execute(memory: List<Long>) =
            a to (memory[a.ordinal] % value.resolve(memory))
    }

    data class Eql(val a: Register, val value: Value) : Instruction() {
        override fun execute(memory: List<Long>) =
            a to if (memory[a.ordinal] == value.resolve(memory)) 1L else 0L
    }
}
