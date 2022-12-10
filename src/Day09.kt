import kotlin.math.abs

private enum class HeadDirection(val x: Int, val y: Int) {
    Up(0, 1),
    UpRight(1, 1),
    Right(1, 0),
    DownRight(1, -1),
    Down(0, -1),
    DownLeft(-1, -1),
    Left(-1, 0),
    UpLeft(-1, 1),
}

private data class RopePosition(val x: Int, val y: Int)

private operator fun RopePosition.plus(direction: HeadDirection) = RopePosition(x = x + direction.x, y = y + direction.y)
private infix fun RopePosition.touches(other: RopePosition) = abs(x - other.x) <= 1 && abs(y - other.y) <= 1
private infix fun RopePosition.sameColumnAs(other: RopePosition) = x == other.x
private infix fun RopePosition.sameRowAs(other: RopePosition) = y == other.y
private infix fun RopePosition.leftOf(other: RopePosition) = x < other.x
private infix fun RopePosition.bottomOf(other: RopePosition) = y < other.y
private infix fun RopePosition.directionTowards(other: RopePosition) = when {
    this touches other -> null
    this sameColumnAs other -> when {
        this bottomOf other -> HeadDirection.Up
        else -> HeadDirection.Down
    }

    this sameRowAs other -> when {
        this leftOf other -> HeadDirection.Right
        else -> HeadDirection.Left
    }

    else -> when {
        this leftOf other -> when {
            this bottomOf other -> HeadDirection.UpRight
            else -> HeadDirection.DownRight
        }

        else -> when {
            this bottomOf other -> HeadDirection.UpLeft
            else -> HeadDirection.DownLeft
        }
    }
}

private data class HeadMove(val direction: HeadDirection, val amount: Int)

private class RopeSimulator(length: Int) {
    private var rope = (1..length).map { RopePosition(x = 0, y = 0) }
    private val visitedByTail = mutableSetOf(rope.last())

    val numberOfTouchedByTail get() = visitedByTail.size

    fun moveHead(move: HeadMove) {
        repeat(move.amount) { moveHead(move.direction) }
    }

    private fun moveHead(direction: HeadDirection) {
        rope = buildList {
            for (i in rope.indices) {
                if (i == 0) {
                    // Head
                    add(rope.first() + direction)
                } else {
                    // Tail
                    val currentTail = rope[i]
                    val successor = get(i - 1)
                    val adjustDirection = currentTail directionTowards successor
                    if (adjustDirection != null) {
                        add(currentTail + adjustDirection)
                    } else {
                        add(currentTail)
                    }
                }
            }
        }
        visitedByTail += rope.last()
    }
}

fun main() {
    fun String.toHeadDirection() = when (this) {
        "U" -> HeadDirection.Up
        "R" -> HeadDirection.Right
        "D" -> HeadDirection.Down
        "L" -> HeadDirection.Left
        else -> error("Couldn't parse $this")
    }

    fun String.toHeadMove() =
        split(' ').let { (direction, amount) -> HeadMove(direction.toHeadDirection(), amount.toInt()) }

    fun part1(input: List<String>): Int {
        val moves = input.map { it.toHeadMove() }
        val simulator = RopeSimulator(length = 2)
        moves.forEach { simulator.moveHead(it) }
        return simulator.numberOfTouchedByTail
    }

    fun part2(input: List<String>): Int {
        val moves = input.map { it.toHeadMove() }
        val simulator = RopeSimulator(length = 10)
        moves.forEach { simulator.moveHead(it) }
        return simulator.numberOfTouchedByTail
    }

    check(part1(readInput("Day09_test")) == 13)
    check(part2(readInput("Day09_test2")) == 36)

    val input = readInput("Day09")
    println(part1(input))
    println(part2(input))
}
