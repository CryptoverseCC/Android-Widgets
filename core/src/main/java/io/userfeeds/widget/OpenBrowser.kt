package io.userfeeds.widget

import android.content.Context
import android.content.Intent
import android.net.Uri

internal fun Context.openBrowser(uri: Uri) {
    val intent = Intent(Intent.ACTION_VIEW, uri)
    startActivity(intent)
}
