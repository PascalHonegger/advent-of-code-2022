private sealed interface CpuInstruction {
    val cycles: Int

    object Noop : CpuInstruction {
        override val cycles = 1
    }

    data class AddX(val value: Int) : CpuInstruction {
        override val cycles = 2
    }
}

private data class RegisterState(val x: Int)

private class CpuSimulator {
    private var state = RegisterState(x = 1)
    val histogram = mutableListOf(state)

    fun execute(instruction: CpuInstruction) {
        repeat(instruction.cycles) {
            histogram.add(state)
        }

        // Mutate State
        state = when (instruction) {
            CpuInstruction.Noop -> state
            is CpuInstruction.AddX -> state.copy(x = state.x + instruction.value)
        }
    }
}

const val crtLineLength = 40

private class CrtSimulator {
    fun print(cycle: Int, state: RegisterState) {
        val spritePosition = (state.x - 1)..(state.x + 1)
        val cursorPosition = (cycle - 1) % crtLineLength
        if (cursorPosition in spritePosition) {
            print('#')
        } else {
            print('.')
        }
        if (cursorPosition == crtLineLength - 1) {
            println()
        }
    }
}

fun main() {
    fun String.toCpuInstruction(): CpuInstruction {
        val instruction = takeWhile { it != ' ' }
        val argument = takeLastWhile { it != ' ' }
        return when (instruction) {
            "noop" -> CpuInstruction.Noop
            "addx" -> CpuInstruction.AddX(argument.toInt())
            else -> error("Couldn't parse $instruction")
        }
    }

    fun part1(input: List<String>): Int {
        val instruction = input.map { it.toCpuInstruction() }
        val simulator = CpuSimulator()
        instruction.forEach { simulator.execute(it) }
        var signalStrength = 0
        for (i in 20 until simulator.histogram.size step 40) {
            signalStrength += simulator.histogram[i].x * i
        }
        return signalStrength
    }

    fun part2(input: List<String>) {
        val instruction = input.map { it.toCpuInstruction() }
        val simulator = CpuSimulator()
        instruction.forEach { simulator.execute(it) }
        val crt = CrtSimulator()
        simulator.histogram.forEachIndexed { index, registerState ->
            if (index > 0)
                crt.print(index, registerState)
        }
    }

    val testInput = readInput("Day10_test")
    check(part1(testInput) == 13140)
    part2(testInput)

    val input = readInput("Day10")
    println(part1(input))
    part2(input)
}
