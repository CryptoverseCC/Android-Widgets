package io.userfeeds.widget

import io.userfeeds.sdk.core.ranking.RankingItem
import java.math.BigDecimal
import java.util.*

internal fun List<RankingItem>.randomIndex(random: Random): Int {
    val sum = fold(BigDecimal.ZERO) { acc, elem -> acc + elem.score }
    if (sum == BigDecimal.ZERO) {
        return random.nextInt(size)
    }
    var value = BigDecimal(random.nextDouble()) * sum
    forEachIndexed { index, elem ->
        value -= elem.score
        if (value < BigDecimal.ZERO) {
            return index
        }
    }
    throw IllegalStateException()
}
