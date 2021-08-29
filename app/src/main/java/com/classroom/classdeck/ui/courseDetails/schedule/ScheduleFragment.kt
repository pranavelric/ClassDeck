package com.classroom.classdeck.ui.courseDetails.schedule

import android.app.Dialog
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.DatePicker
import android.widget.EditText
import android.widget.TimePicker
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.classroom.classdeck.R
import com.classroom.classdeck.adapters.EventAdapter
import com.classroom.classdeck.data.model.Course
import com.classroom.classdeck.data.model.Event
import com.classroom.classdeck.data.model.User
import com.classroom.classdeck.databinding.FragmentScheduleBinding
import com.classroom.classdeck.ui.courseDetails.CourseDetailViewModel
import com.classroom.classdeck.util.*
import com.google.android.material.button.MaterialButton
import com.shrikanthravi.collapsiblecalendarview.widget.CollapsibleCalendar
import dagger.hilt.android.AndroidEntryPoint
import java.text.DateFormatSymbols
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

@AndroidEntryPoint
class ScheduleFragment : Fragment() {


    private lateinit var binding: FragmentScheduleBinding
    private val viewModel: CourseDetailViewModel by lazy {
        ViewModelProvider(this).get(CourseDetailViewModel::class.java)
    }

    @Inject
    lateinit var eventAdapter: EventAdapter

    lateinit var mList: List<Event?>

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
        binding = FragmentScheduleBinding.inflate(inflater, container, false)
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


                    val selectedDate = "${
                        DateFormatSymbols().getShortMonths().get(checkerMonth)
                    } ${checkerDay}, ${checkerYear}"

                    val list = mList
                    val filteredList = list?.filter { event ->
                        selectedDate.equals(event?.eventDate)
                    }

                    eventAdapter.submitList(filteredList)
                    binding.emptyLay.isVisible = filteredList.isEmpty()


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
                showAddClassDialog()
            }
        } else {
            binding.createEventButton.gone()

        }

    }

    private fun observeData() {

        viewModel.addEvents.observe(viewLifecycleOwner, {
            when (it) {
                is ResponseState.Success -> {
                    it.data?.let { it1 -> context?.toast(it1) }

                    course?.courseCode?.let { it1 -> viewModel.getEvents(it1) }
                }
                is ResponseState.Error -> {
                    binding.progressBar.gone()
                    it.message?.let { it1 -> context?.toast(it1) }

                }
                is ResponseState.Loading -> {
                }

            }
        })

        viewModel.getEvents.observe(viewLifecycleOwner, {
            when (it) {
                is ResponseState.Success -> {
                    binding.progressBar.gone()
                    it.data?.let { data ->
                        if (data.isEmpty()) {
                            binding.emptyLay.visible()
                        } else {
                            binding.emptyLay.gone()
                        }

                        eventAdapter.submitList(data)
                        eventAdapter.notifyDataSetChanged()
                        mList = data
                    }
                }
                is ResponseState.Error -> {

                    binding.progressBar.gone()
                    binding.emptyLay.visible()
                    it.message?.let { it1 -> context?.toast(it1) }

                }
                is ResponseState.Loading -> {
                }

            }
        })


    }

    private fun getData() {

        binding.progressBar.visible()
        course?.courseCode?.let { viewModel.getEvents(it) }


    }

    private fun setData() {

        binding.eventRc.apply {
            adapter = eventAdapter
        }


    }


    private fun showAddClassDialog() {
        val dialog = Dialog(requireActivity())
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(false)
        dialog.window?.setLayout(500, WindowManager.LayoutParams.WRAP_CONTENT);
        dialog.setContentView(R.layout.add_event_dialog)


        val dialogButton = dialog.findViewById<MaterialButton>(R.id.create_event_btn)
        val canceButton = dialog.findViewById<MaterialButton>(R.id.cancel)
        val announcementTitle = dialog.findViewById<EditText>(R.id.editTextEventTitle)
        val announcementName = dialog.findViewById<EditText>(R.id.editTextEventMessage)
        val datePicker = dialog.findViewById<DatePicker>(R.id.datePicker)
        val timePicker = dialog.findViewById<TimePicker>(R.id.timePicker)



        canceButton.setOnClickListener { dialog.dismiss() }

        dialogButton.setOnClickListener {

            if (announcementName.text.isNullOrBlank()) {

                announcementName.error = "Message cannot be blank"
            } else if (announcementTitle.text.isNullOrBlank()) {
                announcementTitle.error = "Enter a valid Title"

            } else {

                var hour = 0
                var min = ""
                var eventTime = ""
                var temp = 0
                var eventDate = ""

                if (checkAboveAndroidM()) {
                    hour = timePicker.hour
                    temp = timePicker.hour
                    min = "${timePicker.minute}"
                    eventDate = "${
                        DateFormatSymbols().getShortMonths().get(datePicker.month)
                    } ${datePicker.dayOfMonth}, ${datePicker.year}"

                } else {
                    hour = timePicker.currentHour
                    temp = timePicker.currentHour
                    min = "${timePicker.currentMinute}"
                    eventDate = "${
                        DateFormatSymbols().getShortMonths().get(datePicker.month)
                    } ${datePicker.dayOfMonth}, ${datePicker.year}"
                }

                hour = if (hour > 12) hour % 12 else hour
                val am_pm = if (temp < 12) "AM" else "PM"
                if (min.length == 1) {
                    min = "0" + min
                }
                eventTime = "${hour}:${min} ${am_pm}"

                val event = user?.name?.let { it1 ->
                    course?.courseCode?.let { it2 ->
                        Event(
                            "", announcementTitle.text.toString(), announcementName.text.toString(),
                            getCurrentTime(), getTodaysDate(), it2, eventTime, eventDate,
                            it1
                        )
                    }
                }
                binding.progressBar.visible()
                user?.uid?.let { it1 -> event?.let { it2 -> viewModel.addEvents(it2, it1) } }
                dialog.dismiss()
            }


        }

        dialog.show()

    }


}