package io.userfeeds.ads.sdk

import org.junit.Assert.assertEquals
import org.junit.Test
import java.math.BigDecimal
import java.math.RoundingMode.HALF_UP

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

    private fun List<String>.assertNormalizedTo(vararg expected: String) {
        assertEquals(expected.map(::ad), normalize(this.map(::ad)))
    }

    private fun normalize(ads: List<Ad>): List<Ad> {
        val sum = ads.fold(BigDecimal.ZERO) { acc, ad -> acc + ad.probability }
        return ads.map { it.copy(probability = (it.probability * BigDecimal("100") / sum).setScale(0, HALF_UP)) }
    }
}

private fun ad(score: String) = Ad("", BigDecimal(score), "")
