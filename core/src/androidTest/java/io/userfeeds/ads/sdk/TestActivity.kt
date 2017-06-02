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
        UserfeedsSdk.initialize(
                apiKey = "59049c8fdfed920001508e2a94bad07aa8f846674ae92e8765bd926c",
                debug = BuildConfig.DEBUG
        )
        setContentView(FrameLayout(this).also {
            val layoutParams = FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
            layoutParams.gravity = Gravity.TOP
            it.addView(AdView(this), layoutParams)
            val layoutParams2 = FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
            layoutParams2.gravity = Gravity.BOTTOM
            it.addView(AdView(this), layoutParams2)
        })
    }
}
