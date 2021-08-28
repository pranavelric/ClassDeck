package com.classroom.classdeck.ui.courseDetails.assigments

import android.app.Dialog
import android.os.Bundle
import android.view.*
import android.widget.EditText
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.classroom.classdeck.R
import com.classroom.classdeck.adapters.AnnouncementAdapter
import com.classroom.classdeck.adapters.AssignmentAdapter
import com.classroom.classdeck.data.model.Announcements
import com.classroom.classdeck.data.model.Assignments
import com.classroom.classdeck.data.model.Course
import com.classroom.classdeck.data.model.User
import com.classroom.classdeck.databinding.FragmentAssignmentBinding
import com.classroom.classdeck.ui.courseDetails.CourseDetailViewModel
import com.classroom.classdeck.util.*
import com.google.android.material.button.MaterialButton
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject


@AndroidEntryPoint
class AssignmentFragment : Fragment() {


    private lateinit var binding: FragmentAssignmentBinding
    private val viewModel: CourseDetailViewModel by lazy {
        ViewModelProvider(this).get(CourseDetailViewModel::class.java)
    }

    @Inject
    lateinit var assignmentAdapter: AssignmentAdapter

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
        binding = FragmentAssignmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setData()
        getData()
        observeData()
        setMyClickListeners()
    }


    private fun setMyClickListeners() {

        if (user?.isStudent == false) {
            binding.createAssignButton.visible()
            binding.createAssignButton.setOnClickListener {
                showAddClassDialog()
            }
        } else {
            binding.createAssignButton.gone()

        }

    }

    private fun observeData() {

        viewModel.addAssignment.observe(viewLifecycleOwner, {

            when (it) {
                is ResponseState.Success -> {

                    it.data?.let { it1 -> context?.toast(it1) }

                    course?.courseCode?.let { it1 -> viewModel.getAssignments(it1) }

                }
                is ResponseState.Error -> {

                    binding.progressBar.gone()
                    it.message?.let { it1 -> context?.toast(it1) }

                }
                is ResponseState.Loading -> {
                }


            }

        })

        viewModel.getAssignment.observe(viewLifecycleOwner, {
            when (it) {
                is ResponseState.Success -> {

                    binding.progressBar.gone()

                    it.data?.let { data ->


                        if (data.isEmpty()) {
                            binding.emptyLay.visible()
                        } else {
                            binding.emptyLay.gone()
                        }

                        assignmentAdapter.submitList(data)
                        assignmentAdapter.notifyDataSetChanged()

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

    private fun getData() {

        binding.progressBar.visible()
        course?.courseCode?.let { viewModel.getAssignments(it) }

    }

    private fun setData() {

        binding.assignmentRc.apply {
            adapter = assignmentAdapter
        }


    }


    private fun showAddClassDialog() {
        val dialog = Dialog(requireActivity())
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(false)
        dialog.window?.setLayout(500, WindowManager.LayoutParams.WRAP_CONTENT);
        dialog.setContentView(R.layout.create_assignment_dialog)


        val dialogButton = dialog.findViewById<MaterialButton>(R.id.create_assign_Button)
        val canceButton = dialog.findViewById<MaterialButton>(R.id.cancel)
        val announcementTitle = dialog.findViewById<EditText>(R.id.editTextAssignmentTitle)
        val announcementName = dialog.findViewById<EditText>(R.id.editTextAssignmentMessage)



        canceButton.setOnClickListener { dialog.dismiss() }
        dialogButton.setOnClickListener {

            if (announcementName.text.isNullOrBlank()) {

                announcementName.error = "Message cannot be blank"
            } else if (announcementTitle.text.isNullOrBlank()) {
                announcementTitle.error = "Enter a valid Title"

            } else {


                val announcements = course?.courseCode?.let { it1 ->
                    user?.email?.let { it2 ->
                        Assignments(
                            "", announcementTitle.text.toString(), announcementName.text.toString(),
                            getCurrentTime(),
                            getTodaysDate(), it1, it2
                        )
                    }
                }
                binding.progressBar.visible()
                announcements?.let { it1 -> viewModel.addAssignments(it1) }


                dialog.dismiss()
            }


        }

        dialog.show()

    }


}