package com.burhanuddin.horizontalcalendarviewlib

import android.annotation.TargetApi
import android.app.Activity
import android.content.Context
import android.graphics.Color
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.burhanuddin.horizontalcalendarviewlib.adapter.DaysAdapter
import com.burhanuddin.horizontalcalendarviewlib.adapter.HorizontalCalendarBaseAdapter
import com.burhanuddin.horizontalcalendarviewlib.adapter.MonthsAdapter
import com.burhanuddin.horizontalcalendarviewlib.model.CalendarItemStyle
import com.burhanuddin.horizontalcalendarviewlib.model.HorizontalCalendarConfig
import com.burhanuddin.horizontalcalendarviewlib.utils.CalendarEventsPredicate
import com.burhanuddin.horizontalcalendarviewlib.utils.HorizontalCalendarListener
import com.burhanuddin.horizontalcalendarviewlib.utils.HorizontalCalendarPredicate
import com.burhanuddin.horizontalcalendarviewlib.utils.HorizontalSnapHelper
import com.burhanuddin.horizontalcalendarviewlib.utils.Utils.calculateRelativeCenterPosition
import com.burhanuddin.horizontalcalendarviewlib.utils.Utils.daysBetween
import com.burhanuddin.horizontalcalendarviewlib.utils.Utils.isDateAfter
import com.burhanuddin.horizontalcalendarviewlib.utils.Utils.isDateBefore
import com.burhanuddin.horizontalcalendarviewlib.utils.Utils.isSameDate
import com.burhanuddin.horizontalcalendarviewlib.utils.Utils.isSameMonth
import com.burhanuddin.horizontalcalendarviewlib.utils.Utils.monthsBetween
import java.util.*

/**
 * See [HorizontalCalendarView]
 *
 * @author Mulham-Raee
 * @see HorizontalCalendarListener
 *
 * @since v1.0.0
 */
