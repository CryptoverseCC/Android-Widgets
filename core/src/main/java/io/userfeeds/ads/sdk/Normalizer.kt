package io.userfeeds.ads.sdk

import io.userfeeds.sdk.core.ranking.RankingItem
import java.math.BigDecimal
import java.math.RoundingMode

internal fun normalize(ads: List<RankingItem>): List<RankingItem> {
    val scoreSum = ads.fold(BigDecimal.ZERO) { acc, ad -> acc + ad.score }
    val probabilities = ads.map { it.score * BigDecimal("100") / scoreSum }
    val roundedDownProbabilities = probabilities.map { it.setScale(0, RoundingMode.DOWN) }
    val roundedDownProbabilitiesSum = roundedDownProbabilities.fold(BigDecimal.ZERO) { acc, value -> acc + value }
    val roundedUpProbabilities = if (roundedDownProbabilitiesSum == BigDecimal("100")) {
        roundedDownProbabilities
    } else {
        val reminders = probabilities.zip(roundedDownProbabilities) { value, roundedDown -> value - roundedDown }
        var numberToRoundUp = (BigDecimal("100") - roundedDownProbabilitiesSum).intValueExact()
        val minReminderToRoundUp = reminders.sortedDescending()[numberToRoundUp - 1]
        roundedDownProbabilities.zip(reminders) { value, reminder ->
            value + if (numberToRoundUp > 0 && reminder >= minReminderToRoundUp) {
                numberToRoundUp--
                BigDecimal.ONE
            } else {
                BigDecimal.ZERO
            }
        }
    }
    return ads.zip(roundedUpProbabilities) { ad, probability -> ad.copy(score = probability) }
}
