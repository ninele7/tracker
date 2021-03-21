package com.ninele7.tracker

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.*

data class ColorStore(val color: Int, var selected: Boolean)

class ColorAdapter : ListAdapter<ColorStore, ColorAdapter.ColorViewHolder>(ColorDiffCallback) {
    class ColorViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val colorRect: View = itemView.findViewById(R.id.color_rect)
        fun bind(color: ColorStore) {
            colorRect.setBackgroundColor(color.color)
            colorRect.elevation = if (color.selected) 64f else 16f
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ColorViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.color, parent, false)
        return ColorViewHolder(view)
    }

    override fun onBindViewHolder(holder: ColorViewHolder, position: Int) {
        val element = getItem(position)
        holder.bind(element)
    }
}

class ColorPicker(recyclerView: RecyclerView, context: Context) {
    private val adapter = ColorAdapter()
    private var lastSelectedPosition = 0
    private var selectColorListener: (Int) -> Unit = {}

    private val onItemClick: (View, Int) -> Unit = { _, position ->
        adapter.currentList[lastSelectedPosition].selected = false
        adapter.notifyItemChanged(lastSelectedPosition)
        lastSelectedPosition = position
        adapter.currentList[position].selected = true
        adapter.notifyItemChanged(position)
        selectColorListener(adapter.currentList[position].color)
    }

    init {
        val colors = (1..16).map {
            ColorStore(Color.HSVToColor(floatArrayOf((it * 1f/16 - 1f/32) * 360, 1f, 1f)), false)
        }
        adapter.submitList(colors)
        colors[0].selected = true
        recyclerView.adapter = adapter
        recyclerView.addOnItemTouchListener(RecyclerItemClickListener(context, onItemClick))
    }

    fun setOnSelectColor(l: (Int) -> Unit) {
        selectColorListener = l
    }
}

object ColorDiffCallback : DiffUtil.ItemCallback<ColorStore>() {
    override fun areItemsTheSame(oldItem: ColorStore, newItem: ColorStore): Boolean {
        return oldItem == newItem
    }

    override fun areContentsTheSame(oldItem: ColorStore, newItem: ColorStore): Boolean {
        return oldItem == newItem
    }
}
