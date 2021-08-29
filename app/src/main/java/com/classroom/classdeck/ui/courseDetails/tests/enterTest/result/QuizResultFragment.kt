package com.classroom.classdeck.ui.courseDetails.tests.enterTest.result

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import android.os.CountDownTimer
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.addCallback
import androidx.navigation.fragment.findNavController
import com.classroom.classdeck.R
import com.classroom.classdeck.data.model.*
import com.classroom.classdeck.databinding.QuizResultFragmentBinding
import com.classroom.classdeck.util.*
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class QuizResultFragment : Fragment() {


    private val viewModel: QuizResultViewModel by lazy {
        ViewModelProvider(this).get(QuizResultViewModel::class.java)
    }
    private lateinit var binding: QuizResultFragmentBinding

    private var rankinglist: ArrayList<RankingModel> = ArrayList()

    var course: Course? = null;
    var user: User? = null
    var quiz: Quiz? = null

    private var contestUsr: QuizUser? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        arguments?.let {
            course = (it.getSerializable(Constants.COURSE_BUNDLE_OBJ) as Course?)
            quiz = (it.getSerializable(Constants.QUIZ_BUNDLE_OBJ) as Quiz?)
            user = (it.getSerializable(Constants.USERS_BUNDLE_OBJ) as User?)
            contestUsr = (it.getSerializable(Constants.CONTEST_USER_BUNDLE_OBJ) as QuizUser?)
        }

        requireActivity().onBackPressedDispatcher.addCallback(this) {
            context?.toast("Please submit quiz")
        }

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = QuizResultFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        observeData()
        getData()
        setData()
        setMyClickListeners()
    }

    private fun setMyClickListeners() {
        binding.doneBtn.setOnClickListener {
            val bundle = Bundle().apply {
                putSerializable(Constants.USERS_BUNDLE_OBJ, user)
                putSerializable(Constants.COURSE_BUNDLE_OBJ, course)
            }

            findNavController().navigate(
                R.id.action_quizResultFragment_to_quizFragment,
                bundle
            )
        }


    }

    private fun setData() {

        binding.correctText.text = contestUsr?.correct.toString()
        binding.wrongText.text = contestUsr?.wrong.toString()

    }

    private fun getData() {

        binding.progressBar.visible()
        binding.progressBar.visible()
        quiz?.let { course?.let { it1 -> viewModel.getQuizResult(it, it1) } }


    }

    private fun observeData() {


        viewModel.submitQuizRanking.observe(viewLifecycleOwner, {
            when (it) {
                is ResponseState.Success -> {
                    binding.progressBar.gone()
                    it.data?.let { b ->
                        binding.doneBtn.visible()
                    }
                }
                is ResponseState.Error -> {
                    binding.progressBar.gone()
                    it.message?.let { it1 -> context?.toast_long(it1) }
                }
                is ResponseState.Loading -> {
                }

            }
        })


        viewModel.quizResult.observe(viewLifecycleOwner, {

            when (it) {
                is ResponseState.Success -> {
                    binding.progressBar.gone()
                    it.data?.let { list ->

                        for (i in 0 until list.size) {
                            rankinglist.add(
                                RankingModel(
                                    list[i]?.userId,
                                    list[i]?.userPhoneNumber,
                                    user?.name,
                                    user?.email,
                                    i,
                                    list[i]?.correct
                                )
                            )
                        }

                        binding.progressBar.visible()

                        quiz?.let { it1 ->
                            course?.let { it2 ->
                                viewModel.addUserRanking(
                                    it1, rankinglist,
                                    it2
                                )
                            }
                        }

                    }
                }
                is ResponseState.Error -> {
                    binding.progressBar.gone()
                    it.message?.let { it1 -> context?.toast_long(it1) }
                }
                is ResponseState.Loading -> {
                }

            }

        })

    }


}