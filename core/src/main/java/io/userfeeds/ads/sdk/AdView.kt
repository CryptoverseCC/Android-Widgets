package io.userfeeds.ads.sdk

import android.content.Context
import android.support.v4.view.ViewPager
import android.util.AttributeSet
import android.util.Log
import android.view.View
import android.widget.FrameLayout
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import io.userfeeds.sdk.core.UserfeedsService
import java.math.BigDecimal
import java.security.SecureRandom
import kotlin.LazyThreadSafetyMode.NONE

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
        UserfeedsService.get().getContexts()
                .flatMap {
                    val shareContext = it.single { it.id == "ethereum" }
                    UserfeedsService.get().getAlgorithms(shareContext)
                            .flatMap {
                                val algorithm = it.single { it.identifier == "newa" }
                                UserfeedsService.get().getRanking(shareContext, algorithm)
                            }
                }
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
    }

    companion object {

        private val random by lazy(NONE) { SecureRandom() }
    }
}
