package com.ninele7.tracker.ui.main

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import androidx.navigation.fragment.findNavController
import androidx.viewpager.widget.ViewPager
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.tabs.TabLayout
import com.ninele7.tracker.HabitType
import com.ninele7.tracker.R

class GoodBadHabitsAdapter(fragmentManager: FragmentManager, private val context: Context) :
    FragmentPagerAdapter(fragmentManager, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {
    override fun getCount(): Int = 3

    override fun getItem(position: Int): Fragment = when (position) {
        1 -> HabitsFragment.newInstance(HabitType.GOOD)
        2 -> HabitsFragment.newInstance(HabitType.BAD)
        else -> HabitsFragment.newInstance()
    }

    override fun getPageTitle(position: Int): CharSequence = when (position) {
        1 -> HabitType.GOOD.getName(context)
        2 -> HabitType.BAD.getName(context)
        else -> context.getString(R.string.all)
    }
}


class MainFragment : Fragment() {
    private lateinit var pager: ViewPager

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.main_fragment, container, false)
        view.findViewById<FloatingActionButton>(R.id.fab).setOnClickListener { onFabClick() }
        return view
    }

    private fun onFabClick() {
        findNavController().navigate(MainFragmentDirections.actionHabitsFragmentToEditHabitFragment())
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val adapter = GoodBadHabitsAdapter(childFragmentManager, view.context)
        pager = view.findViewById<ViewPager>(R.id.view_pager)
        val tabLayout = view.findViewById<TabLayout>(R.id.tab_layout)
        pager.adapter = adapter
        tabLayout.setupWithViewPager(pager)
        super.onViewCreated(view, savedInstanceState)
    }

    override fun onResume() {
        pager.currentItem = 0
        super.onResume()
    }
}
