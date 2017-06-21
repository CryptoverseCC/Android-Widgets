package io.userfeeds.widget

import io.userfeeds.sdk.core.ranking.RankingItem
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

    @Test
    fun `Should normalize to 89% and 11%`() {
        val input = listOf("89.4", "10.6")
        input.assertNormalizedTo("89", "11")
    }

    @Test
    fun `Should add to ones with smaller reminder if they have bigger value`() {
        val input = listOf("456", "456", "88")
        input.assertNormalizedTo("46", "46", "8")
    }

    private fun List<String>.assertNormalizedTo(vararg expected: String) {
        assertEquals(expected.map(::ad), io.userfeeds.widget.normalize(this.map(::ad)))
    }
}

private fun ad(score: String) = RankingItem("", BigDecimal(score), null, null)
