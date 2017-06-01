package io.userfeeds.ads.sdk

import android.app.Activity
import android.os.Bundle
import io.userfeeds.sdk.core.UserfeedsSdk

class TestActivity : Activity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(AdView(this))
    }
}
