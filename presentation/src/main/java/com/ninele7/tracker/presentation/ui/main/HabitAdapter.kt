package com.ninele7.tracker.presentation.ui.main

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.ninele7.tracker.R
import com.ninele7.tracker.databinding.HabitBinding
import com.ninele7.tracker.domain.habit.Habit
import com.ninele7.tracker.presentation.Mappings.getEmoji
import com.ninele7.tracker.presentation.Mappings.getPeriodCardText
import com.ninele7.tracker.presentation.Mappings.getPriorityCardText


class HabitAdapter : ListAdapter<Habit, HabitAdapter.HabitViewHolder>(HabitDiffCallback) {
    class HabitViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val binding: HabitBinding? = DataBindingUtil.bind(itemView)

        fun bind(habit: Habit) {
            if (binding != null) {
                binding.name = habit.name
                binding.description = habit.description
                binding.priorityCardText = habit.getPriorityCardText(itemView.context)
                binding.periodCardText = habit.getPeriodCardText(itemView.context)
                binding.typeEmoji = habit.type.getEmoji()
                binding.color = habit.color
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HabitViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.habit, parent, false)
        return HabitViewHolder(view)
    }

    override fun onBindViewHolder(holder: HabitViewHolder, position: Int) {
        val element = getItem(position)
        holder.bind(element)
    }
}

object HabitDiffCallback : DiffUtil.ItemCallback<Habit>() {
    override fun areItemsTheSame(oldItem: Habit, newItem: Habit): Boolean {
        return oldItem.updated == newItem.updated
    }

    override fun areContentsTheSame(oldItem: Habit, newItem: Habit): Boolean {
        return oldItem == newItem
    }
}

class HabitMoveCallback(private val swipedCallBack: (Int) -> Unit) :
    ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT) {
    override fun onMove(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        target: RecyclerView.ViewHolder
    ): Boolean = false

    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
        val position = viewHolder.adapterPosition
        swipedCallBack(position)
    }
}