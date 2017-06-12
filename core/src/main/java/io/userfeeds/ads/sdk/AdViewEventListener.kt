package io.userfeeds.ads.sdk

interface AdViewEventListener {
    
    fun adsLoadStart() = Unit
    fun adsLoadSuccess() = Unit
    fun adsLoadError() = Unit
    fun adsLoadCancel() = Unit
    fun adDisplay() = Unit
    fun adTarget() = Unit
    fun widgetDetails() = Unit

    fun adClick() = Unit
    fun adLongClick() = Unit
    fun adSwipe() = Unit
}
