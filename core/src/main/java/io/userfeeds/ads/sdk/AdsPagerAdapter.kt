package io.userfeeds.ads.sdk

import android.support.v4.view.PagerAdapter
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import io.userfeeds.sdk.core.ranking.RankingItem

class AdsPagerAdapter(private val ranking: List<RankingItem>) : PagerAdapter() {

    override fun getCount() = ranking.size

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        val inflater = LayoutInflater.from(container.context)
        val view = inflater.inflate(R.layout.userfeeds_ad_view, container, false)
        container.addView(view)
        val titleView = view.findViewById(R.id.userfeeds_ad_title) as TextView
        titleView.text = "${ranking[position].value} (${ranking[position].score})"
        return view
    }

    override fun destroyItem(container: ViewGroup, position: Int, obj: Any) {
        container.removeView(obj as View)
    }

    override fun isViewFromObject(view: View, obj: Any) = view === obj
}
