@file:Suppress("NOTHING_TO_INLINE", "unused")

package util

/**
 * Abuse the not operator fun for quick logging
 * If you are reading this, for the love of god, don't use this in production code
 * Example :
 *   !"Log this"
 */
operator fun String.not() = println(this)

inline fun <T> T.print(msg: String? = null): T = this.also { println("$msg$it") }

inline fun <T> T.print(block: StringBuilder.() -> Unit): T = this.also { buildString(block).also { print(it) } }