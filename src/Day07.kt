private sealed interface SystemNode {
    val name: String
    val size: Int
}

private data class SystemDirectory(override val name: String, val children: List<SystemNode>) : SystemNode {
    override val size = children.sumOf { it.size }
}

private data class SystemFile(override val name: String, override val size: Int) : SystemNode

private sealed interface SystemCommand {
    data class SystemCommandCD(val target: String) : SystemCommand
    object SystemCommandLS : SystemCommand
}

fun main() {
    fun String.isSystemCommand() = startsWith('$')

    fun String.toSystemCommand(): SystemCommand {
        val parts = split(' ')
        return when(parts[1]) {
            "ls" -> SystemCommand.SystemCommandLS
            "cd" -> SystemCommand.SystemCommandCD(parts[2])
            else -> error("Couldn't parse '$this'")
        }
    }

    fun List<String>.toSystem(): SystemDirectory {
        // var currentDirectory = "/"
        val pathSegments = mutableListOf("")
        val childrenStack = ArrayDeque<MutableList<SystemNode>>()
        childrenStack.addLast(mutableListOf())

        drop(1).forEach {
            if (it.isSystemCommand()) {
                when (val cmd = it.toSystemCommand()) {
                    is SystemCommand.SystemCommandLS -> Unit
                    is SystemCommand.SystemCommandCD -> {
                        when (cmd.target) {
                            ".." -> {
                                val children = childrenStack.removeLast()
                                childrenStack.last().add(SystemDirectory(name = pathSegments.joinToString(separator = "/"), children = children))
                                pathSegments.removeLast()
                            }
                            else -> {
                                childrenStack.addLast(mutableListOf())
                                pathSegments += cmd.target
                            }
                        }
                    }
                }
            } else {
                when {
                    it.startsWith("dir") -> Unit // Parsed when we CD into and out of directory
                    else -> {
                        val (size, name) = it.split(' ')
                        childrenStack.last().add(SystemFile(name = name, size = size.toInt()))
                    }
                }
            }
        }

        while (pathSegments.size > 1) {
            val children = childrenStack.removeLast()
            childrenStack.last().add(SystemDirectory(name = pathSegments.joinToString(separator = "/"), children = children))
            pathSegments.removeLast()
        }

        return SystemDirectory(name = "/", children = childrenStack.single())
    }

    fun SystemDirectory.flatmap(): Sequence<SystemNode> = sequence {
        yield(this@flatmap)
        children.forEach {
            when(it) {
                is SystemFile -> yield(it)
                is SystemDirectory -> yieldAll(it.flatmap())
            }
        }
    }

    fun part1(input: List<String>): Int {
        return input
            .toSystem()
            .flatmap()
            .filterIsInstance<SystemDirectory>()
            .filter { it.size <= 100_000 }
            .sumOf { it.size }
    }

    fun part2(input: List<String>): Int {
        val diskSpace = 70_000_000
        val targetFreeSpace = 30_000_000
        val system = input.toSystem()
        val currentFreeSpace = diskSpace - system.size
        val toDelete = targetFreeSpace - currentFreeSpace
        return system
            .flatmap()
            .filterIsInstance<SystemDirectory>()
            .map { it.size }
            .sorted()
            .first { it >= toDelete }
    }

    val testInput = readInput("Day07_test")
    check(part1(testInput) == 95437)
    check(part2(testInput) == 24933642)

    val input = readInput("Day07")
    println(part1(input))
    println(part2(input))
}
