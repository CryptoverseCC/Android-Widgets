package io.userfeeds.ads.sdk.analytics

import android.content.Context
import android.os.Bundle
import com.google.firebase.analytics.FirebaseAnalytics
import io.userfeeds.ads.sdk.AdViewEventListener

class FirebaseAnalyticsEventLogger(context: Context) : AdViewEventListener() {

    private val firebaseAnalytics = FirebaseAnalytics.getInstance(context)

    override fun adsLoadStart() {
        firebaseAnalytics.logEvent("userfeeds_ads_load_start", Bundle())
    }

    override fun adsLoadSuccess() {
        firebaseAnalytics.logEvent("userfeeds_ads_load_success", Bundle())
    }

    override fun adsLoadError() {
        firebaseAnalytics.logEvent("userfeeds_ads_load_error", Bundle())
    }

    override fun adsLoadCancel() {
        firebaseAnalytics.logEvent("userfeeds_ads_load_cancel", Bundle())
    }

    override fun adDisplay() {
        firebaseAnalytics.logEvent("userfeeds_ad_display", Bundle())
    }

    override fun adTarget() {
        firebaseAnalytics.logEvent("userfeeds_ad_target", Bundle())
    }

    override fun widgetDetails() {
        firebaseAnalytics.logEvent("userfeeds_widget_details", Bundle())
    }

    override fun adClick() {
        firebaseAnalytics.logEvent("userfeeds_ad_click", Bundle())
    }

    override fun adLongClick() {
        firebaseAnalytics.logEvent("userfeeds_ad_long_click", Bundle())
    }

    override fun adSwipe() {
        firebaseAnalytics.logEvent("userfeeds_ad_swipe", Bundle())
    }
}
