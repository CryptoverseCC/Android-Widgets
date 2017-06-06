package io.userfeeds.ads.sdk

import java.math.BigDecimal
import java.util.*

internal interface Weighted {

    val weight: BigDecimal
}

internal fun <T : Weighted> List<T>.randomIndex(random: Random): Int {
    val sum = fold(BigDecimal.ZERO) { acc, elem -> acc + elem.weight }
    var value = BigDecimal(random.nextDouble()) * sum
    forEachIndexed { index, elem ->
        value -= elem.weight
        if (value < BigDecimal.ZERO) {
            return index
        }
    }
    throw IllegalStateException()
}
