package com.burhanuddin.horizontalcalendarviewlib.adapter

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import android.widget.TextView
import com.burhanuddin.horizontalcalendarviewlib.R

/**
 * @author Mulham-Raee
 * @since v1.0.0
 */
class DateViewHolder(rootView: View) : RecyclerView.ViewHolder(rootView) {
    @JvmField
    var textTop: TextView
    @JvmField
    var textMiddle: TextView
    @JvmField
    var textBottom: TextView
    @JvmField
    var selectionView: View
    @JvmField
    var layoutContent: View
    @JvmField
    var eventsRecyclerView: RecyclerView

    init {
        textTop = rootView.findViewById(R.id.hc_text_top)
        textMiddle = rootView.findViewById(R.id.hc_text_middle)
        textBottom = rootView.findViewById(R.id.hc_text_bottom)
        layoutContent = rootView.findViewById(R.id.hc_layoutContent)
        selectionView = rootView.findViewById(R.id.hc_selector)
        eventsRecyclerView = rootView.findViewById(R.id.hc_events_recyclerView)
    }
}