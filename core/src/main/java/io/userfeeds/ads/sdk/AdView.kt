package io.userfeeds.ads.sdk

import android.content.Context
import android.graphics.Color
import android.support.v4.view.ViewPager
import android.util.AttributeSet
import android.util.Log
import android.view.View
import android.widget.FrameLayout
import io.reactivex.Single.just
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import io.userfeeds.sdk.core.ranking.RankingItem

class AdView @JvmOverloads constructor(
        context: Context,
        attrs: AttributeSet? = null,
        defStyleAttr: Int = 0)
    :
        FrameLayout(context, attrs, defStyleAttr) {

    init {
        View.inflate(context, R.layout.userfeeds_banner_view, this)
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        Log.e("AdView", "onAttachedToWindow")
        just(Ads(
                items = listOf(
                        Ad("Yafi - Internet Chess", 0.50, "http://yafi.pl"),
                        Ad("CoinMarkerCap", 0.30, "http://coinmarketcap.com"),
                        Ad("CoinBase", 0.20, "https://www.coinbase.com/join")
                ),
                widgetUrl = "http://userfeeds.io/",
                contextImage = "https://beta.userfeeds.io/api/contexts/static/img/ethereum.png"
        ))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::onAds, this::onError)
    }

    private fun onAds(ads: Ads) {
        val viewPager = findViewById(R.id.userfeeds_ads_pager) as ViewPager
        viewPager.adapter = AdsPagerAdapter(ads.items)
    }

    private fun onError(error: Throwable) {
        Log.e("AdView", "error", error)
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        Log.e("AdView", "onDetachedFromWindow")
    }
}
