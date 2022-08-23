package com.burhanuddin.horizontalcalendarviewlib.utils

import com.burhanuddin.horizontalcalendarviewlib.HorizontalCalendarView
import java.util.*

/**
 * @author Mulham-Raee
 * @since v1.0.0
 */
abstract class HorizontalCalendarListener {
    abstract fun onDateSelected(date: Calendar?, position: Int)
    fun onCalendarScroll(calendarView: HorizontalCalendarView?, dx: Int, dy: Int) {}
    fun onDateLongClicked(date: Calendar?, position: Int): Boolean {
        return false
    }
}