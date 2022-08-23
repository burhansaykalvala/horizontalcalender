package com.burhanuddin.horizontalcalendarviewlib.adapter

import com.burhanuddin.horizontalcalendarviewlib.model.CalendarEvent
import androidx.recyclerview.widget.RecyclerView
import com.burhanuddin.horizontalcalendarviewlib.adapter.EventsAdapter.EventViewHolder
import android.view.ViewGroup
import android.graphics.drawable.Drawable
import android.view.View
import android.widget.ImageView
import androidx.core.content.ContextCompat
import com.burhanuddin.horizontalcalendarviewlib.R
import androidx.core.graphics.drawable.DrawableCompat
import java.lang.IndexOutOfBoundsException
import kotlin.Throws

/**
 * @author Mulham-Raee
 * @since v1.3.2
 */
class EventsAdapter(private var eventList: List<CalendarEvent>) :
    RecyclerView.Adapter<EventViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EventViewHolder {
        val context = parent.context
        val imageView = ImageView(context)
        val circle = ContextCompat.getDrawable(context, R.drawable.ic_circle_white_8dp)
        val drawableWrapper = DrawableCompat.wrap(circle!!)
        imageView.setImageDrawable(drawableWrapper)
        return EventViewHolder(imageView)
    }

    override fun onBindViewHolder(holder: EventViewHolder, position: Int) {
        val event = getItem(position)
        val imageView = holder.itemView as ImageView
        imageView.contentDescription = event.description
        DrawableCompat.setTint(imageView.drawable, event.color)
    }

    @Throws(IndexOutOfBoundsException::class)
    fun getItem(position: Int): CalendarEvent {
        return eventList[position]
    }

    override fun getItemCount(): Int {
        return eventList.size
    }

    fun update(eventList: List<CalendarEvent>) {
        this.eventList = eventList
        notifyDataSetChanged()
    }

    class EventViewHolder(itemView: View?) : RecyclerView.ViewHolder(
        itemView!!
    )
}