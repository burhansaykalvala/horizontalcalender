package com.burhanuddin.horizontalcalendarviewlib

import android.content.Context
import androidx.recyclerview.widget.LinearLayoutManager
import com.burhanuddin.horizontalcalendarviewlib.HorizontalLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.LinearSmoothScroller
import android.util.DisplayMetrics

/**
 * @author Mulham-Raee
 * @since  v1.0.0
 *
 * See [HorizontalCalendarView]
 */
class HorizontalLayoutManager internal constructor(context: Context?, reverseLayout: Boolean) :
    LinearLayoutManager(context, HORIZONTAL, reverseLayout) {
    var smoothScrollSpeed = SPEED_NORMAL
    override fun smoothScrollToPosition(
        recyclerView: RecyclerView,
        state: RecyclerView.State,
        position: Int
    ) {
        val smoothScroller: LinearSmoothScroller =
            object : LinearSmoothScroller(recyclerView.context) {
                override fun calculateSpeedPerPixel(displayMetrics: DisplayMetrics): Float {
                    return smoothScrollSpeed / displayMetrics.densityDpi
                }
            }
        smoothScroller.targetPosition = position
        startSmoothScroll(smoothScroller)
    }

    companion object {
        const val SPEED_NORMAL = 90f
        const val SPEED_SLOW = 125f
    }
}