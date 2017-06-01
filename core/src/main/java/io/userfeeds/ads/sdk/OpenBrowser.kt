package io.userfeeds.ads.sdk

import android.content.Context
import android.content.Intent
import android.net.Uri

internal fun Context.openBrowser(url: String) {
    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
    startActivity(intent)
}
