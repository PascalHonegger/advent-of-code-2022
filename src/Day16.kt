import kotlin.math.max

fun main() {

    val inputPattern = "Valve (.+) has flow rate=(\\d+); tunnels? leads? to valves? (.+)".toRegex()

    data class Valve(val id: Long, val name: String, val flowRate: Int, val leadsTo: List<String>) {
        override fun toString() = name
    }

    fun String.toValve(id: Long): Valve {
        val (name, flowRate, leadsTo) = inputPattern.matchEntire(this)!!.destructured
        return Valve(id, name, flowRate.toInt(), leadsTo.split(", "))
    }

    fun List<String>.toValves() = mapIndexed { index, row -> row.toValve(1L shl index) }.associateBy { it.name }

    fun Set<Valve>.releasingPressure() = sumOf { it.flowRate }

    fun part1(input: List<String>): Int {
        val valves = input.toValves()
        val cache = HashMap<Triple<Long, Int, Long>, Int>()

        val openValves = mutableSetOf<Valve>()
        fun Valve.countPotentialGain(releasedPressure: Int, remainingMinutes: Int): Int {
            val newPressure = releasedPressure + openValves.releasingPressure()
            if (remainingMinutes == 0) {
                return newPressure
            }

            val cacheKey = Triple(id, remainingMinutes, openValves.fold(0L) { acc, v -> acc or v.id })
            cache[cacheKey]?.let {
                return releasedPressure + it
            }

            fun maxPotentialGain(timeSpent: Int) =
                leadsTo.maxOf { valves.getValue(it).countPotentialGain(newPressure, remainingMinutes - timeSpent) }

            return if (flowRate == 0 || this in openValves) {
                maxPotentialGain(timeSpent = 1)
            } else {
                if (remainingMinutes == 1) {
                    // Cannot open and move at the same time, always open it
                    openValves.add(this)
                    val potentialWithThisOpen = newPressure + openValves.releasingPressure()
                    openValves.remove(this)
                    potentialWithThisOpen
                } else {
                    val potentialWithThisClosed = maxPotentialGain(timeSpent = 1)
                    val releasedWhileOpening = openValves.releasingPressure()
                    openValves.add(this)
                    val potentialWithThisOpen = releasedWhileOpening + maxPotentialGain(timeSpent = 2)
                    openValves.remove(this)
                    max(potentialWithThisClosed, potentialWithThisOpen)
                }
            }.also { cache[cacheKey] = it - releasedPressure; }
        }

        return valves.getValue("AA").countPotentialGain(0, 30)
    }

    fun part2(input: List<String>): Int {
        val valves = input.toValves()
        val cache = HashMap<Triple<Long, Int, Long>, Int>()

        fun List<String>.cartesianProduct(other: List<String>) = flatMap { thisIt ->
            other.map { otherIt ->
                valves.getValue(thisIt) to valves.getValue(otherIt)
            }
        }

        val openValves = mutableSetOf<Valve>()
        fun Pair<Valve, Valve>.countPotentialGain(releasedPressure: Int = 0, minute: Int = 1): Int {
            val newPressure = releasedPressure + openValves.releasingPressure()
            check(minute < 26)

            val (human, elephant) = this
            val cacheKey =
                Triple(human.id or elephant.id, minute, openValves.fold(0L) { acc, v -> acc or v.id })
            cache[cacheKey]?.let {
                return releasedPressure + it
            }

            fun maxPotentialGainIfHumanMoves() = human.leadsTo.maxOf {
                (valves.getValue(it) to elephant).countPotentialGain(
                    newPressure,
                    minute + 1
                )
            }

            fun maxPotentialGainIfElephantMoves() = elephant.leadsTo.maxOf {
                (human to valves.getValue(it)).countPotentialGain(
                    newPressure,
                    minute + 1
                )
            }

            fun maxPotentialGainIfNeitherMoves() = countPotentialGain(newPressure, minute + 1)
            fun maxPotentialGainIfBothMove() = (human.leadsTo.cartesianProduct(elephant.leadsTo)).maxOf {
                it.countPotentialGain(
                    newPressure,
                    minute + 1
                )
            }

            return if (minute == 25) {
                // Cannot open and move at the same time, always open it
                val humanAdded = openValves.add(human)
                val elephantAdded = openValves.add(elephant)
                val potentialWithBothOpen = newPressure + openValves.releasingPressure()
                if (elephantAdded)
                    openValves.remove(elephant)
                if (humanAdded)
                    openValves.remove(human)
                potentialWithBothOpen
            } else {
                val humanCanOpen = human.flowRate != 0 && human !in openValves
                val elephantCanOpen = elephant.flowRate != 0 && elephant !in openValves

                val potentialWithNeitherOpening = maxPotentialGainIfBothMove()
                when {
                    !humanCanOpen && !elephantCanOpen -> potentialWithNeitherOpening
                    !humanCanOpen && elephantCanOpen -> {
                        openValves.add(elephant)
                        val potentialWithElephantOpening = maxPotentialGainIfHumanMoves()
                        openValves.remove(elephant)

                        max(potentialWithNeitherOpening, potentialWithElephantOpening)
                    }

                    humanCanOpen && !elephantCanOpen -> {
                        openValves.add(human)
                        val potentialWithHumanOpening = maxPotentialGainIfElephantMoves()
                        openValves.remove(human)

                        max(potentialWithNeitherOpening, potentialWithHumanOpening)
                    }

                    else -> {
                        val potentialWithEitherOrBothOpening = if (human == elephant) {
                            // Human and Elephant are the same, just assume the human keeps opening
                            openValves.add(human)
                            val potentialWithHumanOpening = maxPotentialGainIfElephantMoves()
                            openValves.remove(human)
                            potentialWithHumanOpening
                        } else {
                            openValves.add(elephant)
                            val potentialWithElephantOpening = maxPotentialGainIfHumanMoves()
                            openValves.remove(elephant)

                            openValves.add(human)
                            val potentialWithHumanOpening = maxPotentialGainIfElephantMoves()
                            openValves.remove(human)

                            openValves.add(human)
                            openValves.add(elephant)
                            val potentialWithBothOpening = maxPotentialGainIfNeitherMoves()
                            openValves.remove(elephant)
                            openValves.remove(human)

                            val potentialWithEitherMoving = max(potentialWithHumanOpening, potentialWithElephantOpening)
                            max(potentialWithEitherMoving, potentialWithBothOpening)
                        }

                        max(potentialWithNeitherOpening, potentialWithEitherOrBothOpening)
                    }
                }
            }.also { cache[cacheKey] = it - releasedPressure }
        }

        return (valves.getValue("AA") to valves.getValue("AA")).countPotentialGain().also { println(it) }
    }

    val testInput = readInput("Day16_test")
    check(part1(testInput) == 1651)
    check(part2(testInput) == 1707)

    val input = readInput("Day16")
    println(part1(input))
    println(part2(input))
}
