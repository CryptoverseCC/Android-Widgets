package io.userfeeds.ads.sdk

import android.content.Context
import android.support.v4.view.ViewPager
import android.util.AttributeSet
import android.util.Log
import android.view.View
import android.widget.FrameLayout
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.userfeeds.sdk.core.UserfeedsSdk
import io.userfeeds.sdk.core.UserfeedsService
import io.userfeeds.sdk.core.algorithm.Algorithm
import io.userfeeds.sdk.core.context.ShareContext
import java.security.SecureRandom
import kotlin.LazyThreadSafetyMode.NONE

class AdView @JvmOverloads constructor(
        context: Context,
        attrs: AttributeSet? = null,
        defStyleAttr: Int = 0)
    :
        FrameLayout(context, attrs, defStyleAttr) {

    private lateinit var _apiKey: String
    private lateinit var _context: String
    private lateinit var _algorithm: String
    private var _debug: Boolean = false

    private lateinit var disposable: Disposable

    init {
        if (attrs != null) {
            _apiKey = attrs.getAttributeValue(R.attr.apiKey)
            _context = attrs.getAttributeValue(R.attr.context)
            _algorithm = attrs.getAttributeValue(R.attr.algorithm)
            _debug = attrs.getAttributeBooleanValue(R.attr.debug, false)
        }
        View.inflate(context, R.layout.userfeeds_banner_view, this)
    }

    fun apiKey(apiKey: String) = apply {
        _apiKey = apiKey
    }

    fun context(context: String) = apply {
        _context = context
    }

    fun algorithm(algorithm: String) = apply {
        _algorithm = algorithm
    }

    fun debug() = apply {
        _debug = true
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        Log.e("AdView", "onAttachedToWindow")
        loadAds()
    }

    private fun loadAds() {
        UserfeedsSdk.initialize(apiKey = _apiKey, debug = _debug)
        disposable = UserfeedsService.get().getRanking(ShareContext(_context, "", ""), Algorithm(_algorithm, ""))
                .map { Ads(items = it, widgetUrl = "http://userfeeds.io/") }
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
