package io.userfeeds.widget

import android.os.SystemClock
import android.support.test.rule.ActivityTestRule
import org.junit.Rule
import org.junit.Test

class LinksRecyclerViewTest {

    @Rule @JvmField
    val rule = ActivityTestRule<RecyclerViewTestActivity>(RecyclerViewTestActivity::class.java)

    @Test
    fun start() {
        SystemClock.sleep(1000000)
    }
}
