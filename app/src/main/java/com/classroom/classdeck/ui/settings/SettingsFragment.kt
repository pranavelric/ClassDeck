package com.classroom.classdeck.ui.settings

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatDelegate
import com.classroom.classdeck.R
import com.classroom.classdeck.databinding.FragmentSettingsBinding
import com.classroom.classdeck.ui.activity.MainActivity


class SettingsFragment : Fragment() {


    private lateinit var binding: FragmentSettingsBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSettingsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setClickListeners()
        setData()
    }

    private fun setData() {

        binding.nightmodeSwitch.isChecked =
            (activity as MainActivity).sharedPrefrences.getIsNightModeEnabled()


    }

    private fun setClickListeners() {
        binding.nightmodeSwitch.setOnCheckedChangeListener { buttonView, isChecked ->
            setNightMode(isChecked)
        }

    }

    private fun setNightMode(checked: Boolean) {
        if (checked) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            (activity as MainActivity).sharedPrefrences.setNightModeEnabled(true)
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            (activity as MainActivity).sharedPrefrences.setNightModeEnabled(false)
        }
    }

}