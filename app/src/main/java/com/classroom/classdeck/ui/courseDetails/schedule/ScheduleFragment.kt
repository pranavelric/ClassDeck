package com.classroom.classdeck.ui.courseDetails.schedule

import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.classroom.classdeck.data.model.Course
import com.classroom.classdeck.data.model.User
import com.classroom.classdeck.databinding.FragmentScheduleBinding
import com.classroom.classdeck.ui.courseDetails.CourseDetailViewModel
import com.classroom.classdeck.util.*
import com.shrikanthravi.collapsiblecalendarview.widget.CollapsibleCalendar
import java.text.DateFormatSymbols
import java.text.SimpleDateFormat
import java.time.Month
import java.util.*


class ScheduleFragment : Fragment() {



    private lateinit var binding:FragmentScheduleBinding
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
       binding = FragmentScheduleBinding.inflate(inflater,container,false)
        return binding.root
    }






    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setData()
        getData()
        observeData()
        setupCalendar()
        setMyClickListeners()
    }

    private fun setupCalendar() {

        binding.calendarView.setCalendarListener(object : CollapsibleCalendar.CalendarListener {
            override fun onClickListener() {
            }

            override fun onDataUpdate() {
            }

            override fun onDayChanged() {
            }

            override fun onDaySelect() {
                val selectedDay = binding.calendarView.selectedDay
                if (selectedDay != null) {
                    val checkerDay = selectedDay.day
                    val checkerMonth = selectedDay.month
                    val checkerYear = selectedDay.year


                    val selectedDate = "${DateFormatSymbols().getShortMonths().get(checkerMonth)} ${checkerDay}, ${checkerYear}"
                    val format = SimpleDateFormat("MMM dd, yyyy", Locale.US)
                    val date1 = format.parse(selectedDate)
                    Log.d("RRR", "onDaySelect: ${format.format(date1)}")

                }
            }

            override fun onItemClick(v: View) {
            }

            override fun onMonthChange() {
            }

            override fun onWeekChange(position: Int) {
            }
        })
    }

    private fun setMyClickListeners() {

        if (user?.isStudent == false) {
            binding.createEventButton.visible()
            binding.createEventButton.setOnClickListener {
             //   showAddClassDialog()
            }
        } else {
            binding.createEventButton.gone()

        }

    }

    private fun observeData() {


    }

    private fun getData() {

        binding.progressBar.visible()

    }

    private fun setData() {

//        binding.eventRc.apply {
//            adapter = assignmentAdapter
//        }


    }


//    private fun showAddClassDialog() {
//        val dialog = Dialog(requireActivity())
//        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
//        dialog.setCancelable(false)
//        dialog.window?.setLayout(500, WindowManager.LayoutParams.WRAP_CONTENT);
//        dialog.setContentView(R.layout.create_assignment_dialog)
//
//
//        val dialogButton = dialog.findViewById<MaterialButton>(R.id.create_assignment_btn)
//        val canceButton = dialog.findViewById<MaterialButton>(R.id.cancel)
//        val announcementTitle = dialog.findViewById<EditText>(R.id.editTextAssignmentTitle)
//        val announcementName = dialog.findViewById<EditText>(R.id.editTextAssignmentMessage)
//
//
//
//        canceButton.setOnClickListener { dialog.dismiss() }
//
//        dialogButton.setOnClickListener {
//
//            if (announcementName.text.isNullOrBlank()) {
//
//                announcementName.error = "Message cannot be blank"
//            } else if (announcementTitle.text.isNullOrBlank()) {
//                announcementTitle.error = "Enter a valid Title"
//
//            } else {
//
//
//                val announcements = course?.courseCode?.let { it1 ->
//                    user?.email?.let { it2 ->
//                        Assignments(
//                            "", announcementTitle.text.toString(), announcementName.text.toString(),
//                            getCurrentTime(),
//                            getTodaysDate(), it1, it2
//                        )
//                    }
//                }
//                binding.progressBar.visible()
//                announcements?.let { it1 -> viewModel.addAssignments(it1) }
//
//
//                dialog.dismiss()
//            }
//
//
//        }
//
//        dialog.show()
//
//    }
//
//
//




}