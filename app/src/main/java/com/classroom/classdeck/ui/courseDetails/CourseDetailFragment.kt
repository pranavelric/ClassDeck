package com.classroom.classdeck.ui.courseDetails

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.classroom.classdeck.R
import com.classroom.classdeck.data.model.Course
import com.classroom.classdeck.data.model.User
import com.classroom.classdeck.databinding.CourseDetailFragmentBinding
import com.classroom.classdeck.ui.home.HomeViewModel
import com.classroom.classdeck.util.*
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class CourseDetailFragment : Fragment() {


    private lateinit var binding: CourseDetailFragmentBinding

    private val viewModel: CourseDetailViewModel by lazy {
        ViewModelProvider(this).get(CourseDetailViewModel::class.java)
    }

    var course: Course? = null;
    var user: User? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        arguments?.let {
            course = (it.getSerializable(Constants.COURSE_BUNDLE_OBJ) as Course?)

            user = (it.getSerializable(Constants.USERS_BUNDLE_OBJ) as User?)
        }


    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = CourseDetailFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        observeData()
        getData()
        setData()
        setMyClickListeners()
    }

    private fun observeData() {

        viewModel.getCourseFromCode.observe(viewLifecycleOwner, {
            when (it) {
                is ResponseState.Success -> {
                    binding.progressBar.gone()
                    it.data?.let { data ->
                        course = data
                        setData()
                    }
                }
                is ResponseState.Error -> {
                    binding.progressBar.gone()
                    it.message?.let { it1 -> context?.toast(it1) }
                }
                is ResponseState.Loading -> {
                }

            }
        })
    }

    private fun setMyClickListeners() {

        binding.courseAnnouncementCard.setOnClickListener {
            val bundle = Bundle().apply {
                putSerializable(Constants.COURSE_BUNDLE_OBJ, course)
                putSerializable(Constants.USERS_BUNDLE_OBJ, user)
            }

            findNavController().navigate(
                R.id.action_courseDetailFragment_to_announcementsFragment,
                bundle
            )


        }

        binding.courseAssignmentCard.setOnClickListener {
            val bundle = Bundle().apply {
                putSerializable(Constants.COURSE_BUNDLE_OBJ, course)
                putSerializable(Constants.USERS_BUNDLE_OBJ, user)
            }

            findNavController().navigate(
                R.id.action_courseDetailFragment_to_assignmentFragment,
                bundle
            )


        }


    }

    private fun getData() {
        binding.progressBar.visible()
        course?.courseCode?.let { viewModel.getCourseFromCode(it) }
    }

    private fun setData() {

        binding.topLay.courseId.text = course?.courseCode
        binding.topLay.courseName.text = course?.courseName
        binding.topLay.courseProfessor.text = course?.createdBy
        binding.topLay.studentsEnrolled.text = course?.studentCount.toString()

    }


}