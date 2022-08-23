package com.burhanuddin.horizontalcalendarviewlib.utils

import com.burhanuddin.horizontalcalendarviewlib.model.CalendarItemStyle
import com.burhanuddin.horizontalcalendarviewlib.utils.HorizontalCalendarPredicate
import java.util.*

/**
 * @author Mulham-Raee
 * @since v1.2.5
 */
interface HorizontalCalendarPredicate {
    fun test(date: Calendar?): Boolean
    fun style(): CalendarItemStyle
    class Or(
        private val firstPredicate: HorizontalCalendarPredicate,
        private val secondPredicate: HorizontalCalendarPredicate
    ) : HorizontalCalendarPredicate {
        override fun test(date: Calendar?): Boolean {
            return firstPredicate.test(date) || secondPredicate.test(date)
        }

        override fun style(): CalendarItemStyle {
            return firstPredicate.style()
        }
    }
}