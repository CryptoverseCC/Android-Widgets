package io.userfeeds.ads.sdk

import java.math.BigDecimal

internal data class Ad(val target: String, val title: String, val score: BigDecimal) : Weighted {

    override val weight = score
}
