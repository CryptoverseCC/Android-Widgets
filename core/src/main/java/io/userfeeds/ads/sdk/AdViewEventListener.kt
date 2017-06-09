package io.userfeeds.ads.sdk

abstract class AdViewEventListener {
    
    open fun adsLoadStart() = Unit
    open fun adsLoadSuccess() = Unit
    open fun adsLoadError() = Unit
    open fun adsLoadCancel() = Unit
    open fun adDisplay() = Unit
    open fun adTarget() = Unit
    open fun widgetDetails() = Unit

    open fun adClick() = Unit
    open fun adLongClick() = Unit
    open fun adSwipe() = Unit
}
