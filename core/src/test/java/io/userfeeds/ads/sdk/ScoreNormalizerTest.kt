package io.userfeeds.ads.sdk

import org.junit.Assert.assertEquals
import org.junit.Test
import java.math.BigDecimal
import java.math.BigDecimal.ONE
import java.math.BigDecimal.ZERO
import java.math.RoundingMode.DOWN

class ScoreNormalizerTest {

    @Test
    fun `Single element should be normalized to 100%`() {
        val input = listOf("4.2")
        input.assertNormalizedTo("100")
    }

    @Test
    fun `Same elements should be normalized to 50%`() {
        val input = listOf("1.0", "1")
        input.assertNormalizedTo("50", "50")
    }

    @Test
    fun `Should normalize to 67% and 33%`() {
        val input = listOf("2.0", "1")
        input.assertNormalizedTo("67", "33")
    }

    @Test
    fun `Should normalize to 34%, 33% and 33%`() {
        val input = listOf("6", "6", "6")
        input.assertNormalizedTo("34", "33", "33")
    }

    private fun List<String>.assertNormalizedTo(vararg expected: String) {
        assertEquals(expected.map(::ad), normalize(this.map(::ad)))
    }

    private fun normalize(ads: List<Ad>): List<Ad> {
        val sum = ads.fold(ZERO) { acc, ad -> acc + ad.score }
        val values = ads.map { it.score * BigDecimal("100") / sum }
        val roundedDownValues = values.map { it.setScale(0, DOWN) }
        val roundedDownSum = roundedDownValues.fold(ZERO) { acc, value -> acc + value }
        val someRoundedUp = if (roundedDownSum == BigDecimal("100")) {
            roundedDownValues
        } else {
            val rests = values.zip(roundedDownValues) { value, roundedDown -> value - roundedDown }
            var numberToRoundUp = (BigDecimal("100") - roundedDownSum).intValueExact()
            val minToRoundUp = rests.sorted()[numberToRoundUp - 1]
            roundedDownValues.zip(rests) { value, rest ->
                value + if (numberToRoundUp > 0 && rest >= minToRoundUp) {
                    numberToRoundUp--
                    ONE
                } else {
                    ZERO
                }
            }
        }
        return ads.zip(someRoundedUp) { ad, probability -> ad.copy(score = probability) }
    }
}

private fun ad(score: String) = Ad("", "", BigDecimal(score))
