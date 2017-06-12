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
import io.userfeeds.sdk.core.ranking.RankingItem
import java.security.SecureRandom
import kotlin.LazyThreadSafetyMode.NONE

class AdView : FrameLayout {

    private val apiKey: String
    private val shareContext: String
    private val algorithm: String
    private val flip: Int
    private val debug: Boolean

    private val listeners = mutableListOf<AdViewEventListener>()

    private lateinit var ads: List<RankingItem>
    private lateinit var disposable: Disposable

    private val loader by find<View>(R.id.userfeeds_ads_loader)
    private val viewPager by find<ViewPager>(R.id.userfeeds_ads_pager)
    private val detailsPanel by find<View>(R.id.userfeeds_ads_details_panel)
    private val probabilityView by find<TextView>(R.id.userfeeds_ad_probability)
    private val emptyView by find<TextView>(R.id.userfeeds_ads_empty_view)

    private val displayRandomAdRunnable = Runnable(this::displayRandomAd)

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

    fun addListener(listener: AdViewEventListener) {
        listeners += listener
    }

    fun removeListener(listener: AdViewEventListener) {
        listeners -= listener
    }

    private fun notifyListeners(func: AdViewEventListener.() -> Unit) {
        listeners.forEach { it.func() }
    }

    init {
        View.inflate(context, R.layout.userfeeds_banner_view, this)
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        logInfo("onAttachedToWindow")
        loadAds()
    }

    private fun loadAds() {
        UserfeedsSdk.initialize(apiKey = apiKey, debug = debug)
        disposable = UserfeedsService.get().getRanking(ShareContext(shareContext, "", ""), Algorithm(algorithm, ""))
                .observeOn(mainThread())
                .doOnSubscribe { notifyListeners { adsLoadStart() } }
                .doOnSuccess { notifyListeners { adsLoadSuccess() } }
                .doOnError { notifyListeners { adsLoadError() } }
                .doOnDispose { notifyListeners { adsLoadCancel() } }
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

    private fun onAds(ads: List<RankingItem>) {
        if (ads.isEmpty()) {
            emptyView.visibility = View.VISIBLE
            emptyView.setText(R.string.userfeeds_ads_empty)
            emptyView.setOnLongClickListener {
                context.openBrowser("http://userfeeds.io/")
                notifyListeners { widgetDetails() }
                true
            }
        } else {
            this.ads = ads
            detailsPanel.visibility = View.VISIBLE
            initPager()
            displayRandomAd()
        }
    }

    private fun initPager() {
        viewPager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {

            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) = Unit

            override fun onPageSelected(position: Int) {
                notifyListeners { adDisplay() }
                val value = normalize(ads)[position]
                probabilityView.text = "${value.score.toInt()}%"
            }

            override fun onPageScrollStateChanged(state: Int) {
                logInfo("onPageScrollStateChanged " + state)
                when (state) {
                    SCROLL_STATE_IDLE -> startCounter()
                    SCROLL_STATE_DRAGGING -> {
                        notifyListeners { adSwipe() }
                        stopCounter()
                    }
                }
            }
        })
        viewPager.adapter = AdsPagerAdapter(ads, object : AdsPagerAdapter.Listener {

            override fun onAdClick(item: RankingItem) {
                notifyListeners { adClick() }
                context.openBrowser(item.target)
                notifyListeners { adTarget() }
            }

            override fun onAdLongClick(item: RankingItem) {
                notifyListeners { adLongClick() }
                logInfo("onAdLongClick")
                context.openBrowser("http://userfeeds.io/")
                notifyListeners { widgetDetails() }
            }
        })
    }

    private fun onError(error: Throwable) {
        if (debug) Log.e("AdView", "error", error)
        emptyView.visibility = View.VISIBLE
        emptyView.setText(R.string.userfeeds_ads_load_error)
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        logInfo("onDetachedFromWindow")
        stopCounter()
        disposable.dispose()
    }

    private fun startCounter() {
        if (flip > 0) {
            logInfo("startCounter")
            removeCallbacks(displayRandomAdRunnable)
            postDelayed(displayRandomAdRunnable, flip * 1000L)
        }
    }

    private fun stopCounter() {
        logInfo("stopCounter")
        removeCallbacks(displayRandomAdRunnable)
    }

    private fun displayRandomAd() {
        val index = ads.randomIndex(random)
        if (index == viewPager.currentItem) {
            notifyListeners { adDisplay() }
            startCounter()
        } else {
            viewPager.currentItem = index
        }
    }

    private fun logInfo(message: String) {
        if (debug) Log.i("AdView", message)
    }

    companion object {

        private const val defaultFlip = 6
        private const val defaultDebug = false
        private val random by lazy(NONE) { SecureRandom() }
    }
}
