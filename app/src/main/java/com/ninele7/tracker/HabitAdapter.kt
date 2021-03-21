package com.ninele7.tracker

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.ninele7.tracker.databinding.HabitBinding


class HabitAdapter : ListAdapter<Habit, HabitAdapter.HabitViewHolder>(HabitDiffCallback) {
    class HabitViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val binding: HabitBinding? = DataBindingUtil.bind(itemView)

        fun bind(habit: Habit) {
            if (binding != null) {
                binding.habit = habit
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
        return oldItem == newItem
    }

    override fun areContentsTheSame(oldItem: Habit, newItem: Habit): Boolean {
        return oldItem == newItem
    }
}

class HabitMoveCallback(private val adapter: HabitAdapter, private val model: HabitsViewModel) :
    ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT) {
    override fun onMove(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        target: RecyclerView.ViewHolder
    ): Boolean = false

    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
        val position = viewHolder.adapterPosition
        model.habitList.removeAt(position)
        adapter.notifyDataSetChanged()
    }
}

