import kotlin.math.max

private enum class SearchDirection { Right, Down, Left, Up }

fun main() {
    data class Point(val x: Int, val y: Long)
    data class Rock(val points: List<Point>) {
        val topY get() = points.last().y
    }

    /*
    ####

    .#.
    ###
    .#.

    ..#
    ..#
    ###

    #
    #
    #
    #

    ##
    ##

     */
    val knownRocksAtFloor = listOf(
        Rock(listOf(Point(3, 1), Point(4, 1), Point(5, 1), Point(6, 1))),
        Rock(listOf(Point(4, 1), Point(3, 2)/*, Point(4, 2) */, Point(5, 2), Point(4, 3))),
        Rock(listOf(Point(3, 1), Point(4, 1), Point(5, 1), Point(5, 2), Point(5, 3))),
        Rock(listOf(Point(3, 1), Point(3, 2), Point(3, 3), Point(3, 4))),
        Rock(listOf(Point(3, 1), Point(4, 1), Point(3, 2), Point(4, 2))),
    )

    fun Point.withOffset(deltaX: Int = 0, deltaY: Long = 0) = Point(x = x + deltaX, y = y + deltaY)
    fun Point.oneUp() = withOffset(deltaY = 1L)
    fun Point.oneDown() = withOffset(deltaY = -1L)
    fun Point.oneRight() = withOffset(deltaX = 1)
    fun Point.oneLeft() = withOffset(deltaX = -1)
    fun Rock.withOffset(deltaX: Int = 0, deltaY: Long = 0) = Rock(points.map { it.withOffset(deltaX, deltaY) })

    fun SearchDirection.rotatedCounterClockwise() = when (this) {
        SearchDirection.Right -> SearchDirection.Up
        SearchDirection.Down -> SearchDirection.Right
        SearchDirection.Left -> SearchDirection.Down
        SearchDirection.Up -> SearchDirection.Left
    }

    fun SearchDirection.rotatedClockwise() = when (this) {
        SearchDirection.Right -> SearchDirection.Down
        SearchDirection.Down -> SearchDirection.Left
        SearchDirection.Left -> SearchDirection.Up
        SearchDirection.Up -> SearchDirection.Right
    }

    fun Point.move(direction: SearchDirection) = when (direction) {
        SearchDirection.Right -> oneRight()
        SearchDirection.Down -> oneDown()
        SearchDirection.Left -> oneLeft()
        SearchDirection.Up -> oneUp()
    }

    fun simulateFallingRocks(windPattern: String, numRocks: Long): Long {
        var towerHeight = 0L

        var blocked = emptySet<Point>()

        val horizontalLimit = 1..7
        val verticalLimit = 1L..Long.MAX_VALUE

        fun Point.isOutOfBounds() = x !in horizontalLimit || y !in verticalLimit
        fun Point.isBlocked() = this in blocked
        fun Point.isLegal() = !isOutOfBounds() && !isBlocked()
        fun Rock.isLegal() = points.all { it.isLegal() }
        fun followOuterWall(points: Set<Point>): Set<Point> {
            return buildSet {
                fun Point.isEmpty() = !isOutOfBounds() && this !in points

                var position = Point(1, points.filter { it.x == 1 }.maxOfOrNull { it.y + 1L } ?: 1L)
                var direction = SearchDirection.Right
                while (true) {
                    val front = position.move(direction)
                    val right = position.move(direction.rotatedClockwise())

                    when {
                        direction == SearchDirection.Up && right.isOutOfBounds() -> break
                        right.isEmpty() -> {
                            direction = direction.rotatedClockwise()
                            position = right
                        }

                        else -> {
                            if (!right.isOutOfBounds()) add(right)
                            when {
                                front.isEmpty() -> {
                                    position = front
                                }

                                else -> {
                                    direction = direction.rotatedCounterClockwise()
                                }
                            }
                        }
                    }
                }
            }
        }

        fun Set<Point>.normalized(): Set<Point> = map { it.withOffset(deltaY = -towerHeight) }.toSet()

        var windOffset = 0
        var rockOffset = 0
        var placedRocks = 0L

        val knownPatterns = mutableMapOf<Triple<Int, Int, Set<Point>>, Pair<Long, Long>>()

        while (placedRocks < numRocks) {
            var newRock = knownRocksAtFloor[rockOffset].withOffset(deltaY = towerHeight + 3L)
            rockOffset = (rockOffset + 1) % knownRocksAtFloor.size
            while (true) {
                val windDirection = windPattern[windOffset]
                windOffset = (windOffset + 1) % windPattern.length
                val windMoved = when (windDirection) {
                    '<' -> newRock.withOffset(deltaX = -1)
                    '>' -> newRock.withOffset(deltaX = +1)
                    else -> error("Unknown $windDirection")
                }

                if (windMoved.isLegal()) {
                    newRock = windMoved
                }

                val gravityMoved = newRock.withOffset(deltaY = -1)

                if (gravityMoved.isLegal()) {
                    newRock = gravityMoved
                    continue
                }

                blocked = followOuterWall(blocked + newRock.points)
                towerHeight = max(towerHeight, newRock.topY)
                break
            }
            placedRocks++

            val patternKey = Triple(windOffset, rockOffset, blocked.normalized())
            val pattern = knownPatterns[patternKey]
            if (pattern != null) {
                val (rocksAtTheTime, towerHeightAtTheTime) = pattern
                val rocksInPattern = placedRocks - rocksAtTheTime
                val heightInPattern = towerHeight - towerHeightAtTheTime
                val remainingRocks = numRocks - placedRocks
                val patternRepetitions = (remainingRocks / rocksInPattern)
                placedRocks += rocksInPattern * patternRepetitions
                val totalHeightDifference = heightInPattern * patternRepetitions
                towerHeight += totalHeightDifference
                blocked = blocked.map { it.withOffset(deltaY = totalHeightDifference) }.toSet()
            } else {
                knownPatterns[patternKey] = placedRocks to towerHeight
            }
        }

        return towerHeight
    }

    fun part1(input: String) = simulateFallingRocks(input, numRocks = 2_022L)
    fun part2(input: String) = simulateFallingRocks(input, numRocks = 1_000_000_000_000L)

    val testInput = readTextInput("Day17_test")
    check(part1(testInput) == 3068L)
    check(part2(testInput) == 1514285714288L)

    val input = readTextInput("Day17")
    println(part1(input))
    println(part2(input))
}
