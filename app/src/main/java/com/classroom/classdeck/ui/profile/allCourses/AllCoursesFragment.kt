package com.classroom.classdeck.ui.profile.allCourses

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import com.classroom.classdeck.adapters.CourseAdapter
import com.classroom.classdeck.data.model.User
import com.classroom.classdeck.databinding.FragmentAllCoursesBinding
import com.classroom.classdeck.ui.dashBorad.DashBoardViewModel
import com.classroom.classdeck.util.*
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class AllCoursesFragment : Fragment() {


    @Inject
    lateinit var courseAdapter: CourseAdapter



    private var user: User? = null

    private val viewModel: DashBoardViewModel by lazy {
        ViewModelProvider(this).get(DashBoardViewModel::class.java)
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        arguments?.let {
            user = it.getSerializable(Constants.USERS_BUNDLE_OBJ) as User?
        }
    }

    private lateinit var binding: FragmentAllCoursesBinding


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentAllCoursesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        getData()
        setData()
        observeData()
        setMyClickListeners()
    }


    private fun setMyClickListeners() {




    }

    private fun observeData() {


        viewModel.getAllCourse.observe(viewLifecycleOwner, {
            when (it) {
                is ResponseState.Success -> {
                    binding.progressBar.gone()
                    it.data?.let { data ->

                        if (data.isEmpty()) {

                            binding.emptyLay.visible()
                        } else {
                            binding.emptyLay.gone()
                        }

                      courseAdapter.submitList(data)
                    }

                }
                is ResponseState.Error -> {
                    binding.emptyLay.visible()
                    binding.progressBar.gone()
                    it.message?.let { it1 -> context?.toast(it1) }
                }
                is ResponseState.Loading -> {
                }

            }
        })


    }

    private fun setData() {

        binding.coursesRc.apply { adapter = courseAdapter }

    }

    private fun getData() {

        binding.progressBar.visible()

        viewModel.getAllCourse()


    }


}