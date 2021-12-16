
import Packet.LiteralValue
import Packet.Operator
import util.Input
import util.day

// answer #1: 917
// answer #2: 2536453523344

fun main() {
    day(n = 16) {
        solution(expected = 917) { input ->
            input.toBinary().parsePackets().first.sumVersions()
        }

        solution(expected = 2536453523344L) { input ->
            input.toBinary().parsePackets().first.resolve()
        }
    }
}

private fun Input.toBinary() =
    lines.first().map {
        val n = Integer.toBinaryString(it.digitToInt(16))
        "%4s".format(n).replace(' ', '0')
    }.joinToString("")

private fun Packet.sumVersions(): Int =
    when (this) {
        is LiteralValue -> version
        is Operator -> version + packets.sumOf { it.sumVersions() }
    }

private const val HEADER_LEN = 6

private fun String.parsePackets(): Pair<Packet, String> {
    val version = substring(0, 3).toInt(2)
    val typeId = substring(3, 6).toInt(2)
    val data = drop(HEADER_LEN)
    val (result, consumed) = when (typeId) {
        4 -> {
            val groups = data.groups()
            val consumed = groups.sumOf { it.length }
            LiteralValue(
                typeId = typeId,
                version = version,
                value = groups.joinToString("") { it.drop(1) }.toLong(2)
            ) to HEADER_LEN + consumed
        }

        else -> {
            val rest = data.drop(1)
            when (val lenId = data.take(1).toInt(2)) {
                0 -> {
                    val (packets, consumed) = rest.parseLenPackets()
                    Operator(
                        typeId = typeId,
                        version = version,
                        lenId = lenId,
                        packets = packets
                    ) to HEADER_LEN + 1 + 15 + consumed
                }
                else -> {
                    val (packets, consumed) = rest.parseNbrOfPackets()
                    Operator(
                        typeId = typeId,
                        version = version,
                        lenId = lenId,
                        packets = packets
                    ) to HEADER_LEN + 1 + 11 + consumed
                }
            }
        }
    }
    return result to drop(consumed)
}

private fun String.parseLenPackets(): Pair<List<Packet>, Int> {
    val len = take(15).toInt(2)
    return buildList {
        var left = this@parseLenPackets.drop(15)
        var consumed = 0
        while (consumed < len) {
            val (packet, remaining) = left.parsePackets()
            add(packet)
            consumed += (left.length - remaining.length)
            left = remaining
        }
    } to len
}

private fun String.parseNbrOfPackets(): Pair<List<Packet>, Int> {
    val nbr = take(11).toInt(2)
    var consumed = 0
    return buildList {
        var left = this@parseNbrOfPackets.drop(11)
        var count = 0
        while (count < nbr) {
            val (packet, remaining) = left.parsePackets()
            add(packet)
            consumed += (left.length - remaining.length)
            left = remaining
            count++
        }
    } to consumed
}

private fun String.groups(): List<String> {
    var done = false
    return chunkedSequence(5).takeWhile {
        when {
            done -> false
            else -> {
                done = it.startsWith("0")
                true
            }
        }
    }.toList()
}

private sealed class Packet {
    abstract fun resolve(): Long

    data class LiteralValue(
        val typeId: Int,
        val version: Int,
        val value: Long
    ) : Packet() {
        override fun resolve(): Long = value
    }

    data class Operator(
        val typeId: Int,
        val version: Int,
        val lenId: Int,
        val packets: List<Packet>
    ) : Packet() {
        override fun resolve(): Long =
            with(packets.map { it.resolve() }) {
                when (typeId) {
                    0 -> sumOf { it }
                    1 -> reduce { a, b -> a * b }
                    2 -> minOf { it }
                    3 -> maxOf { it }
                    5 -> let { (a, b) -> if (a > b) 1 else 0 }
                    6 -> let { (a, b) -> if (a < b) 1 else 0 }
                    7 -> let { (a, b) -> if (a == b) 1 else 0 }
                    else -> error("Should not be here: $typeId")
                }
            }
    }
}
