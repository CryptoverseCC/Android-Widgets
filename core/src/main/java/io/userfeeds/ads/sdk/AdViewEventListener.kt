package io.userfeeds.ads.sdk

interface AdViewEventListener {
    
    fun adsLoadStart() = Unit
    fun adsLoadSuccess() = Unit
    fun adsLoadError() = Unit
    fun adsLoadCancel() = Unit
    fun adDisplay(index: Int) = Unit
    fun adTarget(index: Int) = Unit
    fun widgetDetails() = Unit

    fun adClick(index: Int) = Unit
    fun adLongClick(index: Int) = Unit
    fun adSwipe(index: Int) = Unit
}
