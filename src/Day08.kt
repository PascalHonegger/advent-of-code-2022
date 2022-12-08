import kotlin.math.max

fun main() {
    fun List<String>.toGrid(): List<List<Int>> = map { row -> row.map { it.digitToInt() } }
    fun List<List<Int>>.dimensions() = Pair(size, first().size)
    fun List<List<Int>>.columns(): List<List<Int>> {
        val (_, columns) = dimensions()
        val response = (1..columns).map { mutableListOf<Int>() }
        forEach { row ->
            row.forEachIndexed { index, col ->
                response[index] += col
            }
        }
        return response
    }

    fun part1(input: List<String>): Int {
        val grid = input.toGrid()
        val gridColumns = grid.columns()
        val (rows, cols) = grid.dimensions()
        var visible = 0
        for (rowIndex in 0 until rows) {
            for (colIndex in 0 until cols) {
                val height = grid[rowIndex][colIndex]
                val row = grid[rowIndex]
                val col = gridColumns[colIndex]
                if (
                    row.subList(0, colIndex).none { it >= height } ||
                    row.subList(colIndex + 1, row.size).none { it >= height } ||
                    col.subList(0, rowIndex).none { it >= height } ||
                    col.subList(rowIndex + 1, row.size).none { it >= height }
                ) {
                    visible++
                }
            }
        }
        return visible
    }

    fun part2(input: List<String>): Int {
        val grid = input.toGrid()
        val gridColumns = grid.columns()
        val (rows, cols) = grid.dimensions()
        var topScenicScore = 0
        for (rowIndex in 1 until rows - 1) {
            for (colIndex in 1 until cols - 1) {
                val height = grid[rowIndex][colIndex]
                val row = grid[rowIndex]
                val col = gridColumns[colIndex]

                var left = row.subList(0, colIndex).takeLastWhile { it < height }.count()
                var right = row.subList(colIndex + 1, row.size).takeWhile { it < height }.count()
                var top = col.subList(0, rowIndex).takeLastWhile { it < height }.count()
                var bottom = col.subList(rowIndex + 1, row.size).takeWhile { it < height }.count()

                // Add tree we stopped at, if we didn't reach border
                if (left < colIndex) left++
                if (right < (row.size - (colIndex + 1))) right++
                if (top < rowIndex) top++
                if (bottom < (row.size - (rowIndex + 1))) bottom++

                val scenicScore = left * right * top * bottom
                topScenicScore = max(topScenicScore, scenicScore)
            }
        }
        return topScenicScore
    }

    val testInput = readInput("Day08_test")
    check(part1(testInput) == 21)
    check(part2(testInput) == 8)

    val input = readInput("Day08")
    println(part1(input))
    println(part2(input))
}
