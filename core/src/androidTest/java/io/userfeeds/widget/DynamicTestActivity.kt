package io.userfeeds.widget

import android.app.Activity
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import io.userfeeds.sdk.core.UserfeedsSdk

class DynamicTestActivity : Activity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        UserfeedsSdk.initialize(
                apiKey = "59049c8fdfed920001508e2a94bad07aa8f846674ae92e8765bd926c",
                debug = true)
        val recyclerView = RecyclerView(this).also {
            it.layoutManager = LinearLayoutManager(this)
            it.adapter = DynamicTestAdapter()
        }
        setContentView(recyclerView)
    }
}

class DynamicTestAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun getItemCount() = 28

    override fun getItemViewType(position: Int): Int {
        return if (position == 3 || position == 14) 1 else 0
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == 0) {
            JustViewHolder(TextView(parent.context).apply {
                textSize = 60.0f
            })
        } else {
            AdsViewHolder(LinksViewPager(
                    context = parent.context,
                    shareContext = "ads",
                    algorithm = "internal",
                    flip = 3,
                    debug = true
            ).apply {
                addListener(object : LinksViewPager.EventListener {
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
                    private fun logE(message: String) {
                        Log.e("DynamicTestActivity", "~~~dynamic: $message")
                    }
                })
            })
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is JustViewHolder) {
            holder.textView.text = "postion $position"
        }
    }
}

class JustViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    internal val textView = itemView as TextView
}

class AdsViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)
