package io.userfeeds.widget

import android.support.v4.view.PagerAdapter
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import io.userfeeds.sdk.core.ranking.RankingItem

internal class LinksPagerAdapter(private val items: List<RankingItem>, private val listener: Listener) : PagerAdapter() {

    interface Listener {

        fun onAdClick(item: RankingItem)

        fun onAdLongClick(item: RankingItem)
    }

    override fun getCount() = items.size

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        val inflater = LayoutInflater.from(container.context)
        return inflater.inflate(R.layout.userfeeds_link_view, container, false).apply {
            container.addView(this)
            bind(this, items[position])
        }
    }

    private fun bind(view: View, item: RankingItem) {
        val titleView = view.findViewById(R.id.userfeeds_link_title) as TextView
        titleView.text = item.title
        val urlView = view.findViewById(R.id.userfeeds_link_url) as TextView
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
