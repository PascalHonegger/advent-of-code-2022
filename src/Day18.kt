fun main() {
    data class Cube(val x: Int, val y: Int, val z: Int)

    fun String.toCube() = split(",").map { it.toInt() }.let { (x, y, z) -> Cube(x, y, z) }
    fun Cube.neighbors() =
        listOf(copy(x = x + 1), copy(y = y + 1), copy(z = z + 1), copy(x = x - 1), copy(y = y - 1), copy(z = z - 1))

    fun part1(input: List<String>): Int {
        val lavaCubes = input.mapTo(HashSet()) { it.toCube() }
        val possibleAirCubes = lavaCubes.flatMap { it.neighbors() }
        val airCubes = possibleAirCubes - lavaCubes
        return airCubes.size
    }

    fun part2(input: List<String>): Int {
        val lavaCubes = input.mapTo(HashSet()) { it.toCube() }
        val possibleAirCubes = lavaCubes.flatMap { it.neighbors() }
        val airCubes = possibleAirCubes - lavaCubes
        val xRange = airCubes.minOf { it.x }..airCubes.maxOf { it.x }
        val yRange = airCubes.minOf { it.y }..airCubes.maxOf { it.y }
        val zRange = airCubes.minOf { it.z }..airCubes.maxOf { it.z }

        val reachableByWater = mutableSetOf<Cube>()

        fun recurseOutside(cube: Cube) {
            for (neighbor in cube.neighbors()) {
                if (neighbor.x !in xRange || neighbor.y !in yRange || neighbor.z !in zRange || neighbor in reachableByWater || neighbor in lavaCubes) {
                    continue
                }
                reachableByWater += neighbor
                recurseOutside(neighbor)
            }
        }

        recurseOutside(Cube(xRange.first, yRange.first, zRange.first))

        val airCubesReachableByWater = airCubes.filter { it in reachableByWater }
        return airCubesReachableByWater.size
    }

    val testInput = readInput("Day18_test")
    check(part1(testInput) == 64)
    check(part2(testInput) == 58)

    val input = readInput("Day18")
    println(part1(input))
    println(part2(input))
}
