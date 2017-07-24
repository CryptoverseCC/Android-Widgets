package io.userfeeds.widget

import io.userfeeds.sdk.core.ranking.RankingItem
import java.math.BigDecimal
import java.math.RoundingMode.DOWN
import java.math.RoundingMode.HALF_UP

internal fun normalize(links: List<RankingItem>): List<RankingItem> {
    val scoreSum = links.fold(BigDecimal.ZERO) { acc, item -> acc + item.score }
    if (scoreSum == BigDecimal.ZERO) {
        return links
    }
    val probabilities = links.map { (it.score * BigDecimal("100")).divide(scoreSum, 6, HALF_UP) }
    val roundedDownProbabilities = probabilities.map { it.setScale(0, DOWN) }
    val roundedDownProbabilitiesSum = roundedDownProbabilities.fold(BigDecimal.ZERO) { acc, value -> acc + value }
    val roundedProbabilities = if (roundedDownProbabilitiesSum == BigDecimal("100")) {
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
    return links.zip(roundedProbabilities) { item, probability -> item.copy(score = probability) }
}
