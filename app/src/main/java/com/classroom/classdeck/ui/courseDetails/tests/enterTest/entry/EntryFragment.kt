package com.classroom.classdeck.ui.courseDetails.tests.enterTest.entry

import android.graphics.Color
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.classroom.classdeck.R
import com.classroom.classdeck.data.model.Course
import com.classroom.classdeck.data.model.Quiz
import com.classroom.classdeck.data.model.QuizUser
import com.classroom.classdeck.data.model.User
import com.classroom.classdeck.databinding.EntryFragmentBinding
import com.classroom.classdeck.util.*
import com.marcoscg.dialogsheet.DialogSheet
import dagger.hilt.android.AndroidEntryPoint
import ir.samanjafari.easycountdowntimer.CountDownInterface
import java.util.concurrent.TimeUnit

@AndroidEntryPoint
class EntryFragment : Fragment() {


    private lateinit var binding: EntryFragmentBinding

    private val viewModel: EntryViewModel by lazy {
        ViewModelProvider(this).get(EntryViewModel::class.java)
    }

    var course: Course? = null;
    var user: User? = null
    var quiz: Quiz? = null

    private var contestUser: QuizUser? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        arguments?.let {
            course = (it.getSerializable(Constants.COURSE_BUNDLE_OBJ) as Course?)
            quiz = (it.getSerializable(Constants.QUIZ_BUNDLE_OBJ) as Quiz?)
            user = (it.getSerializable(Constants.USERS_BUNDLE_OBJ) as User?)
        }

    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = EntryFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        observeData()
        checkIfAlreadyCompleted()
        setData()
        setMyListeners()

    }

    private fun observeData() {

        viewModel.userContest.observe(viewLifecycleOwner, {
            when (it) {
                is ResponseState.Success -> {
                    binding.progressBar.gone()
                    it.data?.let { cu ->

                        contestUser = cu

                        if (cu.completed == true) {
                            gameAlreafyPlayedUi()
                        } else {

                        }


                    }
                }
                is ResponseState.Error -> {
                    binding.progressBar.gone()
                }
                is ResponseState.Loading -> {
                }

            }

        })
    }

    private fun gameAlreafyPlayedUi() {
        binding.playBtn.gone()

        context?.let {
            DialogSheet(it)
                .setTitle("Already played")
                .setMessage(
                    "You have already completed this Quiz.\nYou answered ${contestUser?.correct} out of ${contestUser?.question_numbers} questions correctly."
                )
                .setColoredNavigationBar(true)
                .setTitleTextSize(20) // In SP
                .setCancelable(false)
                .setPositiveButton(android.R.string.ok) {
                    findNavController().navigateUp()

                }
                .setNegativeButton(android.R.string.cancel) {
                    findNavController().navigateUp()
                }
                .setRoundedCorners(true)
                .setBackgroundColor(Color.WHITE)
                .setButtonsColorRes(R.color.primary_color_amoled)
                .setNegativeButtonColorRes(R.color.red)
                .show()
        }


    }

    private fun checkIfAlreadyCompleted() {

        user?.uid?.let { quiz?.id?.let { it1 -> viewModel.getUserContest(it, it1) } }

    }

    private fun setMyListeners() {

        binding.easyCountDownTextview.setOnTick(object : CountDownInterface {
            override fun onTick(time: Long) {
                val min = TimeUnit.MILLISECONDS.toMinutes(time)
                val sec = TimeUnit.MILLISECONDS.toSeconds(time)

            }

            override fun onFinish() {
                // show winning popup

                setGameStarted()
                binding.playBtn.visible()
                gameStartedChangeViews()

            }

        })


        binding.playBtn.setOnClickListener {
            val bundle = Bundle().apply {
                putSerializable(Constants.USERS_BUNDLE_OBJ, user)
                putSerializable(Constants.QUIZ_BUNDLE_OBJ, quiz)
                putSerializable(Constants.COURSE_BUNDLE_OBJ, course)

            }

            findNavController().navigate(R.id.action_entryFragment_to_startQuizFragment, bundle)
        }


    }

    private fun setData() {


        val ratio = quiz?.marks?.div(quiz?.question_numbers!!)

        binding.quizInfo.text =
            "This quiz will contain ${quiz?.question_numbers} questions. You will get ${quiz?.answerTime}sec to answer each questions. For each correct answer your will receive ${ratio} marks."


        val timeAndDate = quiz?.startDate + " " + quiz?.startTime

        if (!checkTimeLessThanCurrenTimeIncludingDays(timeAndDate)) {

            getDateTimeDiff(timeAndDate)?.let { list ->
                binding.easyCountDownTextview.setTime(list[0], list[1], list[2], list[3])
                binding.daysText.text = list[0].toString() + "D"
                if (list[0] == 0) {
                    binding.daysColon.gone()
                    binding.daysText.gone()
                }
                if (list[0].toInt() == 0 && list[1].toInt() == 0 && list[2].toInt() == 0 && list[3] == 0) {

                    // time ended
                    // enable play button

                }

            }

            binding.easyCountDownTextview.startTimer()
            binding.playBtn.gone()
        } else {

            binding.playBtn.visible()
            setGameStarted()
            gameStartedChangeViews()
        }


    }

    private fun setGameStarted() {
        course?.let { quiz?.let { it1 -> viewModel.setGameStarted(it1, it) } }
    }

    private fun gameStartedChangeViews() {
        binding.editTextLay.gone()
        binding.entryHeading1.gone()
        binding.entryHeading2.visible()
    }


}