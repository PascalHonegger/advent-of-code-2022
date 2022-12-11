fun main() {
    data class Item(var worryLevel: Long)
    class Monkey(
        val monkeys: MutableList<Monkey>,
        val items: ArrayDeque<Item>,
        val operation: Pair<String, String>,
        val testDivisibleBy: Int,
        val trueTarget: Int,
        val falseTarget: Int,
        var inspectedItems: Int = 0,
    ) {
        init {
            monkeys += this
        }

        fun takeTurn(limitTo: Long, reduceBy: Long?) {
            while (items.isNotEmpty()) {
                val item = items.removeFirst()
                inspectedItems++
                val old = item.worryLevel
                fun String.parseToLong() = when (this) {
                    "old" -> old
                    else -> this.toLong()
                }

                val (op, argument) = operation
                var new = when (op) {
                    "+" -> old + argument.parseToLong()
                    "*" -> old * argument.parseToLong()
                    else -> error("Cannot execute $operation")
                }

                limitTo.let { new %= it }
                reduceBy?.let { new /= it }

                val target = if (new % testDivisibleBy == 0L) trueTarget else falseTarget
                item.worryLevel = new
                monkeys[target].items += item
            }
        }
    }

    fun List<String>.toMonkeys() = buildList {
        this@toMonkeys.chunked(7).forEach { line ->
            val monkey = line[0].removePrefix("Monkey ").removeSuffix(":").toInt()
            check(monkey == size)
            val startingItems = line[1].removePrefix("  Starting items: ").split(", ").map { it.toLong() }
            val operation = line[2].removePrefix("  Operation: new = old ").split(" ").let { (op, arg) -> op to arg }
            val test = line[3].removePrefix("  Test: divisible by ").toInt()
            val ifTrue = line[4].removePrefix("    If true: throw to monkey ").toInt()
            val ifFalse = line[5].removePrefix("    If false: throw to monkey ").toInt()
            Monkey(
                monkeys = this,
                items = ArrayDeque(startingItems.map { Item(it) }),
                operation = operation,
                testDivisibleBy = test,
                trueTarget = ifTrue,
                falseTarget = ifFalse
            )
        }
    }

    /**
     * We only care about the divisibility of the numbers, so we can reduce the number space to account for that.
     * X mod N == 0
     * (X mod L) mod N == 0
     * given L divides cleanly by N
     */
    fun List<Monkey>.calculateLimit() = map { it.testDivisibleBy }.product().toLong()
    fun List<Monkey>.takeTurn(limitTo: Long, reduceBy: Long? = null) =
        forEach { it.takeTurn(limitTo = limitTo, reduceBy = reduceBy) }
    fun List<Monkey>.sumOfMonkeyBusiness() = map { it.inspectedItems.toLong() }.sortedDescending().take(2).product()

    fun part1(input: List<String>): Long {
        val monkeys = input.toMonkeys()
        val limit = monkeys.calculateLimit()
        repeat(20) {
            monkeys.takeTurn(limitTo = limit, reduceBy = 3L)
        }
        return monkeys.sumOfMonkeyBusiness()
    }

    fun part2(input: List<String>): Long {
        val monkeys = input.toMonkeys()
        val limit = monkeys.calculateLimit()
        repeat(10_000) {
            monkeys.takeTurn(limitTo = limit)
        }
        return monkeys.sumOfMonkeyBusiness()
    }

    val testInput = readInput("Day11_test")
    check(part1(testInput) == 10605L)
    check(part2(testInput) == 2713310158L)

    val input = readInput("Day11")
    println(part1(input))
    println(part2(input))
}
