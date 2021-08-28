package com.classroom.classdeck.ui.dashBorad

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.classroom.classdeck.R
import com.classroom.classdeck.databinding.DashBoardFragmentBinding

class DashBoardFragment : Fragment() {



private lateinit var binding:DashBoardFragmentBinding
    private val viewModel: DashBoardViewModel by lazy{
        ViewModelProvider(this).get(DashBoardViewModel::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DashBoardFragmentBinding.inflate(inflater,container,false)
        return binding.root
    }


}