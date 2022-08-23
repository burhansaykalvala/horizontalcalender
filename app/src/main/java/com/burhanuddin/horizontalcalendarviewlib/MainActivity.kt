package com.burhanuddin.horizontalcalendarviewlib

import android.graphics.Color
import android.os.Bundle
import android.text.format.DateFormat
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.burhanuddin.horizontalcalendarviewlib.model.CalendarEvent
import com.burhanuddin.horizontalcalendarviewlib.utils.CalendarEventsPredicate
import com.burhanuddin.horizontalcalendarviewlib.utils.HorizontalCalendarListener
import com.google.android.material.floatingactionbutton.FloatingActionButton
import java.util.*


class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        /* start 2 months ago from now */
        /* start 2 months ago from now */
        val startDate = Calendar.getInstance()
        startDate.add(Calendar.MONTH, -2)

        /* end after 2 months from now */

        /* end after 2 months from now */
        val endDate = Calendar.getInstance()
        endDate.add(Calendar.MONTH, 2)

        // Default Date set to Today.

        // Default Date set to Today.
        val defaultSelectedDate = Calendar.getInstance()

        val horizontalCalendar = HorizontalCalendar.Builder(this, R.id.calendarView)
            .range(startDate, endDate)
            .datesNumberOnScreen(7)
            .configure()
            .formatTopText("MMM")
            .formatMiddleText("dd")
            .formatBottomText("EEE")
            .colorTextTop(R.color.purple_200, R.color.purple_500)
            .colorTextMiddle(R.color.teal_200, androidx.appcompat.R.color.material_deep_teal_200)
            .colorTextBottom(
                R.color.black,
                com.google.android.material.R.color.material_deep_teal_200
            )
            .showTopText(true)
            .showBottomText(true)
            .textColor(Color.LTGRAY, Color.MAGENTA)
            .colorTextMiddle(Color.LTGRAY, Color.parseColor("#ffd54f"))
            .end()
            .defaultSelectedDate(defaultSelectedDate)
            .addEvents(object : CalendarEventsPredicate {
                var rnd = Random()
                override fun events(date: Calendar?): List<CalendarEvent>? {
                    val events: MutableList<CalendarEvent> = ArrayList()
                    val count = rnd.nextInt(6)
                    for (i in 0..count) {
                        events.add(
                            CalendarEvent(
                                Color.rgb(
                                    rnd.nextInt(256),
                                    rnd.nextInt(256),
                                    rnd.nextInt(256)
                                ), "event"
                            )
                        )
                    }
                    return events
                }
            })
            .build()

        Log.i("Default Date", DateFormat.format("EEE, MMM d, yyyy", defaultSelectedDate).toString())

        horizontalCalendar.calendarListener = object : HorizontalCalendarListener() {
            override fun onDateSelected(date: Calendar?, position: Int) {
                val selectedDateStr: String = DateFormat.format("EEE, MMM d, yyyy", date).toString()
                Toast.makeText(this@MainActivity, "$selectedDateStr selected!", Toast.LENGTH_SHORT)
                    .show()
                Log.i("onDateSelected", "$selectedDateStr - Position = $position")
            }
        }
/*

        val fab = findViewById<FloatingActionButton>(R.id.fab)
        fab.setOnClickListener(object : OnClickListener() {
            fun onClick(view: View?) {
                horizontalCalendar.goToday(false)
            }
        })
*/


    }
}