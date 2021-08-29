package com.classroom.classdeck.ui.courseDetails.tests.createTest

import android.os.Build
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.classroom.classdeck.data.model.Course
import com.classroom.classdeck.data.model.Quiz
import com.classroom.classdeck.data.model.User
import com.classroom.classdeck.databinding.FragmentCreateQuizBinding
import com.classroom.classdeck.ui.courseDetails.tests.QuizViewModel
import com.classroom.classdeck.util.*
import com.thecode.aestheticdialogs.*
import java.text.SimpleDateFormat
import java.util.*
import com.classroom.classdeck.R
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class CreateQuizFragment : Fragment() {


    private lateinit var binding: FragmentCreateQuizBinding
    private val viewModel: QuizViewModel by lazy {
        ViewModelProvider(this).get(QuizViewModel::class.java)
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
        binding = FragmentCreateQuizBinding.inflate(inflater, container, false)
        return binding.root
    }


    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        observeData()
        setData()
        setClickListeners()
    }

    private fun observeData() {

        viewModel.createQuizStatus.observe(viewLifecycleOwner, {
            when (it) {
                is ResponseState.Success -> {
                    binding.progressBar.gone()

                    val data: Quiz? = it.data

                    activity?.let { it1 ->
                        AestheticDialog.Builder(it1, DialogStyle.FLAT, DialogType.SUCCESS)
                            .setTitle("Quiz created")
                            .setMessage("Quiz created, Press ok to add details to this quiz")
                            .setCancelable(false)
                            .setDarkMode(false)
                            .setGravity(Gravity.CENTER)
                            .setAnimation(DialogAnimation.FADE)
                            .setOnClickListener(object : OnDialogClickListener {
                                override fun onClick(dialog: AestheticDialog.Builder) {

                                    val bundle = Bundle().apply {

                                        putSerializable(Constants.QUIZ_BUNDLE_OBJ, data)
                                        putSerializable(Constants.COURSE_BUNDLE_OBJ, course)
                                        putSerializable(Constants.USERS_BUNDLE_OBJ, user)

                                    }
                                    findNavController().navigate(
                                        R.id.action_createQuizFragment_to_addQuestionsFragment,
                                        bundle
                                    )


                                    dialog.dismiss()
                                }
                            })
                            .show()
                    }

                }
                is ResponseState.Loading -> {
                }
                is ResponseState.Error -> {
                    binding.progressBar.gone()
                    it.message?.let { it1 -> binding.root.snackbar(it1) }
                }

            }
        })

    }


    @RequiresApi(Build.VERSION_CODES.O)
    private fun setData() {
        var startTime: String = ""
        var startDate: String = ""

        binding.timePicker.setIs24HourView(false)

        binding.timePicker.setOnTimeChangedListener { view, hourOfDay, minute ->
            var hour = hourOfDay
            hour = if (hour > 12) hour % 12 else hour
            val am_pm = if (hourOfDay < 12) "AM" else "PM"

            var min = ""
            if (minute.toString().length == 1) {
                min = "0" + minute
            }


            startTime = "${hour}:${min} ${am_pm}"
        }



        binding.datePicker.minDate = System.currentTimeMillis() - 1000
        binding.datePicker.setOnDateChangedListener { view, year, monthOfYear, dayOfMonth ->
            val year: Int = year
            val month: Int = monthOfYear
            val day: Int = dayOfMonth

            val calendar: Calendar = Calendar.getInstance()
            calendar.set(year, month, day)

            val format = SimpleDateFormat("MMM dd, yyyy")
            val strDate: String = format.format(calendar.getTime())
            startDate = strDate

        }


        val contest = Quiz()
        contest.date = getTodaysDate()
        contest.time = getCurrentTime()


        binding.createGame.setOnClickListener {

            if (startTime == "")
                startTime = getCurrentTime()
            if (startDate == "")
                startDate = getTodaysDate()

            contest.time = getCurrentTime()
            contest.startTime = startTime
            contest.startDate = startDate
            contest.CourseName = course?.courseName.toString()
            contest.courseCode = course?.courseCode.toString()





            if (!binding.editTextNumberOfQuestions.text.isNullOrBlank()) {

                contest.question_numbers = binding.editTextNumberOfQuestions.text.toString().toInt()

            } else {
                binding.editTextNumberOfQuestions.error = "Pleaase fill valid amount"
                return@setOnClickListener
            }


            if (!binding.editTextQuestionTime.text.isNullOrBlank()) {
                contest.answerTime = binding.editTextQuestionTime.text.toString().toInt()
            } else {
                binding.editTextQuestionTime.error = "Pleaase fill valid amount"
                return@setOnClickListener
            }


            if (!binding.editTextQuizTitle.text.isNullOrBlank()) {
                contest.quizTitle = binding.editTextQuizTitle.text.toString()
            } else {
                binding.editTextQuizTitle.error = "Pleaase fill valid amount"
                return@setOnClickListener
            }

            if (!binding.editTextQuizMarks.text.isNullOrBlank()) {
                contest.marks = binding.editTextQuizMarks.text.toString().toInt()
            } else {
                binding.editTextQuizMarks.error = "Pleaase fill valid amount"
            }





            binding.progressBar.visible()

            course?.courseCode?.let { it1 -> viewModel.createQuizContest(it1, contest) }


        }


    }

    private fun setClickListeners() {

    }


}