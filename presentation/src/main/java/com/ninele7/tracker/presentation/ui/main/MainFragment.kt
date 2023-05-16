package com.ninele7.tracker.presentation.ui.main

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.LinearLayout
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.navigation.fragment.findNavController
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.ninele7.tracker.R
import com.ninele7.tracker.domain.habit.HabitType
import com.ninele7.tracker.presentation.Mappings.getName
import com.ninele7.tracker.presentation.viewmodel.main.HabitsCallback
import com.ninele7.tracker.presentation.viewmodel.main.HabitsViewModel
import dagger.hilt.android.AndroidEntryPoint

class GoodBadHabitsAdapter(
    fragmentManager: FragmentManager,
    private val context: Context,
    lifecycle: Lifecycle
) : FragmentStateAdapter(fragmentManager, lifecycle) {

    override fun getItemCount(): Int = 3

    override fun createFragment(position: Int): Fragment = when (position) {
        1 -> HabitsFragment.newInstance(HabitType.GOOD)
        2 -> HabitsFragment.newInstance(HabitType.BAD)
        else -> HabitsFragment.newInstance()
    }

    fun getPageTitle(position: Int): CharSequence = when (position) {
        1 -> HabitType.GOOD.getName(context)
        2 -> HabitType.BAD.getName(context)
        else -> context.getString(R.string.all)
    }
}


@AndroidEntryPoint
class MainFragment : Fragment(), HabitsCallback {
    private lateinit var pager: ViewPager2

    private val viewModel by activityViewModels<HabitsViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        Log.i("MainFragment", this.hashCode().toString())
        val view = inflater.inflate(R.layout.main_fragment, container, false)
        view.findViewById<FloatingActionButton>(R.id.fab).setOnClickListener { onFabClick() }
        val bottomSheetBehavior =
            BottomSheetBehavior.from(view.findViewById<LinearLayout>(R.id.bottom_sheet))
        val inputManager =
            (activity?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager?)
        bottomSheetBehavior.addBottomSheetCallback(object :
            BottomSheetBehavior.BottomSheetCallback() {
            override fun onStateChanged(bottomSheet: View, newState: Int) {
                if (newState == BottomSheetBehavior.STATE_COLLAPSED) {
                    viewModel.filterString.value = ""
                    inputManager?.hideSoftInputFromWindow(
                        view.windowToken,
                        0
                    )
                }
            }

            override fun onSlide(bottomSheet: View, slideOffset: Float) {}
        })
        viewModel.onViewCreated(this)
        return view
    }

    private fun onFabClick() {
        findNavController().navigate(
            MainFragmentDirections.actionHabitsFragmentToEditHabitFragment(
                null
            )
        )
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val adapter = GoodBadHabitsAdapter(childFragmentManager, view.context, lifecycle)
        pager = view.findViewById(R.id.view_pager)
        val tabLayout = view.findViewById<TabLayout>(R.id.tab_layout)
        pager.adapter = adapter
        TabLayoutMediator(tabLayout, pager) { tab, position ->
            tab.text = adapter.getPageTitle(position)
        }.attach()
        super.onViewCreated(view, savedInstanceState)
    }

    override fun onResume() {
        pager.currentItem = 0
        super.onResume()
    }

    override fun onError() {
        val activity = activity
        activity?.runOnUiThread {
            Toast.makeText(activity.baseContext, getString(R.string.smth_wrong), Toast.LENGTH_SHORT)
                .show()
        }
    }

    override fun onHabitCompleted(i: Int, type: HabitType) {
        val activity = activity
        activity?.runOnUiThread {
            val toastText = when (type) {
                HabitType.GOOD -> if (i > 0) getString(
                    R.string.should_do_more,
                    i
                ) else getString(R.string.you_great)

                HabitType.BAD -> if (i > 0) getString(
                    R.string.can_do_more,
                    i
                ) else getString(R.string.stop_doing_it)
            }
            Toast.makeText(
                activity.baseContext,
                toastText,
                Toast.LENGTH_SHORT
            ).show()
        }
    }
}
