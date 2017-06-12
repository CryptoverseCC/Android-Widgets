package io.userfeeds.ads.sdk.analytics

import android.content.Context
import android.os.Bundle
import android.support.annotation.XmlRes
import com.google.android.gms.analytics.GoogleAnalytics
import com.google.android.gms.analytics.HitBuilders
import io.userfeeds.ads.sdk.AdViewEventListener
import kotlin.LazyThreadSafetyMode.NONE

class GoogleAnalyticsEventLogger(context: Context, @XmlRes trackerXml: Int) : AdViewEventListener {

    private val googleAnalytics by lazy(NONE) { GoogleAnalytics.getInstance(context) }
    private val tracker by lazy(NONE) { googleAnalytics.newTracker(trackerXml) }

    override fun adsLoadStart() = sendEvent("userfeeds_ads_load_start")
    override fun adsLoadSuccess() = sendEvent("userfeeds_ads_load_success")
    override fun adsLoadError() = sendEvent("userfeeds_ads_load_error")
    override fun adsLoadCancel() = sendEvent("userfeeds_ads_load_cancel")
    override fun adDisplay() = sendEvent("userfeeds_ad_display")
    override fun adTarget() = sendEvent("userfeeds_ad_target")
    override fun widgetDetails() = sendEvent("userfeeds_widget_details")

    override fun adClick() = sendEvent("userfeeds_ad_click")
    override fun adLongClick() = sendEvent("userfeeds_ad_long_click")
    override fun adSwipe() = sendEvent("userfeeds_ad_swipe")

    private fun sendEvent(action: String) {
        tracker.send(HitBuilders.EventBuilder()
                .setCategory("userfeeds_ads")
                .setAction(action)
                .build())
    }
}
