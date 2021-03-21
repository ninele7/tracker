package com.ninele7.tracker

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView


class MainActivity : AppCompatActivity() {
    private val viewModel by viewModels<HabitsViewModel> {
        HabitsViewModelFactory
    }
    private val newHabitActivityRequestCode = 1
    private val editHabitActivityRequestCode = 2
    private lateinit var recyclerView: RecyclerView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(findViewById(R.id.toolbar))
        recyclerView = findViewById(R.id.recycler_view)
        recyclerView.adapter = viewModel.habitAdapter
        viewModel.habitAdapter.submitList(viewModel.habitList)
        ItemTouchHelper(HabitMoveCallback(viewModel.habitAdapter, viewModel)).attachToRecyclerView(
            recyclerView
        )
        recyclerView.addOnItemTouchListener(RecyclerItemClickListener(this, onItemClick))
    }

    fun onFabClick(view: View) {
        startActivityForResult(
            Intent(this, EditHabitActivity::class.java),
            newHabitActivityRequestCode
        )
    }

    private val onItemClick: (View, Int) -> Unit = { _, position ->
        viewModel.lastEditedHabit = position
        startActivityForResult(
            Intent(this, EditHabitActivity::class.java).putExtra(
                HABIT_INTENT_VALUE,
                viewModel.habitList[position]
            ),
            editHabitActivityRequestCode
        )
    }

    private fun extractHabitFromIntent(data: Intent): Habit? {
        val habit = data.getSerializableExtra(HABIT_INTENT_VALUE)
        return if (habit is Habit) habit else null
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == newHabitActivityRequestCode && resultCode == Activity.RESULT_OK && data != null) {
            val habit = extractHabitFromIntent(data)
            if (habit != null)
                viewModel.habitList.add(habit)
            viewModel.habitAdapter.notifyDataSetChanged()
            recyclerView.scrollToPosition(viewModel.habitList.size - 1)
            return
        }
        if (requestCode == editHabitActivityRequestCode && resultCode == Activity.RESULT_OK && data != null) {
            val habit = extractHabitFromIntent(data)
            if (habit != null)
                viewModel.habitList[viewModel.lastEditedHabit] = habit
            viewModel.habitAdapter.notifyDataSetChanged()
            return
        }
    }
}
