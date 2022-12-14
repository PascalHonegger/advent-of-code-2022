fun main() {
    data class Point(val x: Int, val y: Int) {
        operator fun rangeTo(other: Point) = buildList {
            val fromX = minOf(x, other.x)
            val toX = maxOf(x, other.x)
            val fromY = minOf(y, other.y)
            val toY = maxOf(y, other.y)
            for (newX in fromX..toX) {
                for (newY in fromY..toY) {
                    add(Point(x = newX, y = newY))
                }
            }
        }
    }

    fun String.toPoint() = split(',').let { (x, y) -> Point(x = x.toInt(), y = y.toInt()) }
    fun String.toCorners() = split(" -> ").map { it.toPoint() }
    fun String.toPoints() = toCorners().zipWithNext().flatMap { (start, end) -> start..end }
    fun List<String>.toSetOfPoints() = flatMap { it.toPoints() }.toSet()

    val sandStartingPoint = Point(x = 500, y = 0)

    fun simulateSand(fixedPoints: Set<Point>, abyss: Int): Int {
        val points = fixedPoints.toMutableSet()
        var didPlaceSand: Boolean
        do {
            didPlaceSand = false
            var sandPos = sandStartingPoint
            while (sandPos.y < abyss) {
                val currentPos = sandPos
                // Move Down
                sandPos = sandPos.copy(y = sandPos.y + 1)
                if(sandPos !in points) {
                    continue
                }
                // Move Down Left
                sandPos = sandPos.copy(x = sandPos.x - 1)
                if(sandPos !in points) {
                    continue
                }
                // Move Down Right
                sandPos = sandPos.copy(x = sandPos.x + 2)
                if(sandPos !in points) {
                    continue
                }
                // Sand cannot move anymore -> Place it
                didPlaceSand = points.add(currentPos)
                break
            }
        } while (didPlaceSand)
        return points.size - fixedPoints.size
    }

    fun part1(input: List<String>): Int {
        val fixedPoints = input.toSetOfPoints()
        val abyss = fixedPoints.maxOf { it.y }
        return simulateSand(fixedPoints = fixedPoints, abyss = abyss)
    }

    fun part2(input: List<String>): Int {
        val fixedPoints = input.toSetOfPoints()
        val abyss = fixedPoints.maxOf { it.y } + 2
        val floor = Point(x = 0, y = abyss)..Point(x = 1000, y = abyss)
        return simulateSand(fixedPoints = fixedPoints + floor, abyss = abyss)
    }

    val testInput = readInput("Day14_test")
    check(part1(testInput) == 24)
    check(part2(testInput) == 93)

    val input = readInput("Day14")
    println(part1(input))
    println(part2(input))
}
