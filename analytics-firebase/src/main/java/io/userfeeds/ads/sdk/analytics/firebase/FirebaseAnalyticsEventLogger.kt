package io.userfeeds.ads.sdk.analytics.firebase

import android.content.Context
import android.os.Bundle
import com.google.firebase.analytics.FirebaseAnalytics
import io.userfeeds.ads.sdk.AdViewEventListener
import kotlin.LazyThreadSafetyMode.NONE

class FirebaseAnalyticsEventLogger(context: Context) : AdViewEventListener {

    private val firebaseAnalytics by lazy(NONE) { FirebaseAnalytics.getInstance(context) }

    override fun adsLoadStart() = sendEvent("userfeeds_ads_load_start")
    override fun adsLoadSuccess() = sendEvent("userfeeds_ads_load_success")
    override fun adsLoadError() = sendEvent("userfeeds_ads_load_error")
    override fun adsLoadCancel() = sendEvent("userfeeds_ads_load_cancel")
    override fun adDisplay(index: Int) = sendEvent("userfeeds_ad_display")
    override fun adTarget(index: Int) = sendEvent("userfeeds_ad_target")
    override fun widgetDetails() = sendEvent("userfeeds_widget_details")

    override fun adClick(index: Int) = sendEvent("userfeeds_ad_click")
    override fun adLongClick(index: Int) = sendEvent("userfeeds_ad_long_click")
    override fun adSwipe(index: Int) = sendEvent("userfeeds_ad_swipe")

    private fun sendEvent(action: String) {
        firebaseAnalytics.logEvent(action, Bundle())
    }
}
