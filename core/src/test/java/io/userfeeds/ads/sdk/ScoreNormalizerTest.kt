package io.userfeeds.ads.sdk

import org.junit.Assert.assertEquals
import org.junit.Test
import java.math.BigDecimal

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

    private fun List<String>.assertNormalizedTo(vararg expected: String) {
        assertEquals(expected.map(::ad), normalize(this.map(::ad)))
    }

    private fun normalize(ads: List<Ad>): List<Ad> {
        return List(ads.size) { BigDecimal("100") / BigDecimal(ads.size) }
                .map { Ad("", it, "") }
    }
}

private fun ad(score: String) = Ad("", BigDecimal(score), "")
