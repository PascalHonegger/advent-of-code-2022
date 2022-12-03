fun main() {

    fun Char.priority() = if (isLowerCase()) 1 + (this - 'a') else 27 + (this - 'A')

    fun part1(input: List<String>): Int {
        return input.sumOf { rucksack ->
            val (firstCompartment, secondCompartment) = rucksack.chunked(rucksack.length / 2)
            val overlap = firstCompartment.toSet().intersect(secondCompartment.toSet()).single()
            overlap.priority()
        }
    }

    fun part2(input: List<String>): Int {
        return input.chunked(3).sumOf { group ->
            val badge = group.map { it.toHashSet() }.reduce { acc, it -> acc.apply { retainAll(it) } }.single()
            badge.priority()
        }
    }

    val testInput = readInput("Day03_test")
    check(part1(testInput) == 157)
    check(part2(testInput) == 70)

    val input = readInput("Day03")
    println(part1(input))
    println(part2(input))
}
