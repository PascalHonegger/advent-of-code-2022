fun main() {
    fun String.toCleaningRange() = split('-').let { (from, to) -> from.toInt()..to.toInt() }
    fun String.toCleaningRanges() = split(',').map { it.toCleaningRange() }
    operator fun IntRange.contains(other: IntRange) = first <= other.first && last >= other.last

    fun part1(input: List<String>): Int {
        return input.count { group ->
            val (elf1, elf2) = group.toCleaningRanges()

            elf1 in elf2 || elf2 in elf1
        }
    }

    fun part2(input: List<String>): Int {
        return input.count { group ->
            val (elf1, elf2) = group.toCleaningRanges()

            elf1.intersect(elf2).isNotEmpty()
        }
    }

    val testInput = readInput("Day04_test")
    check(part1(testInput) == 2)
    check(part2(testInput) == 4)

    val input = readInput("Day04")
    println(part1(input))
    println(part2(input))
}
