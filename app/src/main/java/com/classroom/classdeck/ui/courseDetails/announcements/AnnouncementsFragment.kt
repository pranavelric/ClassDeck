package com.classroom.classdeck.ui.courseDetails.announcements

import android.app.Dialog
import android.content.res.Configuration
import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import android.widget.EditText
import androidx.lifecycle.ViewModelProvider
import com.classroom.classdeck.R
import com.classroom.classdeck.adapters.AnnouncementAdapter
import com.classroom.classdeck.data.model.Announcements
import com.classroom.classdeck.data.model.Course
import com.classroom.classdeck.data.model.User
import com.classroom.classdeck.databinding.FragmentAnnouncementsBinding
import com.classroom.classdeck.ui.courseDetails.CourseDetailViewModel
import com.classroom.classdeck.util.*
import com.google.android.material.button.MaterialButton
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class AnnouncementsFragment : Fragment() {


    private lateinit var binding: FragmentAnnouncementsBinding
    private val viewModel: CourseDetailViewModel by lazy {
        ViewModelProvider(this).get(CourseDetailViewModel::class.java)
    }

    @Inject
    lateinit var announcementAdapter: AnnouncementAdapter

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
        binding = FragmentAnnouncementsBinding.inflate(inflater, container, false)
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
            binding.createAnnounceButton.visible()
            binding.createAnnounceButton.setOnClickListener {
                showAddClassDialog()
            }
        } else {
            binding.createAnnounceButton.gone()

        }

    }

    private fun observeData() {

        viewModel.addAnnouncement.observe(viewLifecycleOwner, {

            when (it) {
                is ResponseState.Success -> {

                    it.data?.let { it1 -> context?.toast(it1) }

                    course?.courseCode?.let { it1 -> viewModel.getAnnouncements(it1) }

                }
                is ResponseState.Error -> {

                    binding.progressBar.gone()
                    it.message?.let { it1 -> context?.toast(it1) }

                }
                is ResponseState.Loading -> {
                }


            }

        })

        viewModel.getAnnouncement.observe(viewLifecycleOwner, {
            when (it) {
                is ResponseState.Success -> {

                    binding.progressBar.gone()

                    it.data?.let { data ->


                        if (data.isEmpty()) {
                            binding.emptyLay.visible()
                        } else {
                            binding.emptyLay.gone()
                        }

                        announcementAdapter.submitList(data)
                        announcementAdapter.notifyDataSetChanged()

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
        course?.courseCode?.let { viewModel.getAnnouncements(it) }

    }

    private fun setData() {

        binding.announcementRc.apply {
            adapter = announcementAdapter
        }


    }


    private fun showAddClassDialog() {
        val dialog = Dialog(requireActivity())
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(false)
        dialog.window?.setLayout(500, WindowManager.LayoutParams.WRAP_CONTENT);
        dialog.setContentView(R.layout.create_announcement_dialog)


        val dialogButton = dialog.findViewById<MaterialButton>(R.id.create_announcement_btn)
        val canceButton = dialog.findViewById<MaterialButton>(R.id.cancel)
        val announcementTitle = dialog.findViewById<EditText>(R.id.editTextAnnouncementTitle)
        val announcementName = dialog.findViewById<EditText>(R.id.editTextAnnouncementMessage)



        canceButton.setOnClickListener { dialog.dismiss() }
        dialogButton.setOnClickListener {

            if (announcementName.text.isNullOrBlank()) {

                announcementName.error = "Message cannot be blank"
            } else if (announcementTitle.text.isNullOrBlank()) {
                announcementTitle.error = "Enter a valid Title"

            } else {


                val announcements = course?.courseCode?.let { it1 ->
                    Announcements(
                        "", announcementTitle.text.toString(), announcementName.text.toString(),
                        getCurrentTime(),
                        getTodaysDate(), it1
                    )
                }
                binding.progressBar.visible()
                announcements?.let { it1 -> viewModel.addAnnouncements(it1) }


                dialog.dismiss()
            }


        }

        dialog.show()

    }


}