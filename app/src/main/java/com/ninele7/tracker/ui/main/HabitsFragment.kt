package com.ninele7.tracker.ui.main

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.ninele7.tracker.R
import com.ninele7.tracker.model.Habit
import com.ninele7.tracker.model.HabitType

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

    private val viewModel by activityViewModels<HabitsViewModel> {
        HabitsViewModelFactory
    }

    private lateinit var recyclerView: RecyclerView
    private lateinit var activityContext: Context
    private lateinit var currentList: List<Habit>
    private lateinit var adapter: HabitAdapter

    @SuppressLint("NotifyDataSetChanged")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        Log.i("HabitsFragment", "onCreateView")
        val view = inflater.inflate(R.layout.habits_fragment, container, false)
        recyclerView = view.findViewById(R.id.recycler_view)
        recyclerView.addOnItemTouchListener(RecyclerItemClickListener(activityContext, onItemClick))
        adapter = HabitAdapter()
        val correctHabitType = arguments?.getSerializable(FILTER, HabitType::class.java)
        recyclerView.adapter = adapter
        ItemTouchHelper(HabitMoveCallback {
            viewModel.removeHabit(currentList[it].uid) {
                activity?.runOnUiThread {
                    adapter.notifyDataSetChanged()
                }
            }
        }).attachToRecyclerView(recyclerView)

        val refreshLayout = view.findViewById<SwipeRefreshLayout>(R.id.refresh_layout)
        refreshLayout.setOnRefreshListener {
            val refreshJob = viewModel.forceSync()
            refreshJob.invokeOnCompletion {
                activity?.runOnUiThread {
                    refreshLayout.isRefreshing = false
                }
            }
        }
        viewModel.getFilteredHabits(correctHabitType)
            .observe(viewLifecycleOwner) {
                Log.i("HabitsFragment", it.toString())
                Log.i("HabitsFragment", this.hashCode().toString())
                currentList = it
                adapter.submitList(it)
            }

        return view
    }

    private val onItemClick: (View, Int) -> Unit = { _, position ->
        findNavController().navigate(
            MainFragmentDirections.actionHabitsFragmentToEditHabitFragment(
                currentList[position].uid
            )
        )
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        activityContext = context
    }
}