class HorizontalCalendar internal constructor(
    builder: Builder,
    config: HorizontalCalendarConfig,
    defaultStyle: CalendarItemStyle,
    selectedItemStyle: CalendarItemStyle
) {
    enum class Mode {
        DAYS, MONTHS
    }

    //region private Fields
    var calendarView: HorizontalCalendarView? = null
    private var mCalendarAdapter: HorizontalCalendarBaseAdapter<*, *>? = null

    // Start & End Dates
    var startDate: Calendar? = null
    var endDate: Calendar? = null

    // Calendar Mode
    private val mode: Mode?

    // Number of Dates to Show on Screen
    val numberOfDatesOnScreen: Int

    // Interface events
    var calendarListener: HorizontalCalendarListener? = null
    private val calendarId: Int

    /* Format, Colors & Font Sizes*/
    val defaultStyle: CalendarItemStyle
    val selectedItemStyle: CalendarItemStyle
    val config: HorizontalCalendarConfig

    /* Init Calendar View */
    fun init(
        rootView: View,
        defaultSelectedDate: Calendar?,
        disablePredicate: HorizontalCalendarPredicate?,
        eventsPredicate: CalendarEventsPredicate?
    ) {
        var disablePredicate = disablePredicate
        calendarView = rootView.findViewById(calendarId)
        calendarView!!.setHasFixedSize(true)
        calendarView!!.isHorizontalScrollBarEnabled = false
        calendarView!!.applyConfigFromLayout(this)
        val snapHelper = HorizontalSnapHelper()
        snapHelper.attachToHorizontalCalendar(this)
        disablePredicate = if (disablePredicate == null) {
            defaultDisablePredicate
        } else {
            HorizontalCalendarPredicate.Or(
                disablePredicate,
                defaultDisablePredicate
            )
        }
        mCalendarAdapter = if (mode == Mode.MONTHS) {
            MonthsAdapter(this, startDate, endDate, disablePredicate, eventsPredicate)
        } else {
            DaysAdapter(this, startDate!!, endDate!!, disablePredicate, eventsPredicate)
        }
        calendarView!!.setAdapter(mCalendarAdapter)
        calendarView!!.setLayoutManager(HorizontalLayoutManager(calendarView!!.getContext(), false))
        calendarView!!.addOnScrollListener(HorizontalCalendarScrollListener())
        post { centerToPositionWithNoAnimation(positionOfDate(defaultSelectedDate)) }
    }

    /**
     * Select today date and center the Horizontal Calendar to this date
     *
     * @param immediate pass true to make the calendar scroll as fast as possible to reach the date of today
     * ,or false to play default scroll animation speed.
     */
    fun goToday(immediate: Boolean) {
        selectDate(Calendar.getInstance(), immediate)
    }

    /**
     * Select the date and center the Horizontal Calendar to this date
     *
     * @param date      The date to select
     * @param immediate pass true to make the calendar scroll as fast as possible to reach the target date
     * ,or false to play default scroll animation speed.
     */
    fun selectDate(date: Calendar?, immediate: Boolean) {
        val datePosition = positionOfDate(date)
        if (immediate) {
            centerToPositionWithNoAnimation(datePosition)
            if (calendarListener != null) {
                calendarListener!!.onDateSelected(date, datePosition)
            }
        } else {
            calendarView!!.smoothScrollSpeed = HorizontalLayoutManager.SPEED_NORMAL
            centerCalendarToPosition(datePosition)
        }
    }

    /**
     * Smooth scroll Horizontal Calendar to center this position and select the new centered day.
     *
     * @param position The position to center the calendar to!
     */
    fun centerCalendarToPosition(position: Int) {
        if (position != -1) {
            val relativeCenterPosition = calculateRelativeCenterPosition(
                position,
                calendarView!!.positionOfCenterItem,
                numberOfDatesOnScreen / 2
            )
            if (relativeCenterPosition == position) {
                return
            }
            calendarView!!.smoothScrollToPosition(relativeCenterPosition)
        }
    }

    /**
     * Scroll Horizontal Calendar to center this position and select the new centered day.
     *
     * @param position The position to center the calendar to!
     */
    fun centerToPositionWithNoAnimation(position: Int) {
        if (position != -1) {
            val oldSelectedItem = calendarView!!.positionOfCenterItem
            val relativeCenterPosition = calculateRelativeCenterPosition(
                position,
                oldSelectedItem,
                numberOfDatesOnScreen / 2
            )
            if (relativeCenterPosition == position) {
                return
            }
            calendarView!!.scrollToPosition(relativeCenterPosition)
            calendarView!!.post {
                val newSelectedItem = calendarView!!.positionOfCenterItem
                //refresh to update background colors
                refreshItemsSelector(newSelectedItem, oldSelectedItem)
            }
        }
    }

    fun refreshItemsSelector(position1: Int, vararg positions: Int) {
        mCalendarAdapter!!.notifyItemChanged(position1, "UPDATE_SELECTOR")
        if (positions != null && positions.size > 0) {
            for (pos in positions) {
                mCalendarAdapter!!.notifyItemChanged(pos, "UPDATE_SELECTOR")
            }
        }
    }

    fun isItemDisabled(position: Int): Boolean {
        return mCalendarAdapter!!.isDisabled(position)
    }

    fun refresh() {
        mCalendarAdapter!!.notifyDataSetChanged()
    }

    fun show() {
        calendarView!!.visibility = View.VISIBLE
    }

    fun hide() {
        calendarView!!.visibility = View.INVISIBLE
    }

    fun post(runnable: Runnable?) {
        calendarView!!.post(runnable)
    }

    @TargetApi(21)
    fun setElevation(elevation: Float) {
        calendarView!!.elevation = elevation
    }

    /**
     * @return the current selected date
     */
    val selectedDate: Calendar
        get() = mCalendarAdapter!!.getItem(calendarView!!.positionOfCenterItem)!!

    /**
     * @return position of selected date in Horizontal Calendar
     */
    val selectedDatePosition: Int
        get() = calendarView!!.positionOfCenterItem

    /**
     * @param position The position of date
     * @return the date on this index
     * @throws IndexOutOfBoundsException if position is out of the calendar range
     */
    @Throws(IndexOutOfBoundsException::class)
    fun getDateAt(position: Int): Calendar {
        return mCalendarAdapter!!.getItem(position)!!
    }

    /**
     * @param date The date to search for
     * @return true if the calendar contains this date or false otherwise
     */
    operator fun contains(date: Calendar?): Boolean {
        return positionOfDate(date) != -1
    }

    val context: Context
        get() = calendarView!!.context

    fun setRange(startDate: Calendar, endDate: Calendar) {
        this.startDate = startDate
        this.endDate = endDate
        mCalendarAdapter!!.update(startDate, endDate, false)
    }

    val shiftCells: Int
        get() = numberOfDatesOnScreen / 2

    /**
     * @return position of date in Calendar, or -1 if date does not exist
     */
    fun positionOfDate(date: Calendar?): Int {
        if (isDateBefore(date!!, startDate!!) || isDateAfter(
                date, endDate!!
            )
        ) {
            return -1
        }
        val position: Int
        position = if (mode == Mode.DAYS) {
            if (isSameDate(date, startDate!!)) {
                0
            } else {
                daysBetween(startDate!!, date)
            }
        } else {
            if (isSameMonth(date, startDate!!)) {
                0
            } else {
                monthsBetween(startDate!!, date)
            }
        }
        val shiftCells = numberOfDatesOnScreen / 2
        return position + shiftCells
    }

    class Builder {
        val viewId: Int
        val rootView: View

        // Start & End Dates
        var startDate: Calendar? = null
        var endDate: Calendar? = null
        var defaultSelectedDate: Calendar? = null
        var mode: Mode? = null

        // Number of Days to Show on Screen
        var numberOfDatesOnScreen = 0

        // Specified which dates should be disabled
        private var disablePredicate: HorizontalCalendarPredicate? = null

        // Add events to each Date
        private var eventsPredicate: CalendarEventsPredicate? = null
        private var configBuilder: ConfigBuilder? = null

        /**
         * @param rootView pass the rootView for the Fragment where HorizontalCalendar is attached
         * @param viewId   the id specified for HorizontalCalendarView in your layout
         */
        constructor(rootView: View, viewId: Int) {
            this.rootView = rootView
            this.viewId = viewId
        }

        /**
         * @param activity pass the activity where HorizontalCalendar is attached
         * @param viewId   the id specified for HorizontalCalendarView in your layout
         */
        constructor(activity: Activity, viewId: Int) {
            rootView = activity.window.decorView
            this.viewId = viewId
        }

        fun range(startDate: Calendar?, endDate: Calendar?): Builder {
            this.startDate = startDate
            this.endDate = endDate
            return this
        }

        fun mode(mode: Mode?): Builder {
            this.mode = mode
            return this
        }

        fun datesNumberOnScreen(numberOfItemsOnScreen: Int): Builder {
            numberOfDatesOnScreen = numberOfItemsOnScreen
            return this
        }

        fun defaultSelectedDate(date: Calendar?): Builder {
            defaultSelectedDate = date
            return this
        }

        fun disableDates(predicate: HorizontalCalendarPredicate?): Builder {
            disablePredicate = predicate
            return this
        }

        fun addEvents(predicate: CalendarEventsPredicate?): Builder {
            eventsPredicate = predicate
            return this
        }

        fun configure(): ConfigBuilder {
            if (configBuilder == null) {
                configBuilder = ConfigBuilder(this)
            }
            return configBuilder!!
        }

        @Throws(IllegalStateException::class)
        private fun initDefaultValues() {
            /* Defaults variables */
            check(!(startDate == null || endDate == null)) { "HorizontalCalendar range was not specified, either startDate or endDate is null!" }
            if (mode == null) {
                mode = Mode.DAYS
            }
            if (numberOfDatesOnScreen <= 0) {
                numberOfDatesOnScreen = 5
            }
            if (defaultSelectedDate == null) {
                defaultSelectedDate = Calendar.getInstance()
            }
        }

        /**
         * @return Instance of [HorizontalCalendar] initiated with builder settings
         */
        @Throws(IllegalStateException::class)
        fun build(): HorizontalCalendar {
            initDefaultValues()
            if (configBuilder == null) {
                configBuilder = ConfigBuilder(this)
                configBuilder!!.end()
            }
            val defaultStyle = configBuilder!!.createDefaultStyle()
            val selectedItemStyle = configBuilder!!.createSelectedItemStyle()
            val config = configBuilder!!.createConfig()
            val horizontalCalendar =
                HorizontalCalendar(this, config, defaultStyle, selectedItemStyle)
            horizontalCalendar.init(
                rootView,
                defaultSelectedDate,
                disablePredicate,
                eventsPredicate
            )
            return horizontalCalendar
        }
    }

    private val defaultDisablePredicate: HorizontalCalendarPredicate =
        object : HorizontalCalendarPredicate {
            override fun test(date: Calendar?): Boolean {
                return isDateBefore(date!!, startDate!!) || isDateAfter(
                    date, endDate!!
                )
            }

            override fun style(): CalendarItemStyle {
                return CalendarItemStyle(Color.GRAY, null)
            }
        }

    private inner class HorizontalCalendarScrollListener internal constructor() :
        RecyclerView.OnScrollListener() {
        var lastSelectedItem = -1
        val selectedItemRefresher: Runnable = SelectedItemRefresher()
        override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
            //On Scroll, agenda is refresh to update background colors
            post(selectedItemRefresher)
            if (calendarListener != null) {
                calendarListener!!.onCalendarScroll(calendarView, dx, dy)
            }
        }

        private inner class SelectedItemRefresher internal constructor() : Runnable {
            override fun run() {
                val positionOfCenterItem = calendarView!!.positionOfCenterItem
                if (lastSelectedItem == -1 || lastSelectedItem != positionOfCenterItem) {
                    //On Scroll, agenda is refresh to update background colors
                    refreshItemsSelector(positionOfCenterItem)
                    if (lastSelectedItem != -1) {
                        refreshItemsSelector(lastSelectedItem)
                    }
                    lastSelectedItem = positionOfCenterItem
                }
            }
        }
    }
    //endregion
    /**
     * Private Constructor to insure HorizontalCalendar can't be initiated the default way
     */
    init {
        numberOfDatesOnScreen = builder.numberOfDatesOnScreen
        calendarId = builder.viewId
        startDate = builder.startDate
        endDate = builder.endDate
        this.config = config
        this.defaultStyle = defaultStyle
        this.selectedItemStyle = selectedItemStyle
        mode = builder.mode
    }
}