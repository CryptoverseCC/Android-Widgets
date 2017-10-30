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

    private val recipientAddress: String
    private val asset: String
    private val whitelist: String?
    private val algorithm: String
    private val title: String?
    private val description: String?
    private val contactMethod: String?
    private val impressions: String?
    private val slots: Int
    private val flip: Int
    private val apiUrl: String
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

    private val displayRandomLinkRunnable = Runnable(this::displayRandomLink)

    constructor(
            context: Context,
            asset: String,
            recipientAddress: String,
            algorithm: String = "links",
            whitelist: String? = null,
            title: String? = null,
            description: String? = null,
            contactMethod: String? = null,
            impressions: String? = null,
            slots: Int = 10,
            flip: Int = 6,
            apiUrl: String = "https://api.userfeeds.io",
            debug: Boolean = false) : super(context) {
        this.asset = asset.toLowerCase()
        this.recipientAddress = recipientAddress.toLowerCase()
        this.whitelist = whitelist?.toLowerCase()
        this.algorithm = algorithm
        this.title = title
        this.description = description
        this.contactMethod = contactMethod
        this.impressions = impressions
        this.slots = slots
        this.flip = flip
        this.apiUrl = apiUrl
        this.debug = debug
    }

    constructor(context: Context, attrs: AttributeSet) : this(context, attrs, 0)

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        val a = context.obtainStyledAttributes(attrs, R.styleable.LinksViewPager, defStyleAttr, 0)
        this.asset = a.getString(R.styleable.LinksViewPager_asset) ?: throw Exception("asset not set")
        this.recipientAddress = a.getString(R.styleable.LinksViewPager_recipientAddress) ?: throw Exception("recipientAddress not set")
        this.algorithm = a.getString(R.styleable.LinksViewPager_algorithm) ?: "links"
        this.whitelist = a.getString(R.styleable.LinksViewPager_whitelist)
        this.title = a.getString(R.styleable.LinksViewPager_title)
        this.description = a.getString(R.styleable.LinksViewPager_title)
        this.contactMethod = a.getString(R.styleable.LinksViewPager_title)
        this.impressions = a.getString(R.styleable.LinksViewPager_title)
        this.slots = a.getInt(R.styleable.LinksViewPager_slots, 10)
        this.flip = a.getInt(R.styleable.LinksViewPager_flip, 6)
        this.apiUrl = a.getString(R.styleable.LinksViewPager_apiUrl) ?: "https://api.userfeeds.io"
        this.debug = a.getBoolean(R.styleable.LinksViewPager_debug, false)
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
                .getRanking(asset, recipientAddress, algorithm, whitelist)
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
        get() = "https://linkexchange.io/apps/widgets/#/details" +
                "?recipientAddress=${Uri.encode(recipientAddress)}" +
                "&asset=${Uri.encode(asset)}" +
                (if (whitelist != null) "&whitelist=${Uri.encode(whitelist)}" else "") +
                "&algorithm=${Uri.encode(algorithm)}" +
                "&size=android%20banner"

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
            removeCallbacks(displayRandomLinkRunnable)
            postDelayed(displayRandomLinkRunnable, flip * 1000L)
        }
    }

    private fun stopCounter() {
        logInfo("stopCounter")
        removeCallbacks(displayRandomLinkRunnable)
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

        private val random by lazy(NONE) { SecureRandom() }
    }
}
