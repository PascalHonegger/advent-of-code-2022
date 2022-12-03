enum class Hand(val score: Int) {
    Rock(score = 1),
    Paper(score = 2),
    Scissor(score = 3),
}

enum class RoundResult(val score: Int) {
    Win(score = 6),
    Draw(score = 3),
    Lose(score = 0),
}

fun main() {
    fun String.toHand() = when(this) {
        "A" -> Hand.Rock
        "B"-> Hand.Paper
        "C" -> Hand.Scissor
        "X" -> Hand.Rock
        "Y"-> Hand.Paper
        "Z" -> Hand.Scissor
        else -> error("Failed to parse '$this'")
    }

    fun String.toRoundResult() = when(this) {
        "X" -> RoundResult.Lose
        "Y"-> RoundResult.Draw
        "Z" -> RoundResult.Win
        else -> error("Failed to parse '$this'")
    }

    infix fun Hand.beats(other: Hand) =
        this == Hand.Scissor && other == Hand.Paper ||
                this == Hand.Rock && other == Hand.Scissor ||
                this == Hand.Paper && other == Hand.Rock

    fun part1(input: List<String>): Int {
        return input.sumOf {  round ->
            val (opponent, recommendation) = round.split(' ').map { it.toHand() }

            val result = when {
                recommendation == opponent -> RoundResult.Draw
                recommendation beats opponent -> RoundResult.Win
                else -> RoundResult.Lose
            }

            result.score + recommendation.score
        }
    }

    fun part2(input: List<String>): Int {
        return input.sumOf {  round ->
            val (hint1, hint2) = round.split(' ')
            val opponent = hint1.toHand()
            val result = hint2.toRoundResult()

            val recommendation = when(result) {
                RoundResult.Draw -> opponent
                RoundResult.Win -> Hand.values().first { it beats opponent }
                RoundResult.Lose -> Hand.values().first { opponent beats it }
            }

            result.score + recommendation.score
        }
    }

    val testInput = readInput("Day02_test")
    check(part1(testInput) == 15)
    check(part2(testInput) == 12)

    val input = readInput("Day02")
    println(part1(input))
    println(part2(input))
}
