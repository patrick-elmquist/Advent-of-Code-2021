import common.day

fun main() {
    day(n = 0, inParallel = true) {
        solution { input ->
            val ints = input.ints
            ints.forEach { n1 ->
                ints.forEach { n2 -> if (n1 + n2 == 2020) return@solution n1 * n2 }
            }
        }

        solution { input ->
            val ints = input.ints
            ints.forEach { n1 ->
                ints.forEach { n2 ->
                    ints.forEach { n3 -> if (n1 + n2 + n3 == 2020) return@solution n1 * n2 * n3 }
                }
            }
        }
    }
}

