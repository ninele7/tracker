package com.ninele7.tracker.presentation.ui.edit

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Filter
import android.widget.TextView
import com.ninele7.tracker.R
import com.ninele7.tracker.domain.habit.HabitProperty
import com.ninele7.tracker.presentation.Mappings.getName

class HabitPropertyArrayAdapter<T>(
    context: Context,
    private val layoutId: Int,
    private val items: Array<T>
) :
    ArrayAdapter<T>(context, layoutId, items) where T : HabitProperty {
    private val filter = HabitFilter(this)
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val listItem = convertView ?: LayoutInflater.from(context).inflate(layoutId, parent, false)

        listItem.findViewById<TextView>(R.id.spinner_item_text).text =
            items[position].getName(context)
        return listItem
    }

    override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
        return getView(position, convertView, parent)
    }

    override fun getFilter(): Filter {
        return filter
    }

    private class HabitFilter<T>(private val adapter: HabitPropertyArrayAdapter<T>) :
        Filter() where T : HabitProperty {
        override fun performFiltering(constraint: CharSequence?): FilterResults {
            val results = FilterResults()
            results.values = adapter.items
            results.count = adapter.count
            return results
        }

        override fun publishResults(constraint: CharSequence?, results: FilterResults?) {}

        override fun convertResultToString(resultValue: Any?): CharSequence {
            return when (resultValue) {
                is HabitProperty -> resultValue.getName(adapter.context)
                else -> ""
            }
        }
    }
}