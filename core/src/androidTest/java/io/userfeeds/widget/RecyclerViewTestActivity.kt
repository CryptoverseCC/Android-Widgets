package io.userfeeds.widget

import android.app.Activity
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import android.widget.FrameLayout
import io.userfeeds.sdk.core.UserfeedsSdk

class RecyclerViewTestActivity : Activity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        UserfeedsSdk.initialize(
                apiKey = "59049c8fdfed920001508e2a94bad07aa8f846674ae92e8765bd926c",
                debug = true)
        val linksRecyclerView = LinksRecyclerView(
                context = this,
                shareContext = "rinkeby:0x0406735fc1a657398941a50a0602eddf9723a6c8",
                algorithm = "ads",
                debug = true)
        setContentView(linksRecyclerView)
    }
}
