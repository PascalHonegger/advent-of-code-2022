private sealed interface MonkeyJob {
    val name: String
}

private data class OperationMonkeyJob(
    override val name: String,
    val left: String,
    val operation: Char,
    val right: String
) : MonkeyJob {

    override fun toString() = "$name: $left $operation $right"
}

private data class ValueMonkeyJob(
    override val name: String,
    val value: Long
) : MonkeyJob {
    override fun toString() = "$name: $value"
}

fun main() {
    fun String.toMonkeyJob(): MonkeyJob {
        val split = split(' ')
        val name = split[0].removeSuffix(":")
        return split[1].toLongOrNull()?.let { ValueMonkeyJob(name, it) } ?: OperationMonkeyJob(
            name = name,
            left = split[1],
            operation = split[2].single(),
            right = split[3]
        )
    }
    fun List<String>.toMonkeyJobs() = map { it.toMonkeyJob() }.associateBy { it.name }.toMutableMap()

    fun MutableMap<String, MonkeyJob>.reduce() {
        var didReduce = true
        while (didReduce) {
            didReduce = false
            values.filterIsInstance<OperationMonkeyJob>().forEach {
                val left = get(it.left)
                val right = get(it.right)
                if (left is ValueMonkeyJob && right is ValueMonkeyJob) {
                    val value = when (it.operation) {
                        '+' -> left.value + right.value
                        '-' -> left.value - right.value
                        '*' -> left.value * right.value
                        '/' -> left.value / right.value
                        else -> error("Unkown operation: ${it.operation}")
                    }
                    replace(it.name, ValueMonkeyJob(it.name, value))
                    didReduce = true
                }
            }
        }
    }

    fun part1(input: List<String>): Long {
        val jobs = input.toMonkeyJobs()
        jobs.reduce()
        val root = jobs["root"] as ValueMonkeyJob
        return root.value
    }

    fun part2(input: List<String>) {
        val jobs = input.toMonkeyJobs()
        jobs.remove("humn") as ValueMonkeyJob
        jobs.reduce()
        val root = jobs["root"] as OperationMonkeyJob
        val left = jobs[root.left]
        val right = jobs[root.right]

        val open = left as? OperationMonkeyJob ?: right as OperationMonkeyJob
        val solved = left as? ValueMonkeyJob ?: right as ValueMonkeyJob

        val equation = buildString {
            append(solved.value)
            append("=")

            fun traverse(job: OperationMonkeyJob) {
                append('(')
                when (val l = jobs[job.left]) {
                    is ValueMonkeyJob -> append(l.value)
                    is OperationMonkeyJob -> traverse(l)
                    else -> append("x")
                }
                append(job.operation)
                when (val r = jobs[job.right]) {
                    is ValueMonkeyJob -> append(r.value)
                    is OperationMonkeyJob -> traverse(r)
                    else -> append("x")
                }
                append(')')
            }

            traverse(open)
        }

        println(equation)
    }

    val testInput = readInput("Day21_test")
    check(part1(testInput) == 152L)
    part2(testInput)

    val input = readInput("Day21")
    println(part1(input))
    part2(input)
}
