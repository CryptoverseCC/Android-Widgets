package io.userfeeds.ads.sdk

import java.math.BigDecimal

internal data class Ad(val title: String, val probability: BigDecimal, val url: String) : Weighted {

    override val weight = probability
}
