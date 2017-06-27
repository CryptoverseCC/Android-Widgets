package io.userfeeds.widget

import android.content.Context
import android.content.Intent
import android.net.Uri

internal fun Context.openBrowser(uri: String) {
    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(uri))
    startActivity(intent)
}
