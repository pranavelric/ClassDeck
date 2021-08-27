package com.classroom.classdeck.ui.login.selectType

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.classroom.classdeck.R
import com.classroom.classdeck.databinding.FragmentSelectTypeBinding
import com.classroom.classdeck.util.Constants


class SelectTypeFragment : Fragment() {


    private lateinit var binding: FragmentSelectTypeBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSelectTypeBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setMyClickListeners()
    }

    private fun setMyClickListeners() {




        binding.teacherCard.setOnClickListener {
            val bundle = Bundle().apply {
                putSerializable(Constants.USERS_BUNDLE_OBJ, "teacher")
            }
              findNavController().navigate(R.id.action_selectTypeFragment_to_loginFragment, bundle)

        }

        binding.studentCard.setOnClickListener{
            val bundle = Bundle().apply {
                putSerializable(Constants.USERS_BUNDLE_OBJ, "student")
            }
            findNavController().navigate(R.id.action_selectTypeFragment_to_loginFragment, bundle)

        }


    }


}