package io.userfeeds.widget

import android.support.v4.view.ViewPager
import android.support.v4.view.ViewPager.SCROLL_STATE_DRAGGING
import android.support.v4.view.ViewPager.SCROLL_STATE_IDLE
import android.view.View
import android.widget.TextView
import io.reactivex.android.schedulers.AndroidSchedulers.mainThread
import io.reactivex.disposables.Disposable
import io.userfeeds.sdk.core.UserfeedsService
import io.userfeeds.sdk.core.algorithm.Algorithm
import io.userfeeds.sdk.core.context.ShareContext
import io.userfeeds.sdk.core.ranking.RankingItem
import java.security.SecureRandom
import kotlin.LazyThreadSafetyMode.NONE

class LinksViewPager : android.widget.FrameLayout {

    interface EventListener {

        fun linksLoadStart() = Unit
        fun linksLoadSuccess() = Unit
        fun linksLoadEmpty() = Unit
        fun linksLoadError() = Unit
        fun linksLoadCancel() = Unit
        fun linkDisplay(index: Int) = Unit
        fun linkOpen(index: Int) = Unit
        fun widgetOpen() = Unit

        fun linkClick(index: Int) = Unit
        fun linkLongClick(index: Int) = Unit
        fun linksSwipe(index: Int) = Unit
    }

    private val shareContext: String
    private val algorithm: String
    private val flip: Int
    private val debug: Boolean

    private val listeners = mutableListOf<EventListener>()

    private lateinit var ads: List<RankingItem>
    private var loaded = false
    private lateinit var disposable: Disposable

    private val loader by find<View>(R.id.userfeeds_ads_loader)
    private val viewPager by find<ViewPager>(R.id.userfeeds_ads_pager)
    private val detailsPanel by find<View>(R.id.userfeeds_ads_details_panel)
    private val probabilityView by find<TextView>(R.id.userfeeds_ad_probability)
    private val emptyView by find<TextView>(R.id.userfeeds_ads_empty_view)

    private val displayRandomAdRunnable = Runnable(this::displayRandomAd)

    constructor(
            context: android.content.Context,
            shareContext: String,
            algorithm: String,
            flip: Int = defaultFlip,
            debug: Boolean = defaultDebug) : super(context) {
        this.shareContext = shareContext
        this.algorithm = algorithm
        this.flip = flip
        this.debug = debug
    }

    constructor(context: android.content.Context, attrs: android.util.AttributeSet) : this(context, attrs, 0)

    constructor(context: android.content.Context, attrs: android.util.AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        val a = context.obtainStyledAttributes(attrs, R.styleable.LinksViewPager, defStyleAttr, 0)
        this.shareContext = a.getString(R.styleable.LinksViewPager_context)
        this.algorithm = a.getString(R.styleable.LinksViewPager_algorithm)
        this.flip = a.getInt(R.styleable.LinksViewPager_flip, io.userfeeds.widget.LinksViewPager.Companion.defaultFlip)
        this.debug = a.getBoolean(R.styleable.LinksViewPager_debug, io.userfeeds.widget.LinksViewPager.Companion.defaultDebug)
        a.recycle()
    }

    fun addListener(listener: EventListener) {
        listeners += listener
    }

    fun removeListener(listener: EventListener) {
        listeners -= listener
    }

    private fun notifyListeners(func: EventListener.() -> Unit) {
        listeners.forEach { it.func() }
    }

    init {
        android.view.View.inflate(context, R.layout.userfeeds_links_pager_view, this)
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        logInfo("onAttachedToWindow")
        if (!loaded) {
            loadAds()
        } else {
            displayRandomAd()
            startCounter()
        }
    }

    private fun loadAds() {
        disposable = UserfeedsService.get().getRanking(ShareContext(shareContext, "", ""), Algorithm(algorithm, ""))
                .observeOn(mainThread())
                .doOnSubscribe { notifyListeners { linksLoadStart() } }
                .doOnSuccess { notifyListeners { linksLoadSuccess() } }
                .doOnError { notifyListeners { linksLoadError() } }
                .doOnDispose { notifyListeners { linksLoadCancel() } }
                .doOnSubscribe { showLoader() }
                .doFinally { hideLoader() }
                .subscribe(this::onLinks, this::onError)
    }

    private fun showLoader() {
        loader.visibility = android.view.View.VISIBLE
    }

    private fun hideLoader() {
        loader.visibility = android.view.View.GONE
    }

    private fun onLinks(links: List<RankingItem>) {
        if (links.isEmpty()) {
            notifyListeners { linksLoadEmpty() }
            emptyView.visibility = android.view.View.VISIBLE
            emptyView.setText(R.string.userfeeds_links_empty)
            emptyView.setOnLongClickListener {
                context.openBrowser("http://userfeeds.io/")
                notifyListeners { widgetOpen() }
                true
            }
        } else {
            this.ads = links
            this.loaded = true
            detailsPanel.visibility = android.view.View.VISIBLE
            initPager()
            displayRandomAd()
            startCounter()
        }
    }

    private fun initPager() {
        viewPager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {

            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) = Unit

            override fun onPageSelected(position: Int) {
                notifyListeners { linkDisplay(position) }
                val value = normalize(ads)[position]
                probabilityView.text = "${value.score.toInt()}%"
            }

            override fun onPageScrollStateChanged(state: Int) {
                logInfo("onPageScrollStateChanged " + state)
                when (state) {
                    SCROLL_STATE_IDLE -> startCounter()
                    SCROLL_STATE_DRAGGING -> {
                        notifyListeners { linksSwipe(viewPager.currentItem) }
                        stopCounter()
                    }
                }
            }
        })
        viewPager.adapter = LinksPagerAdapter(ads, object : LinksPagerAdapter.Listener {

            override fun onAdClick(item: RankingItem) {
                notifyListeners { linkClick(ads.indexOf(item)) }
                context.openBrowser(item.target)
                notifyListeners { linkOpen(ads.indexOf(item)) }
            }

            override fun onAdLongClick(item: RankingItem) {
                notifyListeners { linkLongClick(ads.indexOf(item)) }
                context.openBrowser("http://userfeeds.io/")
                notifyListeners { widgetOpen() }
            }
        })
    }

    private fun onError(error: Throwable) {
        if (debug) android.util.Log.e("LinksViewPager", "error", error)
        emptyView.visibility = android.view.View.VISIBLE
        emptyView.setText(R.string.userfeeds_links_load_error)
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
            notifyListeners { linkDisplay(index) }
            startCounter()
        } else {
            viewPager.currentItem = index
        }
    }

    private fun logInfo(message: String) {
        if (debug) android.util.Log.i("LinksViewPager", message)
    }

    companion object {

        private const val defaultFlip = 6
        private const val defaultDebug = false
        private val random by lazy(NONE) { SecureRandom() }
    }
}
