package io.userfeeds.widget

import android.content.Context
import android.net.Uri
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.AttributeSet
import android.util.Log
import android.view.View
import android.widget.FrameLayout
import android.widget.TextView
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.userfeeds.sdk.core.UserfeedsService
import io.userfeeds.sdk.core.algorithm.Algorithm
import io.userfeeds.sdk.core.context.ShareContext
import io.userfeeds.sdk.core.ranking.RankingItem

class LinksRecyclerView : FrameLayout {

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
    private val debug: Boolean

    private lateinit var links: List<RankingItem>
    private var loaded = false
    private lateinit var disposable: Disposable

    private val loader by find<View>(R.id.userfeeds_links_loader)
    private val recycler by find<RecyclerView>(R.id.userfeeds_links_recycler_view)
    private val emptyView by find<TextView>(R.id.userfeeds_links_empty_view)

    private fun notifyListeners(func: EventListener.() -> Unit) {
        object : EventListener {}.func()
    }

    constructor(
            context: Context,
            shareContext: String,
            algorithm: String,
            whitelist: String? = null,
            debug: Boolean = defaultDebug) : super(context) {
        this.shareContext = shareContext
        this.algorithm = algorithm
        this.whitelist = whitelist
        this.debug = debug
    }

    constructor(context: Context, attrs: AttributeSet) : this(context, attrs, 0)

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        val a = context.obtainStyledAttributes(attrs, R.styleable.LinksRecyclerView, defStyleAttr, 0)
        this.shareContext = a.getString(R.styleable.LinksRecyclerView_context)
        this.algorithm = a.getString(R.styleable.LinksRecyclerView_algorithm)
        this.whitelist = a.getString(R.styleable.LinksRecyclerView_whitelist)
        this.debug = a.getBoolean(R.styleable.LinksRecyclerView_debug, defaultDebug)
        a.recycle()
    }

    init {
        inflate(context, R.layout.userfeeds_links_recycler_view, this)
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        if (!loaded) {
            loadLinks()
        }
    }

    private fun loadLinks() {
        disposable = UserfeedsService.get()
                .getRanking(ShareContext(shareContext, "", ""), Algorithm(algorithm, ""), whitelist)
                .map(::normalize)
                .observeOn(AndroidSchedulers.mainThread())
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
            initRecycler()
        }
    }

    private fun initRecycler() {
        recycler.layoutManager = LinearLayoutManager(context)
        recycler.adapter = LinksRecyclerAdapter(links, object : LinksRecyclerAdapter.Listener {

            override fun onLinkClick(item: RankingItem) {
                notifyListeners { linkClick(links.indexOf(item)) }
                context.openBrowser(Uri.parse(item.target))
                notifyListeners { linkOpen(links.indexOf(item)) }
            }
        })
    }

    private val widgetDetailsUrl
        get() = Uri.parse("https://userfeeds.io/apps/widgets/details/").buildUpon()
                .appendQueryParameter("context", shareContext)
                .appendQueryParameter("algorithm", algorithm)
                .apply { if (whitelist != null) appendQueryParameter("whitelist", whitelist) }
                .build()

    private fun onError(error: Throwable) {
        if (debug) Log.e("LinksViewPager", "error", error)
        emptyView.show()
        emptyView.setText(R.string.userfeeds_links_load_error)
    }

    companion object {

        private const val defaultDebug = false
    }
}
