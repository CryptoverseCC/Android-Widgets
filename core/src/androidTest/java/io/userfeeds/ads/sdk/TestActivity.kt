package io.userfeeds.ads.sdk

import android.app.Activity
import android.os.Bundle
import android.view.Gravity
import android.view.ViewGroup
import android.widget.FrameLayout
import io.userfeeds.sdk.core.UserfeedsSdk

class TestActivity : Activity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(FrameLayout(this).also {
            val layoutParams = FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
            layoutParams.gravity = Gravity.TOP
            val adView1 = AdView(this)
                    .apiKey("59049c8fdfed920001508e2a94bad07aa8f846674ae92e8765bd926c")
                    .context("ads")
                    .algorithm("internal")
                    .debug()
            it.addView(adView1, layoutParams)
            val layoutParams2 = FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
            layoutParams2.gravity = Gravity.BOTTOM
            val adView2 = AdView(this)
                    .apiKey("59049c8fdfed920001508e2a94bad07aa8f846674ae92e8765bd926c")
                    .context("ads")
                    .algorithm("internal")
                    .debug()
            it.addView(adView2, layoutParams2)
        })
    }
}
