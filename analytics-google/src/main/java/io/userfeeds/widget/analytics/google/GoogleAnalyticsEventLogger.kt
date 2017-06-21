package io.userfeeds.widget.analytics.google

import android.content.Context
import android.support.annotation.XmlRes
import com.google.android.gms.analytics.GoogleAnalytics
import com.google.android.gms.analytics.HitBuilders
import io.userfeeds.widget.LinksViewPager
import kotlin.LazyThreadSafetyMode.NONE

class GoogleAnalyticsEventLogger(context: Context, @XmlRes trackerXml: Int) : LinksViewPager.EventListener {

    private val googleAnalytics by lazy(NONE) { GoogleAnalytics.getInstance(context) }
    private val tracker by lazy(NONE) { googleAnalytics.newTracker(trackerXml) }

    override fun linksLoadStart() = sendEvent("userfeeds_ads_load_start")
    override fun linksLoadSuccess() = sendEvent("userfeeds_ads_load_success")
    override fun linksLoadError() = sendEvent("userfeeds_ads_load_error")
    override fun linksLoadCancel() = sendEvent("userfeeds_ads_load_cancel")
    override fun linkDisplay(index: Int) = sendEvent("userfeeds_ad_display")
    override fun linkOpen(index: Int) = sendEvent("userfeeds_ad_target")
    override fun widgetOpen() = sendEvent("userfeeds_widget_details")

    override fun linkClick(index: Int) = sendEvent("userfeeds_ad_click")
    override fun linkLongClick(index: Int) = sendEvent("userfeeds_ad_long_click")
    override fun linksSwipe(index: Int) = sendEvent("userfeeds_ad_swipe")

    private fun sendEvent(action: String) {
        tracker.send(HitBuilders.EventBuilder()
                .setCategory("userfeeds_ads")
                .setAction(action)
                .build())
    }
}
