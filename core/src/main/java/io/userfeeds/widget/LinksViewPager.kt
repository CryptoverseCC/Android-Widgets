package io.userfeeds.widget

import android.content.Context
import android.net.Uri
import android.support.v4.view.ViewPager
import android.support.v4.view.ViewPager.SCROLL_STATE_DRAGGING
import android.support.v4.view.ViewPager.SCROLL_STATE_IDLE
import android.util.AttributeSet
import android.util.Log
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
    private val whitelist: String?
    private val publisherNote: String?
    private val flip: Int
    private val debug: Boolean

    private val listeners = mutableListOf<EventListener>()

    private lateinit var links: List<RankingItem>
    private var loaded = false
    private lateinit var disposable: Disposable

    private val loader by find<View>(R.id.userfeeds_links_loader)
    private val viewPager by find<ViewPager>(R.id.userfeeds_links_pager)
    private val detailsPanel by find<View>(R.id.userfeeds_links_details_panel)
    private val probabilityView by find<TextView>(R.id.userfeeds_link_probability)
    private val emptyView by find<TextView>(R.id.userfeeds_links_empty_view)

    private val displayRandomAdRunnable = Runnable(this::displayRandomLink)

    constructor(
            context: Context,
            shareContext: String,
            algorithm: String,
            whitelist: String? = null,
            publisherNote: String? = null,
            flip: Int = defaultFlip,
            debug: Boolean = defaultDebug) : super(context) {
        this.shareContext = shareContext
        this.algorithm = algorithm
        this.whitelist = whitelist
        this.publisherNote = publisherNote
        this.flip = flip
        this.debug = debug
    }

    constructor(context: Context, attrs: AttributeSet) : this(context, attrs, 0)

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        val a = context.obtainStyledAttributes(attrs, R.styleable.LinksViewPager, defStyleAttr, 0)
        this.shareContext = a.getString(R.styleable.LinksViewPager_context)
        this.algorithm = a.getString(R.styleable.LinksViewPager_algorithm)
        this.whitelist = a.getString(R.styleable.LinksViewPager_whitelist)
        this.publisherNote = a.getString(R.styleable.LinksViewPager_publisherNote)
        this.flip = a.getInt(R.styleable.LinksViewPager_flip, defaultFlip)
        this.debug = a.getBoolean(R.styleable.LinksViewPager_debug, defaultDebug)
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
        inflate(context, R.layout.userfeeds_links_pager_view, this)
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        logInfo("onAttachedToWindow")
        if (!loaded) {
            loadLinks()
        } else {
            displayRandomLink()
            startCounter()
        }
    }

    private fun loadLinks() {
        disposable = UserfeedsService.get()
                .getRanking(ShareContext(shareContext, "", ""), Algorithm(algorithm, ""), whitelist)
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
        loader.show()
    }

    private fun hideLoader() {
        loader.hide()
    }

    private fun onLinks(links: List<RankingItem>) {
        if (links.isEmpty()) {
            notifyListeners { linksLoadEmpty() }
            emptyView.show()
            emptyView.setText(R.string.userfeeds_links_empty)
            emptyView.setOnLongClickListener {
                context.openBrowser(widgetDetailsUrl)
                notifyListeners { widgetOpen() }
                true
            }
        } else {
            this.links = links
            this.loaded = true
            detailsPanel.show()
            initPager()
            displayRandomLink()
            startCounter()
        }
    }

    private fun initPager() {
        viewPager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {

            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) = Unit

            override fun onPageSelected(position: Int) {
                notifyListeners { linkDisplay(position) }
                val value = normalize(links)[position]
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
        viewPager.adapter = LinksPagerAdapter(links, object : LinksPagerAdapter.Listener {

            override fun onAdClick(item: RankingItem) {
                notifyListeners { linkClick(links.indexOf(item)) }
                context.openBrowser(item.target)
                notifyListeners { linkOpen(links.indexOf(item)) }
            }

            override fun onAdLongClick(item: RankingItem) {
                notifyListeners { linkLongClick(links.indexOf(item)) }
                context.openBrowser(widgetDetailsUrl)
                notifyListeners { widgetOpen() }
            }
        })
    }

    private val widgetDetailsUrl
        get() = "https://userfeeds.io/apps/widgets/#/details" +
                "?context=${Uri.encode(shareContext)}" +
                "&algorithm=${Uri.encode(algorithm)}" +
                (if (whitelist != null) "&whitelist=${Uri.encode(whitelist)}" else "") +
                (if (publisherNote != null) "&publisherNote=${Uri.encode(publisherNote)}" else "")

    private fun onError(error: Throwable) {
        if (debug) Log.e("LinksViewPager", "error", error)
        emptyView.show()
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

    private fun displayRandomLink() {
        val index = links.randomIndex(random)
        if (index == viewPager.currentItem) {
            notifyListeners { linkDisplay(index) }
            startCounter()
        } else {
            viewPager.currentItem = index
        }
    }

    private fun logInfo(message: String) {
        if (debug) Log.i("LinksViewPager", message)
    }

    companion object {

        private const val defaultFlip = 6
        private const val defaultDebug = false
        private val random by lazy(NONE) { SecureRandom() }
    }
}
