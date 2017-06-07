package io.userfeeds.ads.sdk

import android.content.Context
import android.support.v4.view.ViewPager
import android.util.AttributeSet
import android.util.Log
import android.view.View
import android.widget.FrameLayout
import android.widget.TextView
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.userfeeds.sdk.core.UserfeedsSdk
import io.userfeeds.sdk.core.UserfeedsService
import io.userfeeds.sdk.core.algorithm.Algorithm
import io.userfeeds.sdk.core.context.ShareContext
import java.security.SecureRandom
import kotlin.LazyThreadSafetyMode.NONE

class AdView : FrameLayout {

    private val apiKey: String
    private val shareContext: String
    private val algorithm: String
    private val debug: Boolean

    private lateinit var disposable: Disposable

    constructor(context: Context, apiKey: String, shareContext: String, algorithm: String, debug: Boolean = false) : super(context) {
        this.apiKey = apiKey
        this.shareContext = shareContext
        this.algorithm = algorithm
        this.debug = debug
    }

    constructor(context: Context, attrs: AttributeSet) : this(context, attrs, 0)

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        val a = context.obtainStyledAttributes(attrs, R.styleable.AdView, defStyleAttr, 0)
        this.apiKey = a.getString(R.attr.apiKey)
        this.shareContext = a.getString(R.attr.context)
        this.algorithm = a.getString(R.attr.algorithm)
        this.debug = a.getBoolean(R.attr.debug, false)
        a.recycle()
    }

    init {
        View.inflate(context, R.layout.userfeeds_banner_view, this)
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        if (debug) Log.i("AdView", "onAttachedToWindow")
        loadAds()
    }

    private fun loadAds() {
        UserfeedsSdk.initialize(apiKey = apiKey, debug = debug)
        disposable = UserfeedsService.get().getRanking(ShareContext(shareContext, "", ""), Algorithm(algorithm, ""))
                .map { Ads(items = it, widgetUrl = "http://userfeeds.io/") }
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe { showLoader() }
                .doFinally { hideLoader() }
                .subscribe(this::onAds, this::onError)
    }

    private fun showLoader() {
        findViewById(R.id.userfeeds_ads_loader).visibility = View.VISIBLE
    }

    private fun hideLoader() {
        findViewById(R.id.userfeeds_ads_loader).visibility = View.GONE
    }

    private fun onAds(ads: Ads) {
        val viewPager = findViewById(R.id.userfeeds_ads_pager) as ViewPager
        viewPager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrollStateChanged(state: Int) = Unit
            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) = Unit
            override fun onPageSelected(position: Int) {
                val probabilityView = findViewById(R.id.userfeeds_ad_probability) as TextView
                val value = normalize(ads.items)[position]
                probabilityView.text = "${value.score.toInt()}%"
            }
        })
        viewPager.adapter = AdsPagerAdapter(ads)
        viewPager.currentItem = ads.items.randomIndex(random)
    }

    private fun onError(error: Throwable) {
        if (debug) Log.e("AdView", "error", error)
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        if (debug) Log.i("AdView", "onDetachedFromWindow")
        disposable.dispose()
    }

    companion object {

        private val random by lazy(NONE) { SecureRandom() }
    }
}
