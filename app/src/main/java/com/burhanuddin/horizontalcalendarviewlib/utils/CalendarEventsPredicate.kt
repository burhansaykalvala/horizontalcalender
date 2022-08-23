package com.burhanuddin.horizontalcalendarviewlib.utils

import com.burhanuddin.horizontalcalendarviewlib.model.CalendarEvent
import java.util.*

/**
 * @author Mulham-Raee
 * @since v1.3.2
 */
interface CalendarEventsPredicate {
    /**
     * @param date the date where the events will be attached to.
     * @return a list of [CalendarEvent] related to this date.
     */
    fun events(date: Calendar?): List<CalendarEvent>?
}