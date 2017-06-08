package io.userfeeds.ads.sdk

import android.support.annotation.IdRes
import android.view.View
import android.view.ViewGroup
import kotlin.LazyThreadSafetyMode.NONE

internal inline fun <reified T : View> ViewGroup.find(@IdRes id: Int): Lazy<T>
        = lazy(NONE) { findViewById(id) as T }
