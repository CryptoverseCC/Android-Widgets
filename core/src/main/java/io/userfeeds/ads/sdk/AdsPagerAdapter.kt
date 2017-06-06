package io.userfeeds.ads.sdk

import android.support.v4.view.PagerAdapter
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView

internal class AdsPagerAdapter(private val ads: Ads) : PagerAdapter() {

    override fun getCount() = ads.items.size

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        val inflater = LayoutInflater.from(container.context)
        return inflater.inflate(R.layout.userfeeds_ad_view, container, false).apply {
            container.addView(this)
            bind(this, ads.items[position])
        }
    }

    private fun bind(view: View, ad: Ad) {
        val titleView = view.findViewById(R.id.userfeeds_ad_title) as TextView
        titleView.text = ad.title
        val urlView = view.findViewById(R.id.userfeeds_ad_url) as TextView
        urlView.text = ad.url
        view.setOnClickListener {
            it.context.openBrowser(ad.url)
        }
        view.setOnLongClickListener {
            it.context.openBrowser(ads.widgetUrl)
            true
        }
    }

    override fun destroyItem(container: ViewGroup, position: Int, obj: Any) {
        container.removeView(obj as View)
    }

    override fun isViewFromObject(view: View, obj: Any) = view === obj
}
