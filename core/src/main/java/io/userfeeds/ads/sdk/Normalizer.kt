package io.userfeeds.ads.sdk

import io.userfeeds.sdk.core.ranking.RankingItem
import java.math.BigDecimal
import java.math.RoundingMode

internal fun normalize(ads: List<RankingItem>): List<RankingItem> {
    val sum = ads.fold(BigDecimal.ZERO) { acc, ad -> acc + BigDecimal(ad.score) }
    val values = ads.map { BigDecimal(it.score) * BigDecimal("100") / sum }
    val roundedDownValues = values.map { it.setScale(0, RoundingMode.DOWN) }
    val roundedDownSum = roundedDownValues.fold(BigDecimal.ZERO) { acc, value -> acc + value }
    val someRoundedUp = if (roundedDownSum == BigDecimal("100")) {
        roundedDownValues
    } else {
        val rests = values.zip(roundedDownValues) { value, roundedDown -> value - roundedDown }
        var numberToRoundUp = (BigDecimal("100") - roundedDownSum).intValueExact()
        val minToRoundUp = rests.sorted()[numberToRoundUp - 1]
        roundedDownValues.zip(rests) { value, rest ->
            value + if (numberToRoundUp > 0 && rest >= minToRoundUp) {
                numberToRoundUp--
                BigDecimal.ONE
            } else {
                BigDecimal.ZERO
            }
        }
    }
    return ads.zip(someRoundedUp) { ad, probability -> ad.copy(score = probability.toDouble()) }
}
