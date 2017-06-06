package io.userfeeds.ads.sdk

import android.content.Context
import android.support.v4.view.ViewPager
import android.util.AttributeSet
import android.util.Log
import android.view.View
import android.widget.FrameLayout
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import io.userfeeds.sdk.core.ranking.RankingItem
import java.math.BigDecimal
import java.security.SecureRandom
import kotlin.LazyThreadSafetyMode.NONE

class AdView @JvmOverloads constructor(
        context: Context,
        attrs: AttributeSet? = null,
        defStyleAttr: Int = 0)
    :
        FrameLayout(context, attrs, defStyleAttr) {

    private lateinit var disposable: Disposable

    init {
        View.inflate(context, R.layout.userfeeds_banner_view, this)
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        Log.e("AdView", "onAttachedToWindow")
        loadAds()
    }

    private fun loadAds() {
        disposable = Observable.just(
                listOf(
                        RankingItem(3.0, "http://userfeeds.io"),
                        RankingItem(2.5, "https://www.coinbase.com/"),
                        RankingItem(1.1, "http://coinmarketcap.com/")
                ))
                .map {
                    Ads(
                            items = it.map { Ad(it.value, BigDecimal(it.score), it.value) },
                            widgetUrl = "http://userfeeds.io/",
                            contextImage = "https://beta.userfeeds.io/api/contexts/static/img/ethereum.png"
                    )
                }
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::onAds, this::onError)
    }

    private fun onAds(ads: Ads) {
        val viewPager = findViewById(R.id.userfeeds_ads_pager) as ViewPager
        viewPager.adapter = AdsPagerAdapter(ads)
        viewPager.currentItem = ads.items.randomIndex(random)
    }

    private fun onError(error: Throwable) {
        Log.e("AdView", "error", error)
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        Log.e("AdView", "onDetachedFromWindow")
        disposable.dispose()
    }

    companion object {

        private val random by lazy(NONE) { SecureRandom() }
    }
}
