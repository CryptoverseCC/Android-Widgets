package io.userfeeds.ads.sdk

import android.os.SystemClock
import android.support.test.rule.ActivityTestRule
import org.junit.Rule
import org.junit.Test

class AdViewTest {

    @Rule @JvmField
    val rule = ActivityTestRule<TestActivity>(TestActivity::class.java)

    @Test
    fun start() {
        SystemClock.sleep(300000)
    }
}
