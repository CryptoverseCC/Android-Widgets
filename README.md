# android-ads-sdk

SDK with view to display ads.

## Setup

### Request API key

Go to https://api.userfeeds.io/portal/apis/, register and receive key.

### Add dependency

```groovy
dependencies {
    compile 'io.userfeeds.ads.sdk:core:<latest version here>'
    compile 'com.android.support:support-v4:<latest version here>'
}
```

You may find the latest version number by going to [maven central search](http://search.maven.org/#search|ga|1|g%3A%22io.userfeeds.ads.sdk%22).

You will need to also depend on support-v4 library, but you don't have to specify it explicitly if you already depend on another support library that depends on it (e.g. appcompat-v7, desing).

### Initialize SDK

You will usually init the SDK in a class extending Application.

```
class MyAwesomeApp : Application() {

    override fun onCreate() {
        UserfeedsSdk.initialize(apiKey = "<your API key>")
    }
}
```

## Use

### Add `AdView` to layout

```
<io.userfeeds.ads.sdk.AdView
    xmlns:userfeeds="http://schemas.android.com/apk/res-auto"
    android:id="@+id/my_ad_view"
    android:layout_width="match_parent"
    android:layout_height="wrap_content"
    userfeeds:context="ethereum:0x0406735fC1a657398941A50A0602eddf9723A6C8"
    userfeeds:algorithm="ads"/>
```

### Or create it via code

```
val myAdView = AdView(
        context = this,
        shareContext = "ethereum:0x0406735fC1a657398941A50A0602eddf9723A6C8",
        algorithm = "ads")
val layoutParams = FrameLayout.LayoutParams(MATCH_PARENT, WRAP_CONTENT).apply {
    gravity = BOTTOM
}
someFrameLayout.addView(myAdView, layoutParams)
```

### What's inside

You may add event listeners to `AdView` to know what's happening.

```
myAdView.addListener(object : AdViewEventListener {
    override fun adClick(index: Int) = logE("adClick $index")
    override fun adLongClick(index: Int) = logE("adLongClick $index")
    override fun adSwipe(index: Int) = logE("adSwipe $index")

    override fun adsLoadStart() = logE("adsLoadStart")
    override fun adsLoadSuccess() = logE("adsLoadSuccess")
    override fun adsLoadError() = logE("adsLoadError")
    override fun adsLoadCancel() = logE("adsLoadCancel")
    override fun adDisplay(index: Int) = logE("adDisplay $index")
    override fun adTarget(index: Int) = logE("adTarget $index")
    override fun widgetDetails() = logE("widgetDetails")
    private fun logE(message: String) {
        Log.e(TAG, message)
    }
})
```

### Settings

`userfeeds:flip="10"` or constructor parameter `flip` can be used to change how often ads are switched. Default value is 6 seconds. Set value lower to equal to 0 if you don't want ads to be switched automatically.
