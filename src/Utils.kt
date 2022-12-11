import java.io.File
import java.math.BigInteger
import java.security.MessageDigest

/**
 * Reads lines from the given input txt file.
 */
fun readInput(name: String) = File("src", "$name.txt")
    .readLines()

/**
 * Reads whole text from the given input txt file.
 */
fun readTextInput(name: String) = File("src", "$name.txt")
    .readText()

/**
 * Converts string to md5 hash.
 */
fun String.md5() = BigInteger(1, MessageDigest.getInstance("MD5").digest(toByteArray()))
    .toString(16)
    .padStart(32, '0')

/**
 * Calculate product by multiplying all elements together
 */
fun Iterable<Int>.product() = reduce(Int::times)

/**
 * Calculate product by multiplying all elements together
 */
fun Iterable<Long>.product() = reduce(Long::times)
