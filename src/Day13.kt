import kotlin.math.max

private sealed interface Packet : Comparable<Packet>
private data class ListPacket(val children: List<Packet>) : Packet {
    override fun toString() = "[${children.joinToString()}]"
    override fun compareTo(other: Packet): Int {
        return when (other) {
            is ListPacket -> {
                for (i in 0 until (max(children.size, other.children.size))) {
                    val left = children.getOrNull(i) ?: return -1
                    val right = other.children.getOrNull(i) ?: return 1
                    left.compareTo(right).let {
                        if (it != 0) return it
                    }
                }
                return 0
            }

            is ItemPacket -> compareTo(ListPacket(listOf(other)))
        }
    }
}

private data class ItemPacket(val value: Int) : Packet {
    override fun toString() = value.toString()
    override fun compareTo(other: Packet): Int {
        return when (other) {
            is ListPacket -> ListPacket(listOf(this)).compareTo(other)
            is ItemPacket -> value.compareTo(other.value)
        }
    }
}

private class PacketParser(val input: String) {
    private var idx = 0

    private fun consume() = input[idx++]
    private fun peek() = input[idx]

    private fun consumeNumber(): ItemPacket {
        val number = buildString {
            while (peek().isDigit()) {
                append(consume())
            }
        }.toInt()
        return ItemPacket(number)
    }

    private fun consumePacket(): Packet {
        val next = peek()
        return when {
            next.isDigit() -> consumeNumber()
            else -> consumeListPacket()
        }
    }

    private fun consumeList(): List<Packet> = buildList {
        add(consumePacket())
        while (peek() == ',') {
            consume()
            add(consumePacket())
        }
    }

    private fun consumeListPacket(): ListPacket {
        check(consume() == '[')
        val content = if (peek() == ']') emptyList() else consumeList()
        check(consume() == ']')
        return ListPacket(content)
    }

    val rootPacket = consumeListPacket()
}

fun main() {
    fun String.toListPacket() = PacketParser(this).rootPacket

    fun part1(input: List<String>): Int {
        return input
            .asSequence()
            .chunked(3)
            .map { (first, second, _) -> first.toListPacket() to second.toListPacket() }
            .withIndex()
            .filter { it.value.first <= it.value.second }
            .sumOf { it.index + 1}
    }

    fun part2(input: List<String>): Int {
        val dividerPackets = listOf(
            ListPacket(listOf(ListPacket(listOf(ItemPacket(2))))),
            ListPacket(listOf(ListPacket(listOf(ItemPacket(6))))),
        )
        return input
            .filter { it.isNotEmpty() }
            .map { it.toListPacket() }
            .let { it + dividerPackets }
            .sorted()
            .withIndex()
            .filter { it.value in dividerPackets }
            .map { it.index + 1 }
            .product()
    }

    val testInput = readInput("Day13_test")
    check(part1(testInput) == 13)
    check(part2(testInput) == 140)

    val input = readInput("Day13")
    println(part1(input))
    println(part2(input))
}
