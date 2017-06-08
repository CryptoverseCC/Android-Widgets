package io.userfeeds.ads.sdk

import io.userfeeds.sdk.core.ranking.RankingItem
import java.math.BigDecimal
import java.util.*

internal fun List<RankingItem>.randomIndex(random: Random): Int {
    val sum = fold(BigDecimal.ZERO) { acc, elem -> acc + BigDecimal(elem.score) }
    var value = BigDecimal(random.nextDouble()) * sum
    forEachIndexed { index, elem ->
        value -= BigDecimal(elem.score)
        if (value < BigDecimal.ZERO) {
            return index
        }
    }
    throw IllegalStateException()
}
