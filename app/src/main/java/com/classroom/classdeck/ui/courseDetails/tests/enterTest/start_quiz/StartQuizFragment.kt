package com.classroom.classdeck.ui.courseDetails.tests.enterTest.start_quiz

import android.animation.Animator
import android.content.res.ColorStateList
import android.graphics.Color
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import android.os.CountDownTimer
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.DecelerateInterpolator
import android.widget.Button
import android.widget.TextView
import androidx.activity.addCallback
import androidx.navigation.fragment.findNavController
import com.classroom.classdeck.R
import com.classroom.classdeck.data.model.*
import com.classroom.classdeck.databinding.StartQuizFragmentBinding
import com.classroom.classdeck.util.*
import com.google.android.material.button.MaterialButton
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class StartQuizFragment : Fragment() {


    private lateinit var binding: StartQuizFragmentBinding
    private val viewModel: StartQuizViewModel by lazy {
        ViewModelProvider(this).get(StartQuizViewModel::class.java)
    }

    var course: Course? = null;
    var user: User? = null
    var quiz: Quiz? = null

    private var contestUsr: QuizUser? = null


    private var score = 0
    private var count = 0
    private var correct = 0
    private var wrong = 0
    private var unattempted = 0

    private lateinit var quesList: MutableList<Question>
    private var position: Int = 0
    private lateinit var timer: CountDownTimer


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        arguments?.let {
            course = (it.getSerializable(Constants.COURSE_BUNDLE_OBJ) as Course?)
            quiz = (it.getSerializable(Constants.QUIZ_BUNDLE_OBJ) as Quiz?)
            user = (it.getSerializable(Constants.USERS_BUNDLE_OBJ) as User?)
        }

        requireActivity().onBackPressedDispatcher.addCallback(this) {
            context?.toast("Please complete quiz")
        }

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = StartQuizFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        quesList = ArrayList()
        timer = createTimer()
        binding.nextQuest.gone()
        observeData()
        getData()
        setData()
        setMyClickListener()
    }

    private fun setMyClickListener() {

        for (i in 0 until 4) {
            binding.optionLay.getChildAt(i).setOnClickListener {
                startTimer(timer, false)
                checkAnswer(it as MaterialButton, true)
            }
        }

        binding.nextQuest.setOnClickListener {
            binding.nextQuest.gone()

            enableOption(true)
            position++
            if (position == quesList.size) {

                enableOption(false)
                binding.nextQuest.text = "Submit"

                // go to next fragment
                // save data to contest ans contestUser

                saveDataForContestAndContestUser()

            }
            if (position != quesList.size) {
                count = 0
                quesList[position].question?.let { it1 -> playAnim(binding.question, 0, it1) }
                startTimer(timer, true)
            }
        }


    }

    private fun saveDataForContestAndContestUser() {


        contestUsr = quiz?.let { Mappers.contestToUserContest(it) }!!
        contestUsr!!.wrong = wrong
        contestUsr!!.correct = correct
        contestUsr!!.completed = true
        binding.progressBar.visible()

        course?.let { viewModel.userCompletedContest(quiz!!, contestUsr!!, user, it) }


    }

    private fun enableOption(b: Boolean) {

        for (i in 0 until 4) {
            binding.optionLay.getChildAt(i).isEnabled = b
            if (b) {
                binding.optionLay.getChildAt(i).backgroundTintList =
                    ColorStateList.valueOf(Color.WHITE)
            }
        }


    }

    private fun setData() {
        enableOption(false)
    }

    private fun getData() {

        binding.progressBar.visible()
        quiz?.let { course?.let { it1 -> viewModel.getQuizQuestions(it, it1) } }


    }

    private fun observeData() {

        viewModel.submitQuizStatus.observe(viewLifecycleOwner, { task ->


            when (task) {
                is ResponseState.Success -> {
                    binding.progressBar.gone()

                    val bundle = Bundle().apply {
                        putSerializable(Constants.USERS_BUNDLE_OBJ, user)
                        putSerializable(Constants.QUIZ_BUNDLE_OBJ, quiz)
                        putSerializable(Constants.CONTEST_USER_BUNDLE_OBJ, contestUsr)
                        putSerializable(Constants.COURSE_BUNDLE_OBJ, course)

                    }

                    findNavController().navigate(
                        R.id.action_startQuizFragment_to_quizResultFragment,
                        bundle
                    )


                }
                is ResponseState.Error -> {
                    binding.progressBar.gone()
                    binding.root?.snackbar("${task.message}")
                }
                is ResponseState.Loading -> {
                }

            }


        })



        viewModel.quizQuestions.observe(viewLifecycleOwner, { task ->
            when (task) {
                is ResponseState.Success -> {
                    binding.progressBar.gone()

                    // update quiz is taken by user
                    task.data?.let { list ->

                        quesList.clear()
                        for (i in list) {
                            if (i != null) {
                                quesList.add(i)
                            }
                        }

                        if (quesList.size > 0) {
                            //view
                            quesList[position].question?.let { playAnim(binding.question, 0, it) }
                            enableOption(true)
                            startTimer(timer, true)
                        }


                    }
                }
                is ResponseState.Loading -> {
                }
                is ResponseState.Error -> {
                    binding.progressBar.gone()

                    binding.root?.snackbar("Some error occured, Please try again later")
                }

            }

        })

    }


    private fun createTimer(): CountDownTimer {
        return object : CountDownTimer((quiz?.answerTime?.times(1000))!!.toLong(), 1000) {
            override fun onTick(millisUntilFinished: Long) {
                // update textview time
                binding.quizTimeLeft.text = (millisUntilFinished / 1000).toString()
            }

            override fun onFinish() {
                // checkanswers

                // unattempted
                 unattempted++
              //  wrong++
                for (i in 0 until 4) {
                    checkAnswer(binding.optionLay.getChildAt(i) as MaterialButton, false)
                }
            }

        }
    }

    private fun startTimer(timer: CountDownTimer, start: Boolean) {
        if (start) {
            timer.start()
        } else {
            timer.cancel()
        }
    }

    private fun checkAnswer(selectOption: MaterialButton, increase: Boolean) {
        // enable next question option

        enableOption(false)
        binding.nextQuest.visible()

        if (selectOption.text.toString() == quesList[position].answer) {

            if (increase) {
                score++
                correct++
            }

            selectOption?.backgroundTintList = ColorStateList.valueOf(Color.GREEN)

        } else {
            wrong++
            selectOption?.backgroundTintList = ColorStateList.valueOf(Color.RED)

            val correctOption: MaterialButton =
                binding.optionLay.findViewWithTag(quesList[position].answer)
            correctOption.backgroundTintList = ColorStateList.valueOf(Color.GREEN)

        }

    }


    private fun playAnim(view: View, value: Int, data: String) {

        view.animate().alpha(value.toFloat()).scaleX(value.toFloat()).scaleY(1F)
            .setDuration(500).setStartDelay(50)
            .setInterpolator(DecelerateInterpolator())
            .setListener(object : Animator.AnimatorListener {
                override fun onAnimationStart(animation: Animator?) {

                    if (value == 0 && count < 4) {
                        var option = ""
                        if (count == 0) {
                            option = quesList[position].optionA.toString()

                        } else if (count == 1) {
                            option = quesList[position].optionB.toString()
                        } else if (count == 2) {
                            option = quesList[position].optionC.toString()
                        } else if (count == 3) {
                            option = quesList[position].optionD.toString()
                        }

                        playAnim(binding.optionLay.getChildAt(count), 0, option)
                        count++

                    }


                }

                override fun onAnimationEnd(animation: Animator?) {

                    try {
                        (view as TextView).text = data
                        binding.quesNumCounter.text = "${position + 1}/${quesList.size}"
                    } catch (e: Exception) {
                        e.printStackTrace()
                        (view as Button).text = data
                    }

                    view.tag = data
                    if (value != 1) {
                        playAnim(view, 1, data)
                    }


                }


                override fun onAnimationRepeat(animation: Animator?) {

                }

                override fun onAnimationCancel(animation: Animator?) {

                }

            })


    }


}