package io.userfeeds.widget

import android.app.Activity
import android.os.Bundle
import io.userfeeds.sdk.core.UserfeedsSdk

class RecyclerViewTestActivity : Activity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        UserfeedsSdk.initialize("", debug = true)
        val linksRecyclerView = LinksRecyclerView(
                context = this,
                rankingContext = "rinkeby:0x0406735fc1a657398941a50a0602eddf9723a6c8",
                algorithm = "links",
                debug = true)
        setContentView(linksRecyclerView)
    }
}
