package io.userfeeds.ads.sdk

import java.math.BigDecimal
import java.util.*

interface Weighted {

    val weight: BigDecimal
}

internal fun <T : Weighted> List<T>.randomIndex(random: Random): Int {
    var sum = BigDecimal.ZERO
    for (elem in this) {
        sum += elem.weight
    }
    var value = BigDecimal(random.nextDouble()) * sum
    forEachIndexed { index, elem ->
        value -= elem.weight
        if (value < BigDecimal.ZERO) {
            return index
        }
    }
    throw IllegalStateException()
}
