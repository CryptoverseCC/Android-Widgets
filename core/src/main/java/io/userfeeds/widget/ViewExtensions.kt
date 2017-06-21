package io.userfeeds.widget

import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE

internal fun View.show() {
    visibility = VISIBLE
}

internal fun View.hide() {
    visibility = GONE
}
