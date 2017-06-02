package io.userfeeds.ads.sdk

import org.junit.Assert.assertEquals
import org.junit.Test
import java.lang.Double.doubleToLongBits
import java.lang.Double.longBitsToDouble
import java.math.BigDecimal
import java.util.*

class WeightedTest {

    @Test
    fun `Should return the only item`() {
        val element = "0.3".w
        val list = listOf(element)
        assertSelectedElement(0, list)
    }

    @Test
    fun `Should return second item`() {
        val element = "0.2".w
        val list = listOf("0.1".w, element)
        assertSelectedElement(1, list)
    }

    @Test
    fun `Should return third element when equal weights`() {
        val element = "1.0".w
        val list = listOf("4.0".w, "2.0".w, element, "5.0".w)
        assertSelectedElement(2, list)
    }

    @Test
    fun `Should return first item when on the edge`() {
        val element = "1.0".w
        val otherElement = WeightedImpl(BigDecimal(longBitsToDouble(doubleToLongBits(1.0) - 1L)))
        val list = listOf(element, otherElement)
        assertSelectedElement(0, list)
    }

    private fun assertSelectedElement(index: Int, list: List<Weighted>) {
        assertEquals(index, list.randomIndex(randomStub))
    }

    private val randomStub = object : Random() {

        override fun nextDouble() = 0.5
    }

    private class WeightedImpl(override val weight: BigDecimal) : Weighted

    private val String.w get() = WeightedImpl(BigDecimal(this))
}
