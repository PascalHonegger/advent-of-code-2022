import kotlin.math.max
import kotlin.system.measureTimeMillis

fun main() {
    val inputPattern =
        "Blueprint (.+): Each ore robot costs (.+) ore. Each clay robot costs (.+) ore. Each obsidian robot costs (.+) ore and (.+) clay. Each geode robot costs (.+) ore and (.+) obsidian.".toRegex()

    data class Blueprint(
        val id: Int,
        val oreRobotOreCost: Int,
        val clayRobotOreCost: Int,
        val obsidianRobotOreCost: Int,
        val obsidianRobotClayCost: Int,
        val geodeRobotOreCost: Int,
        val geodeRobotObsidianCost: Int
    ) {
        override fun toString() = "Blueprint $id"
    }

    data class RoboterState(
        val oreRobots: Int = 0,
        val clayRobots: Int = 0,
        val obsidianRobots: Int = 0,
        val geodeRobots: Int = 0,
        val ore: Int = 0,
        val clay: Int = 0,
        val obsidian: Int = 0,
        val geode: Int = 0,
    )

    fun RoboterState.tick() = copy(
        ore = ore + oreRobots,
        clay = clay + clayRobots,
        obsidian = obsidian + obsidianRobots,
        geode = geode + geodeRobots,
    )

    fun String.toBlueprint(): Blueprint {
        val match = inputPattern.matchEntire(this)!!.groupValues.drop(1).map { it.toInt() }
        return Blueprint(
            id = match[0],
            oreRobotOreCost = match[1],
            clayRobotOreCost = match[2],
            obsidianRobotOreCost = match[3],
            obsidianRobotClayCost = match[4],
            geodeRobotOreCost = match[5],
            geodeRobotObsidianCost = match[6],
        )
    }

    fun Blueprint.runSimulation(minutes: Int): Int {
        val cache = HashMap<Pair<RoboterState, Int>, Int>()

        fun simulateMinute(
            state: RoboterState,
            remainingMinutes: Int
        ): Int {
            val cacheKey = state to remainingMinutes
            cache[cacheKey]?.let { return it }
            if (remainingMinutes == 0) {
                return state.geode
            }

            val newState = state.tick()

            // If we can buy Geode robot, always do it
            if (geodeRobotOreCost <= state.ore && geodeRobotObsidianCost <= state.obsidian) {
                val boughtGeodeRobot = newState.copy(
                    ore = newState.ore - geodeRobotOreCost,
                    obsidian = newState.obsidian - geodeRobotObsidianCost,
                    geodeRobots = newState.geodeRobots + 1,
                )
                return simulateMinute(boughtGeodeRobot, remainingMinutes - 1)
            }

            var maxGeodes = simulateMinute(newState, remainingMinutes - 1)

            val maxOreCost = maxOf(oreRobotOreCost, clayRobotOreCost, obsidianRobotOreCost, geodeRobotOreCost)
            if (remainingMinutes > oreRobotOreCost &&
                state.oreRobots < maxOreCost &&
                oreRobotOreCost <= state.ore) {
                val boughtOreRobot = newState.copy(
                    ore = newState.ore - oreRobotOreCost,
                    oreRobots = newState.oreRobots + 1,
                )
                maxGeodes = max(maxGeodes, simulateMinute(boughtOreRobot, remainingMinutes - 1))
            }

            val maxClayCost = maxOf(clayRobotOreCost, obsidianRobotClayCost)
            if (state.clayRobots < maxClayCost && clayRobotOreCost <= state.ore) {
                val boughtClayRobot = newState.copy(
                    ore = newState.ore - clayRobotOreCost,
                    clayRobots = newState.clayRobots + 1,
                )
                maxGeodes = max(maxGeodes, simulateMinute(boughtClayRobot, remainingMinutes - 1))
            }

            val maxObsidianCost = geodeRobotObsidianCost
            if (state.obsidianRobots < maxObsidianCost && obsidianRobotOreCost <= state.ore && obsidianRobotClayCost <= state.clay) {
                val boughtObsidianRobot = newState.copy(
                    ore = newState.ore - obsidianRobotOreCost,
                    clay = newState.clay - obsidianRobotClayCost,
                    obsidianRobots = newState.obsidianRobots + 1,
                )
                maxGeodes = max(maxGeodes, simulateMinute(boughtObsidianRobot, remainingMinutes - 1))
            }

            cache[cacheKey] = maxGeodes
            return maxGeodes
        }

        val initialState = RoboterState(oreRobots = 1)
        return simulateMinute(initialState, minutes).also { println("Took ${cache.size} simulations") } // 4048776 -> 4048189 -> 2045829
    }

    fun part1(input: List<String>): Int {
        return input
            .map { it.toBlueprint() }
            .sumOf { blueprint ->
                val maxGeodes: Int
                val duration = measureTimeMillis {
                    maxGeodes = blueprint.runSimulation(minutes = 24)
                }
                println("$blueprint: $maxGeodes in ${duration}ms")
                blueprint.id * maxGeodes
            }
    }

    fun part2(input: List<String>): Int {
        return input
            .take(3)
            .map { it.toBlueprint() }
            .map { blueprint ->
                val maxGeodes: Int
                val duration = measureTimeMillis {
                    maxGeodes = blueprint.runSimulation(minutes = 32)
                }
                println("$blueprint: $maxGeodes in ${duration}ms")
                maxGeodes
            }.product()
    }

    val testInput = readInput("Day19_test")
    check(part1(testInput) == 33)
    check(part2(testInput) == 56 * 62)

    val input = readInput("Day19")
    println(part1(input))
    println(part2(input))
}
