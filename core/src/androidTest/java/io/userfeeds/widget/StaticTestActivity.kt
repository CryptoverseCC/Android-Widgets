package io.userfeeds.widget

import android.app.Activity
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import android.widget.FrameLayout
import io.userfeeds.sdk.core.UserfeedsSdk

class StaticTestActivity : Activity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        UserfeedsSdk.initialize("", debug = true)
        setContentView(FrameLayout(this).also {
            val layoutParams = FrameLayout.LayoutParams(MATCH_PARENT, WRAP_CONTENT).apply {
                gravity = Gravity.TOP
            }
            val adView1 = LinksViewPager(
                    context = this,
                    rankingContext = "rinkeby:0x0406735fc1a657398941a50a0602eddf9723a6c8",
                    algorithm = "links",
                    publisherNote = "All ads visible",
                    flip = 0,
                    debug = true)
            it.addView(adView1, layoutParams)
            val layoutParams2 = FrameLayout.LayoutParams(MATCH_PARENT, WRAP_CONTENT).apply {
                gravity = Gravity.BOTTOM
            }
            val adView2 = LinksViewPager(
                    context = this,
                    rankingContext = "rinkeby:0xcd73518680ab60ec2253841909d3448bc60f0665",
                    algorithm = "links",
                    whitelist = "rinkeby:0xcd73518680ab60ec2253841909d3448bc60f0665",
                    publisherNote = "Send email to maciej dot gorski at userfeeds dot io to have your link whitelisted",
                    debug = true)
            adView2.addListener(object : LinksViewPager.EventListener {
                override fun linkClick(index: Int) = logE("linkClick $index")
                override fun linkLongClick(index: Int) = logE("linkLongClick $index")
                override fun linksSwipe(index: Int) = logE("linksSwipe $index")

                override fun linksLoadStart() = logE("linksLoadStart")
                override fun linksLoadSuccess() = logE("linksLoadSuccess")
                override fun linksLoadEmpty() = logE("linksLoadEmpty")
                override fun linksLoadError() = logE("linksLoadError")
                override fun linksLoadCancel() = logE("linksLoadCancel")
                override fun linkDisplay(index: Int) = logE("linkDisplay $index")
                override fun linkOpen(index: Int) = logE("linkOpen $index")
                override fun widgetOpen() = logE("widgetOpen")
            })
            it.addView(adView2, layoutParams2)
        })
    }

    private fun logE(message: String) {
        Log.e("StaticTestActivity", message)
    }
}
