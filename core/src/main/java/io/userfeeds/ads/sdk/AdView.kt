package io.userfeeds.ads.sdk

import android.content.Context
import android.support.v4.view.ViewPager
import android.support.v4.view.ViewPager.SCROLL_STATE_DRAGGING
import android.support.v4.view.ViewPager.SCROLL_STATE_IDLE
import android.util.AttributeSet
import android.util.Log
import android.view.View
import android.widget.FrameLayout
import android.widget.TextView
import io.reactivex.android.schedulers.AndroidSchedulers.mainThread
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
    private val flip: Int
    private val debug: Boolean

    private lateinit var ads: Ads
    private lateinit var disposable: Disposable

    private val loader by find<View>(R.id.userfeeds_ads_loader)
    private val viewPager by find<ViewPager>(R.id.userfeeds_ads_pager)
    private val probabilityView by find<TextView>(R.id.userfeeds_ad_probability)

    private val displayRandomAdRunnable = Runnable { displayRandomAd(firstTime = false) }

    constructor(
            context: Context,
            apiKey: String,
            shareContext: String,
            algorithm: String,
            flip: Int = defaultFlip,
            debug: Boolean = defaultDebug) : super(context) {
        this.apiKey = apiKey
        this.shareContext = shareContext
        this.algorithm = algorithm
        this.flip = flip
        this.debug = debug
    }

    constructor(context: Context, attrs: AttributeSet) : this(context, attrs, 0)

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        val a = context.obtainStyledAttributes(attrs, R.styleable.AdView, defStyleAttr, 0)
        this.apiKey = a.getString(R.styleable.AdView_apiKey)
        this.shareContext = a.getString(R.styleable.AdView_context)
        this.algorithm = a.getString(R.styleable.AdView_algorithm)
        this.flip = a.getInt(R.styleable.AdView_flip, defaultFlip)
        this.debug = a.getBoolean(R.styleable.AdView_debug, defaultDebug)
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
                .doOnSubscribe { /* ADS LOAD EVENT */ }
                .doOnError { /* ADS LOAD FAILED EVENT */ }
                .doOnDispose { /* ADS LOAD CANCELLED EVENT */ }
                .observeOn(mainThread())
                .doOnSubscribe { showLoader() }
                .doFinally { hideLoader() }
                .subscribe(this::onAds, this::onError)
    }

    private fun showLoader() {
        loader.visibility = View.VISIBLE
    }

    private fun hideLoader() {
        loader.visibility = View.GONE
    }

    private fun onAds(ads: Ads) {
        this.ads = ads
        initPager()
        displayRandomAd(firstTime = true)
    }

    private fun initPager() {
        viewPager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {

            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) = Unit

            override fun onPageSelected(position: Int) {
                // AD DISPLAYED EVENT
                val value = normalize(ads.items)[position]
                probabilityView.text = "${value.score.toInt()}%"
            }

            override fun onPageScrollStateChanged(state: Int) {
                when (state) {
                    SCROLL_STATE_IDLE -> startCounter()
                    SCROLL_STATE_DRAGGING -> stopCounter()
                }
            }
        })
        viewPager.adapter = AdsPagerAdapter(ads)
    }

    private fun onError(error: Throwable) {
        if (debug) Log.e("AdView", "error", error)
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        if (debug) Log.i("AdView", "onDetachedFromWindow")
        stopCounter()
        disposable.dispose()
    }

    private fun startCounter() {
        if (flip > 0) {
            if (debug) Log.i("AdView", "startCounter ${hashCode()}")
            removeCallbacks(displayRandomAdRunnable)
            postDelayed(displayRandomAdRunnable, flip * 1000L)
        }
    }

    private fun stopCounter() {
        if (debug) Log.i("AdView", "stopCounter ${hashCode()}")
        removeCallbacks(displayRandomAdRunnable)
    }

    private fun displayRandomAd(firstTime: Boolean) {
        val index = ads.items.randomIndex(random)
        if (index == viewPager.currentItem) {
            if (firstTime) {
                // AD DISPLAYED EVENT
            } else {
                // SAME AD DISPLAYED EVENT
            }
        } else {
            viewPager.currentItem = index
        }
        startCounter()
    }

    companion object {

        private const val defaultFlip = 6
        private const val defaultDebug = false
        private val random by lazy(NONE) { SecureRandom() }
    }
}
