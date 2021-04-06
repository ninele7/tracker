package com.ninele7.tracker.ui.main

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.ninele7.tracker.*

class HabitsFragment : Fragment() {

    companion object {
        private const val FILTER = "FILTER"
        fun newInstance() = HabitsFragment()
        fun newInstance(filter: HabitType): HabitsFragment {
            val args = Bundle()
            args.putSerializable(FILTER, filter)
            val fragment = HabitsFragment()
            fragment.arguments = args
            return fragment
        }
    }

    private val viewModel by viewModels<HabitsViewModel> {
        HabitsViewModelFactory
    }

    private lateinit var recyclerView: RecyclerView
    private lateinit var activityContext: Context
    private lateinit var currentList: List<Habit>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.habits_fragment, container, false)
        recyclerView = view.findViewById(R.id.recycler_view)
        recyclerView.addOnItemTouchListener(RecyclerItemClickListener(activityContext, onItemClick))
        val adapter = HabitAdapter()
        val correctHabitType = arguments?.getSerializable(FILTER) as HabitType?
        recyclerView.adapter = adapter
        viewModel.habitListLiveData.observe(viewLifecycleOwner, {
            currentList =
                (if (correctHabitType == null) it else it.filter { habit -> habit.type == correctHabitType })

            adapter.submitList(currentList)
        })
        ItemTouchHelper(HabitMoveCallback(adapter, viewModel)).attachToRecyclerView(
            recyclerView
        )
        return view
    }

    private val onItemClick: (View, Int) -> Unit = { _, position ->
        findNavController().navigate(
            MainFragmentDirections.actionHabitsFragmentToEditHabitFragment(
                currentList[position].id
            )
        )
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        activityContext = context
    }
}
