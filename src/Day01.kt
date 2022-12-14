fun main() {
    fun List<String>.splitIntoElves(): List<List<Int>> = buildList {
        var elf = mutableListOf<Int>()
        this@splitIntoElves.forEach {
            if (it.isBlank()) {
                add(elf)
                elf = mutableListOf()
            } else {
                elf += it.toInt()
            }
        }
        add(elf)
    }

    fun part1(input: List<String>): Int {
        return input
            .splitIntoElves()
            .maxOf { it.sum() }
    }

    fun part2(input: List<String>): Int {
        return input
            .splitIntoElves()
            .map { it.sum() }
            .sortedDescending()
            .take(3)
            .sum()
    }

    val testInput = readInput("Day01_test")
    check(part1(testInput) == 24000)
    check(part2(testInput) == 45000)

    val input = readInput("Day01")
    println(part1(input))
    println(part2(input))
}
