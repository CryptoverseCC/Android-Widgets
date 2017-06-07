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

    private fun List<String>.assertNormalizedTo(vararg expected: String) {
        assertEquals(expected.map(::ad), normalize(this.map(::ad)))
    }

    private fun normalize(ads: List<Ad>): List<Ad> {
        return listOf(Ad("", BigDecimal("100"), ""))
    }
}

private fun ad(score: String) = Ad("", BigDecimal(score), "")
