package io.userfeeds.ads.sdk

import android.app.Activity
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import android.widget.FrameLayout
import io.userfeeds.sdk.core.UserfeedsSdk

class TestActivity : Activity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        UserfeedsSdk.initialize(
                apiKey = "59049c8fdfed920001508e2a94bad07aa8f846674ae92e8765bd926c",
                debug = true)
        setContentView(FrameLayout(this).also {
            val layoutParams = FrameLayout.LayoutParams(MATCH_PARENT, WRAP_CONTENT).apply {
                gravity = Gravity.TOP
            }
            val adView1 = AdView(
                    context = this,
                    shareContext = "ads",
                    algorithm = "internal",
                    flip = 0,
                    debug = true)
            it.addView(adView1, layoutParams)
            val layoutParams2 = FrameLayout.LayoutParams(MATCH_PARENT, WRAP_CONTENT).apply {
                gravity = Gravity.BOTTOM
            }
            val adView2 = AdView(
                    context = this,
                    shareContext = "ads",
                    algorithm = "internal",
                    debug = true)
            adView2.addListener(object : AdViewEventListener {
                override fun adClick(index: Int) = logE("adClick $index")
                override fun adLongClick(index: Int) = logE("adLongClick $index")
                override fun adSwipe(index: Int) = logE("adSwipe $index")

                override fun adsLoadStart() = logE("adsLoadStart")
                override fun adsLoadSuccess() = logE("adsLoadSuccess")
                override fun adsLoadEmpty() = logE("adsLoadEmpty")
                override fun adsLoadError() = logE("adsLoadError")
                override fun adsLoadCancel() = logE("adsLoadCancel")
                override fun adDisplay(index: Int) = logE("adDisplay $index")
                override fun adTarget(index: Int) = logE("adTarget $index")
                override fun widgetDetails() = logE("widgetDetails")
            })
            it.addView(adView2, layoutParams2)
        })
    }

    private fun logE(message: String) {
        Log.e("TestActivity", message)
    }
}
