fun main() {
    class Node(val character: Char, val index: Int, val neighbors: MutableList<Node> = mutableListOf()) {
        val height = when (character) {
            'S' -> 'a' - 'a'
            'E' -> 'z' - 'a'
            else -> character - 'a'
        }

        override fun toString() = "[$index]: $character"
    }

    fun findShortestPath(input: List<String>): Int? {
        lateinit var end: Node
        val distanceFromStart = mutableListOf<Int>()
        val nodes = mutableListOf<Node>()

        for (row in input) {
            for (cell in row) {
                val node = Node(character = cell, index = nodes.size)
                nodes += node
                val isStart = cell == 'S'
                val isEnd = cell == 'E'
                distanceFromStart += if (isStart) 0 else Int.MAX_VALUE
                if (isEnd) {
                    end = node
                }
            }
        }
        val numRows = input.size
        val rowLength = input.first().length
        nodes.forEachIndexed { index, node ->
            fun addAsNeighborIfReachable(neighbor: Node?) {
                neighbor?.let {
                    if ((it.height - 1) <= node.height) {
                        node.neighbors += it
                    }
                }
            }

            val row = index / rowLength
            val col = index % rowLength
            if (col != 0) addAsNeighborIfReachable(nodes[index - 1])
            if (col != rowLength - 1) addAsNeighborIfReachable(nodes[index + 1])
            if (row != 0) addAsNeighborIfReachable(nodes[index - rowLength])
            if (row != numRows - 1) addAsNeighborIfReachable(nodes[index + rowLength])
        }

        val workingSet = nodes.toMutableList()
        while (workingSet.isNotEmpty()) {
            val min = workingSet.minBy { distanceFromStart[it.index] }
            workingSet.remove(min)
            val newDistance = distanceFromStart[min.index] + 1
            if (newDistance < 0) {
                // No path to node found, early exit
                return null
            }
            for (neighbor in min.neighbors) {
                if (distanceFromStart[neighbor.index] > newDistance) {
                    distanceFromStart[neighbor.index] = newDistance
                    if (neighbor == end) {
                        return newDistance
                    }
                }
            }
        }
        return null
    }

    fun String.enumerateStartingPositions() = sequence {
        replace("S", "a").also {
            for (i in indices) {
                if (it[i] == 'a') {
                    yield(it.replaceRange(i..i, "S"))
                }
            }
        }
    }

    fun List<String>.enumerateStartingPositions() = sequence {
        for (i in indices) {
            get(i).enumerateStartingPositions().forEach {
                val copy = toMutableList()
                copy[i] = it
                yield(copy)
            }
        }
    }

    fun part1(input: List<String>): Int {
        return findShortestPath(input) ?: error("No path found")
    }

    fun part2(input: List<String>): Int {
        return input.enumerateStartingPositions()
            .mapNotNull { findShortestPath(it) }.min()
    }

    val testInput = readInput("Day12_test")
    check(part1(testInput) == 31)
    check(part2(testInput) == 29)

    val input = readInput("Day12")
    println(part1(input))
    println(part2(input))
}
