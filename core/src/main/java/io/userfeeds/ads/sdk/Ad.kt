package io.userfeeds.ads.sdk

import java.math.BigDecimal

internal class Ad(val title: String, val probability: BigDecimal, val url: String) : Weighted {

    override val weight = probability
}
