fun main() {
    class Node(val value: Long) {
        lateinit var previous: Node
        lateinit var next: Node

        override fun toString() = value.toString()
    }

    class MutableRingBuffer(initialItems: List<Long>) : Iterable<Node> {
        private var head: Node
        private var zero: Node
        val size: Int

        init {
            size = initialItems.size

            val nodes = initialItems.map { Node(value = it) }
            head = nodes.first()
            zero = nodes.single { it.value == 0L }

            nodes.forEachIndexed { index, current ->
                current.previous = nodes.getOrElse(index - 1) { nodes.last() }
                current.next = nodes.getOrElse(index + 1) { head }
            }
        }

        fun doMixing(node: Node) {
            if (node.value == 0L) return

            val oldPrevious = node.previous

            var current = node
            if (node.value > 0) {
                for (i in 0 until node.value % (size - 1)) {
                    current = current.next
                }
            } else {
                for (i in 0 until (-node.value) % (size - 1)) {
                    current = current.previous
                }
            }

            // Unlink node
            link(node.previous, node.next)

            // Add link at correct location
            if (node.value > 0) {
                link(node, current.next)
                link(current, node)
            } else {
                link(current.previous, node)
                link(node, current)
            }

            if (node == head) {
                head = oldPrevious.next
            }
        }

        private fun link(a: Node, b: Node) {
            a.next = b
            b.previous = a
        }

        operator fun get(index: Int): Node {
            val optimizedIndex = index % size
            var current = zero
            repeat(optimizedIndex) {
                current = current.next
            }
            return current
        }

        override operator fun iterator() = iterator {
            var current = head
            repeat(size) {
                yield(current)
                current = current.next
            }
        }

        override fun toString(): String = joinToString()
    }

    fun part1(input: List<String>): Long {
        val mutableRingBuffer = MutableRingBuffer(input.map { it.toLong() })
        val initialNodes = mutableRingBuffer.toList()
        for (node in initialNodes) {
            mutableRingBuffer.doMixing(node)
        }
        return mutableRingBuffer[1_000].value + mutableRingBuffer[2_000].value + mutableRingBuffer[3_000].value
    }

    fun part2(input: List<String>): Long {
        val mutableRingBuffer = MutableRingBuffer(input.map { it.toLong() * 811589153L })
        val initialNodes = mutableRingBuffer.toList()
        repeat(10) {
            for (node in initialNodes) {
                mutableRingBuffer.doMixing(node)
            }
        }
        return mutableRingBuffer[1_000].value + mutableRingBuffer[2_000].value + mutableRingBuffer[3_000].value
    }

    val testInput = readInput("Day20_test")
    check(part1(testInput) == 3L)
    check(part2(testInput) == 1623178306L)

    val input = readInput("Day20")
    println(part1(input))
    println(part2(input))
}
