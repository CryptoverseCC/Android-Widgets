package io.userfeeds.widget

import android.os.SystemClock
import android.support.test.rule.ActivityTestRule
import org.junit.Rule
import org.junit.Test

class LinksViewPagerStaticTest {

    @Rule @JvmField
    val rule = ActivityTestRule<StaticTestActivity>(StaticTestActivity::class.java)

    @Test
    fun start() {
        SystemClock.sleep(1000)
    }
}
