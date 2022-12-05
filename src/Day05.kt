fun main() {
    data class Operation(val amount: Int, val from: Int, val to: Int)

    fun parseStack(input: List<String>): List<ArrayDeque<Char>> {
        /*
                [D]
            [N] [C]
            [Z] [M] [P]
             1   2   3
         */
        val reversed = input.reversed()
        val numberOfStacks = (reversed.first().length + 1) / 4
        val stacks = (1..numberOfStacks).map { ArrayDeque<Char>() }
        reversed.drop(1).forEach {
            it.chunked(4).withIndex().forEach { (index, it) ->
                val crate = it[1]
                if (crate != ' ') {
                    stacks[index].addLast(crate)
                }
            }
        }
        return stacks
    }

    fun parseOperations(input: List<String>): List<Operation> {
        return input.map {
            // move AMOUNT from FROM to TO
            val parts = it.split(' ')
            Operation(parts[1].toInt(), parts[3].toInt() - 1, parts[5].toInt() - 1)
        }
    }

    fun parseInput(input: List<String>): Pair<List<ArrayDeque<Char>>, List<Operation>> {
        val splitAt = input.indexOfFirst { it.isEmpty() }
        val stackLines = input.take(splitAt)
        val operationLines = input.drop(splitAt + 1)
        return parseStack(stackLines) to parseOperations(operationLines)
    }

    fun part1(input: List<String>): String {
        val (stacks, operations) = parseInput(input)
        for (operation in operations) {
            val from = stacks[operation.from]
            val to = stacks[operation.to]
            repeat(operation.amount) {
                to.addLast(from.removeLast())
            }
        }
        return stacks.map { it.last() }.joinToString(separator = "")
    }

    fun part2(input: List<String>): String {
        val (stacks, operations) = parseInput(input)
        for (operation in operations) {
            val from = stacks[operation.from]
            val to = stacks[operation.to]
            val removed = mutableListOf<Char>()
            repeat(operation.amount) {
                removed += from.removeLast()
            }
            removed.reversed().forEach { to.addLast(it) }
        }
        return stacks.map { it.last() }.joinToString(separator = "")
    }

    val testInput = readInput("Day05_test")
    check(part1(testInput) == "CMZ")
    check(part2(testInput) == "MCD")

    val input = readInput("Day05")
    println(part1(input))
    println(part2(input))
}
