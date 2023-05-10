package com.ninele7.tracker.ui.edit.habit

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.ninele7.tracker.R
import com.ninele7.tracker.databinding.EditHabitFragmentBinding
import com.ninele7.tracker.model.HabitPriority
import com.ninele7.tracker.model.HabitType
import com.ninele7.tracker.ui.util.ColorPicker

class EditHabitFragment : Fragment(), EditHabitCallback {
    private val viewModel by viewModels<EditHabitViewModel> {
        EditHabitViewModelFactory
    }
    private lateinit var binding: EditHabitFragmentBinding
    private val args: EditHabitFragmentArgs by navArgs()
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.edit_habit_fragment, container, false)
        val priorities = HabitPropertyArrayAdapter(
            inflater.context,
            R.layout.spinner_dropdown_item,
            HabitPriority.values()
        )
        val prioritySpinner = binding.prioritySpinner
        prioritySpinner.setAdapter(priorities)
        val typeRadio = binding.typeRadioGroup
        val argsUid = args.id
        if (argsUid != null) viewModel.loadHabitById(argsUid)
        prioritySpinner.onItemClickListener =
            AdapterView.OnItemClickListener { _, _, position, _ ->
                viewModel.priorityId.value = position
            }
        typeRadio.setOnCheckedChangeListener { _, id ->
            viewModel.type.value = if (id == R.id.radioGood) HabitType.GOOD else HabitType.BAD
        }
        val colorPicker = ColorPicker(binding.colorPicker, inflater.context)
        colorPicker.setOnSelectColor { color ->
            viewModel.observer.color = color
        }
        binding.button.setOnClickListener { viewModel.onSaveButton(it) }
        viewModel.priorityId.observe(viewLifecycleOwner) {
            if (it != -1)
                binding.prioritySpinner.setText(HabitPriority.values()[it].getName(inflater.context))
        }
        viewModel.type.observe(viewLifecycleOwner) {
            if (it != null)
                typeRadio.check(
                    when (it) {
                        HabitType.GOOD -> R.id.radioGood
                        HabitType.BAD -> R.id.radioBad
                    }
                )
        }
        viewModel.onViewCreated(this)
        binding.viewModel = viewModel
        return binding.root
    }

    override fun onDestroy() {
        viewModel.onViewDestroyed()
        super.onDestroy()
    }

    override fun onSaved() {
        findNavController().popBackStack()
    }

    override fun onError() {
        val activity = activity
        activity?.runOnUiThread {
            Toast.makeText(activity.baseContext, getString(R.string.smth_wrong), Toast.LENGTH_SHORT)
                .show()
        }
    }
}
