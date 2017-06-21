# Android-Widgets

SDK with collection of widgets.

## Setup

### Add dependency

```groovy
dependencies {
    compile 'io.userfeeds.widget:core:<latest version here>'
    compile 'com.android.support:support-v4:<latest version here>'
}
```

You may find the latest version number by going to [maven central search](http://search.maven.org/#search|ga|1|g%3A%22io.userfeeds.ads.sdk%22).

You will need to also depend on support-v4 library, but you don't have to specify it explicitly if you already depend on another support library that depends on it (e.g. appcompat-v7, desing).

## Use

### Add `LinksViewPager` to layout

```
<io.userfeeds.widget.LinksViewPager
    xmlns:userfeeds="http://schemas.android.com/apk/res-auto"
    android:id="@+id/my_links_view"
    android:layout_width="match_parent"
    android:layout_height="wrap_content"
    userfeeds:context="ethereum:0x0406735fC1a657398941A50A0602eddf9723A6C8"
    userfeeds:algorithm="ads"/>
```

### Or create it via code

```
val myLinksView = LinksViewPager(
        context = this,
        shareContext = "ethereum:0x0406735fC1a657398941A50A0602eddf9723A6C8",
        algorithm = "ads")
val layoutParams = FrameLayout.LayoutParams(MATCH_PARENT, WRAP_CONTENT).apply {
    gravity = BOTTOM
}
someFrameLayout.addView(myAdView, layoutParams)
```

### What's inside

You may add event listeners to `LinksViewPager` to know what's happening.

```
myAdView.addListener(object : LinksViewPager.EventListener {
    override fun linkClick(index: Int) = logE("linkClick $index")
    override fun linkLongClick(index: Int) = logE("linkLongClick $index")
    override fun linksSwipe(index: Int) = logE("linksSwipe $index")

    override fun linksLoadStart() = logE("linksLoadStart")
    override fun linksLoadSuccess() = logE("linksLoadSuccess")
    override fun linksLoadEmpty() = logE("linksLoadEmpty")
    override fun linksLoadError() = logE("linksLoadError")
    override fun linksLoadCancel() = logE("linksLoadCancel")
    override fun linkDisplay(index: Int) = logE("linkDisplay $index")
    override fun linkOpen(index: Int) = logE("linkOpen $index")
    override fun widgetOpen() = logE("widgetOpen")
    private fun logE(message: String) {
        Log.e(TAG, message)
    }
})
```

### Settings

`userfeeds:flip="10"` or constructor parameter `flip` can be used to change how often ads are switched. Default value is 6 seconds. Set value lower or equal to 0 if you don't want links to be switched automatically.
