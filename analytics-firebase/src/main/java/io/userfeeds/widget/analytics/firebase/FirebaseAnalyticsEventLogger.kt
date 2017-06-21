package io.userfeeds.widget.analytics.firebase

import android.content.Context
import android.os.Bundle
import com.google.firebase.analytics.FirebaseAnalytics
import io.userfeeds.widget.LinksViewPager
import kotlin.LazyThreadSafetyMode.NONE

class FirebaseAnalyticsEventLogger(context: Context) : LinksViewPager.EventListener {

    private val firebaseAnalytics by lazy(NONE) { FirebaseAnalytics.getInstance(context) }

    override fun linksLoadStart() = sendEvent("userfeeds_ads_load_start")
    override fun linksLoadSuccess() = sendEvent("userfeeds_ads_load_success")
    override fun linksLoadEmpty() = sendEvent("userfeeds_ads_load_empty")
    override fun linksLoadError() = sendEvent("userfeeds_ads_load_error")
    override fun linksLoadCancel() = sendEvent("userfeeds_ads_load_cancel")
    override fun linkDisplay(index: Int) = sendEvent("userfeeds_ad_display")
    override fun linkOpen(index: Int) = sendEvent("userfeeds_ad_target")
    override fun widgetOpen() = sendEvent("userfeeds_widget_details")

    override fun linkClick(index: Int) = sendEvent("userfeeds_ad_click")
    override fun linkLongClick(index: Int) = sendEvent("userfeeds_ad_long_click")
    override fun linksSwipe(index: Int) = sendEvent("userfeeds_ad_swipe")

    private fun sendEvent(action: String) {
        firebaseAnalytics.logEvent(action, Bundle())
    }
}
