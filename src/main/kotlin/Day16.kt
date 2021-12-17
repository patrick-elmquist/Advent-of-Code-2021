
import Packet.LiteralValue
import Packet.Operator
import util.Input
import util.day

// answer #1: 917
// answer #2: 2536453523344

private const val HEADER_LEN = 6
private const val MODE_LEN = 1
private const val LITERAL_VALUE_PACKET = 4

fun main() {
    day(n = 16) {
        part1(expected = 917) { input ->
            input.toBinary().parsePackets().first.sumVersions()
        }

        part2(expected = 2536453523344L) { input ->
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

private fun String.parsePackets(): Pair<Packet, String> {
    val version = substring(0, 3).toInt(2)
    val typeId = substring(3, 6).toInt(2)
    val (result, consumed) = when (typeId) {
        LITERAL_VALUE_PACKET -> {
            val data = drop(HEADER_LEN)
            val groups = data.groups()
            val consumed = groups.sumOf { it.length }
            LiteralValue(
                typeId = typeId,
                version = version,
                value = groups.joinToString("") { it.drop(1) }.toLong(2)
            ) to HEADER_LEN + consumed
        }

        else -> {
            val data = drop(HEADER_LEN + MODE_LEN)
            when (val lenId = drop(HEADER_LEN).take(MODE_LEN).toInt(2)) {
                0 -> {
                    val (packets, consumed) = data.parseLenPackets()
                    Operator(
                        typeId = typeId,
                        version = version,
                        lenId = lenId,
                        packets = packets
                    ) to HEADER_LEN + MODE_LEN + consumed
                }
                else -> {
                    val (packets, consumed) = data.parseNbrOfPackets()
                    Operator(
                        typeId = typeId,
                        version = version,
                        lenId = lenId,
                        packets = packets
                    ) to HEADER_LEN + MODE_LEN + consumed
                }
            }
        }
    }
    return result to drop(consumed)
}

private fun String.parseLenPackets(): Pair<List<Packet>, Int> {
    val offset = 15
    val desiredLength = take(offset).toInt(2)
    var consumed = 0
    var remaining = drop(offset)
    return buildList {
        while (consumed < desiredLength) {
            val (packet, unused) = remaining.parsePackets()
            add(packet)
            consumed += (remaining.length - unused.length)
            remaining = unused
        }
    } to offset + consumed
}

private fun String.parseNbrOfPackets(): Pair<List<Packet>, Int> {
    val offset = 11
    val desiredNumber = take(offset).toInt(2)
    var consumed = 0
    var remaining = drop(offset)
    return buildList {
        repeat(desiredNumber) {
            val (packet, unused) = remaining.parsePackets()
            add(packet)
            consumed += (remaining.length - unused.length)
            remaining = unused
        }
    } to offset + consumed
}

private fun String.groups(): List<String> {
    val chunkSize = 5
    var done = false
    return chunkedSequence(chunkSize).takeWhile {
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
