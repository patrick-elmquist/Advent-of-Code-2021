/**
 * Abuse the not operator fun for quick logging
 * If you are reading this, for the love of god, don't use this in production code
 * Example :
 *   !"Log this"
 */
operator fun String.not() = println(this)
