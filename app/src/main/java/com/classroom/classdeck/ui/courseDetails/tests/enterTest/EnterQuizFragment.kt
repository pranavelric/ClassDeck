package com.classroom.classdeck.ui.courseDetails.tests.enterTest

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.classroom.classdeck.R
import com.classroom.classdeck.adapters.LeaderBoardAdapter
import com.classroom.classdeck.data.model.Course
import com.classroom.classdeck.data.model.Quiz
import com.classroom.classdeck.data.model.User
import com.classroom.classdeck.databinding.FragmentEnterQuizBinding
import com.classroom.classdeck.util.*
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject


@AndroidEntryPoint
class EnterQuizFragment : Fragment() {


    private val viewModel: QuizEntryViewModel by lazy {
        ViewModelProvider(this).get(QuizEntryViewModel::class.java)
    }

    @Inject
    lateinit var leaderBoardAdapter: LeaderBoardAdapter

    private lateinit var binding: FragmentEnterQuizBinding
    var course: Course? = null;
    var user: User? = null
    var quiz: Quiz? = null
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
        binding = FragmentEnterQuizBinding.inflate(inflater, container, false)






        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        getData()
        checkIfRegistered()
        setData()
        observeData()
        setClickListeners()
    }

    private fun getData() {
        binding.progressBar.visible()
        course?.let { quiz?.let { it1 -> viewModel.getUserRanking(it1, it) } }



    }

    private fun checkIfRegistered() {
        user?.let {
            quiz?.let { it1 ->
                course?.let { it2 ->
                    viewModel.isUserEnteredInContest(
                        it, it1,
                        it2
                    )
                }
            }
        }
        viewModel.isUserResgistered.observe(viewLifecycleOwner, {
            when (it) {
                is ResponseState.Success -> {

                    if (it.data == true) {

                        binding.regBtn.gone()
                        binding.entryBtn.visible()
                        binding.registeredTag.visible()
                    }

                }
                is ResponseState.Loading -> {
                }
                is ResponseState.Error -> {

                    binding.regBtn.isEnabled = false
                    binding.registeredTag.visible()

                    it.message?.let { context?.toast(it) }
                }

            }
        })
    }


    private fun setData() {

        binding.courseCodeText.text = course?.courseCode
        binding.quizMarks.text = quiz?.marks.toString()
        binding.quizTitleText.text = quiz?.quizTitle
        binding.textDate.text = (quiz?.startDate + " " + quiz?.startTime)



        binding.leaderboardRc.apply {
            adapter = leaderBoardAdapter
        }


    }


    private fun setClickListeners() {
        binding.regBtn.setOnClickListener {

            setEntry()

        }


        binding.entryBtn.setOnClickListener {
            val bundle = Bundle().apply {
                putSerializable(Constants.USERS_BUNDLE_OBJ, user)

                putSerializable(Constants.QUIZ_BUNDLE_OBJ, quiz)
                putSerializable(Constants.COURSE_BUNDLE_OBJ, course)
            }
            findNavController().navigate(
                R.id.action_enterQuizFragment_to_entryFragment,
                bundle
            )


        }


    }

    private fun setEntry() {


        binding.progressBar.visible()
        course?.let { quiz?.let { it1 -> viewModel.enterInQuiz(user!!, it1, it) } }


    }


    private fun observeData() {
        viewModel.quizEntryStatus.observe(viewLifecycleOwner, {
            when (it) {
                is ResponseState.Success -> {
                    binding.progressBar.gone()
                    binding.root.snackbar("${it.data}")

                    binding.regBtn.gone()
                    binding.entryBtn.visible()
                    binding.registeredTag.visible()
                }
                is ResponseState.Error -> {
                    binding.progressBar.gone()
                    binding.root.snackbar("${it.message}")
                }
                is ResponseState.Loading -> {
                }

            }
        })


        viewModel.getUserRanking.observe(viewLifecycleOwner,{
            when (it) {
                is ResponseState.Success -> {
                    binding.progressBar.gone()
                    if (it.data == null) {
                        binding.emptyLay.visible()
                    } else {
                        binding.emptyLay.gone()

                        it.data?.let { ranking ->
                            leaderBoardAdapter.submitList(ranking.rankings)
                        }

                    }


                }
                is ResponseState.Error -> {
                    binding.progressBar.gone()
                    binding.emptyLay.visible()
                    it.message?.let { it1 -> context?.toast_long(it1) }
                }
                is ResponseState.Loading -> {
                }

            }
        })




    }


}