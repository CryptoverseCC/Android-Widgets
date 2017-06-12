package io.userfeeds.ads.sdk

import android.app.Activity
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.ViewGroup
import android.widget.FrameLayout

class TestActivity : Activity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(FrameLayout(this).also {
//            val layoutParams = FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
//            layoutParams.gravity = Gravity.TOP
//            val adView1 = AdView(
//                    context = this,
//                    apiKey = "59049c8fdfed920001508e2a94bad07aa8f846674ae92e8765bd926c",
//                    shareContext = "ads",
//                    algorithm = "internal",
//                    debug = true)
//            it.addView(adView1, layoutParams)
            val layoutParams2 = FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
            layoutParams2.gravity = Gravity.BOTTOM
            val adView2 = AdView(
                    context = this,
                    apiKey = "59049c8fdfed920001508e2a94bad07aa8f846674ae92e8765bd926c",
                    shareContext = "ads",
                    algorithm = "internal",
                    flip = 3,
                    debug = true)
            adView2.addListener(object : AdViewEventListener {
                override fun adClick() = logE("adClick")
                override fun adLongClick() = logE("adLongClick")
                override fun adSwipe() = logE("adSwipe")

                override fun adsLoadStart() = logE("adsLoadStart")
                override fun adsLoadSuccess() = logE("adsLoadSuccess")
                override fun adsLoadError() = logE("adsLoadError")
                override fun adsLoadCancel() = logE("adsLoadCancel")
                override fun adDisplay() = logE("adDisplay")
                override fun adTarget() = logE("adTarget")
                override fun widgetDetails() = logE("widgetDetails")
            })
            it.addView(adView2, layoutParams2)
        })
    }

    private fun logE(message: String) {
        Log.e("TestActivity", message)
    }
}
