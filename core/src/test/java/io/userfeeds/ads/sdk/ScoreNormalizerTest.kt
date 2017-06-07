package io.userfeeds.ads.sdk

import io.userfeeds.sdk.core.ranking.RankingItem
import org.junit.Assert.assertEquals
import org.junit.Test

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
}

private fun ad(score: String) = RankingItem("", score.toDouble(), "")
