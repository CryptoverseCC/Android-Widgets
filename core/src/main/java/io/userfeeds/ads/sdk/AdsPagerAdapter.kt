package io.userfeeds.ads.sdk

import android.support.v4.view.PagerAdapter
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import io.userfeeds.sdk.core.ranking.RankingItem

internal class AdsPagerAdapter(private val items: List<RankingItem>, private val listener: AdsPagerAdapter.Listener) : PagerAdapter() {

    interface Listener {

        fun onAdClick(item: RankingItem)

        fun onAdLongClick(item: RankingItem)
    }

    override fun getCount() = items.size

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        val inflater = LayoutInflater.from(container.context)
        return inflater.inflate(R.layout.userfeeds_ad_view, container, false).apply {
            container.addView(this)
            bind(this, items[position])
        }
    }

    private fun bind(view: View, item: RankingItem) {
        val titleView = view.findViewById(R.id.userfeeds_ad_title) as TextView
        titleView.text = item.title
        val urlView = view.findViewById(R.id.userfeeds_ad_url) as TextView
        urlView.text = item.target
        view.setOnClickListener {
            listener.onAdClick(item)
        }
        view.setOnLongClickListener {
            listener.onAdLongClick(item)
            true
        }
    }

    override fun destroyItem(container: ViewGroup, position: Int, obj: Any) {
        container.removeView(obj as View)
    }

    override fun isViewFromObject(view: View, obj: Any) = view === obj
}
