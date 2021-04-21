package com.ninele7.tracker.ui.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.ninele7.tracker.R
import com.ninele7.tracker.databinding.BottomSheetFragmentBinding

class BottomSheetFragment : Fragment() {
    private lateinit var binding: BottomSheetFragmentBinding
    private val viewModel by activityViewModels<HabitsViewModel> {
        HabitsViewModelFactory
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding =
            DataBindingUtil.inflate(inflater, R.layout.bottom_sheet_fragment, container, false)
        binding.viewModel = viewModel
        binding.lifecycleOwner = this
        binding.sortRadioGroup.setOnCheckedChangeListener {_, id -> viewModel.changeSortOrder(id)}
        return binding.root
    }
}