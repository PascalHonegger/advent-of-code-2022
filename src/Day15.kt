import kotlin.math.*

fun main() {

    val inputPattern = "Sensor at x=(.*), y=(.*): closest beacon is at x=(.*), y=(.*)".toRegex()

    data class Point(val x: Int, val y: Int)

    infix fun Point.distanceTo(other: Point): Int {
        // https://en.wikipedia.org/wiki/Taxicab_geometry
        return abs(x - other.x) + abs(y - other.y)
    }

    fun String.toReading(): Pair<Point, Point> {
        val (sensorX, sensorY, beaconX, beaconY) = inputPattern.matchEntire(this)!!.destructured
        return Point(sensorX.toInt(), sensorY.toInt()) to Point(beaconX.toInt(), beaconY.toInt())
    }

    fun Pair<Point, Point>.reachableRangeByRow() = sequence {
        val (sensor, closestBeacon) = this@reachableRangeByRow
        val taxicabDistance = sensor distanceTo closestBeacon
        for (offset in taxicabDistance downTo 0) {
            val width = taxicabDistance - offset
            val range = sensor.x - width..sensor.x + width
            yield(sensor.y + offset to range)
            yield(sensor.y - offset to range)
        }
    }

    fun part1(input: List<String>, measureY: Int): Int {
        val readings = input.map { it.toReading() }
        val reachablePoints = readings
            .asSequence()
            .flatMap { it.reachableRangeByRow() }
            .filter { (y, _) -> y == measureY }
            .flatMap { (y, range) -> range.map { Point(it, y) } }
            .toSet()
        val sensorAndBeaconsOnLine =
            readings.flatMap { listOf(it.first, it.second) }.filter { it.y == measureY }.toSet()

        return (reachablePoints - sensorAndBeaconsOnLine).size
    }

    fun part2(input: List<String>, maximum: Int): Long {
        val readings = input.map { it.toReading() }
        val solutionSpace = 0..maximum
        val blockedAreasByRow = readings
            .asSequence()
            .flatMap { it.reachableRangeByRow() }
            .filter { (y, _) -> y in solutionSpace }
            .groupBy(keySelector = { it.first }, valueTransform = { it.second })


        for ((y, blockedAreas) in blockedAreasByRow.entries) {
            var solution = solutionSpace.first
            var hitBlockedArea: Boolean
            while (solution < solutionSpace.last) {
                hitBlockedArea = false
                for (blockedArea in blockedAreas) {
                    if (solution in blockedArea) {
                        solution = blockedArea.last + 1
                        hitBlockedArea = true
                        break
                    }
                }
                if (!hitBlockedArea) {
                    return solution.toLong() * 4_000_000L + y.toLong()
                }
            }
        }
        error("Distress signal not found")
    }

    val testInput = readInput("Day15_test")
    check(part1(testInput, measureY = 10) == 26)
    check(part2(testInput, maximum = 20) == 56000011L)

    val input = readInput("Day15")
    println(part1(input, measureY = 2_000_000))
    println(part2(input, maximum = 4_000_000))
}
