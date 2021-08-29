package com.classroom.classdeck.ui.courseDetails.tests

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.classroom.classdeck.R
import com.classroom.classdeck.adapters.QuizAdapter
import com.classroom.classdeck.data.model.Course
import com.classroom.classdeck.data.model.User
import com.classroom.classdeck.databinding.QuizFragmentBinding
import com.classroom.classdeck.util.*
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject


@AndroidEntryPoint
class QuizFragment : Fragment() {


    private lateinit var binding: QuizFragmentBinding
    private val viewModel: QuizViewModel by lazy {
        ViewModelProvider(this).get(QuizViewModel::class.java)
    }

    @Inject
    lateinit var quizAdapter: QuizAdapter

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
        binding = QuizFragmentBinding.inflate(inflater, container, false)
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
            binding.createTestButton.visible()
            binding.createTestButton.setOnClickListener {
                val bundle = Bundle().apply {
                    putSerializable(Constants.COURSE_BUNDLE_OBJ, course)
                    putSerializable(Constants.USERS_BUNDLE_OBJ, user)
                }

                findNavController().navigate(
                    R.id.action_quizFragment_to_createQuizFragment,
                    bundle
                )

            }
        } else {
            binding.createTestButton.gone()

        }


        quizAdapter.setOnItemClickListener { quiz, pos ->

            val bundle = Bundle().apply {
                putSerializable(Constants.COURSE_BUNDLE_OBJ, course)
                putSerializable(Constants.USERS_BUNDLE_OBJ, user)
                putSerializable(Constants.QUIZ_BUNDLE_OBJ, quiz)

            }

            findNavController().navigate(
                R.id.action_quizFragment_to_enterQuizFragment,
                bundle
            )

        }

        quizAdapter.setOnEntryBtnClickListener { quiz, pos ->

            val bundle = Bundle().apply {
                putSerializable(Constants.COURSE_BUNDLE_OBJ, course)
                putSerializable(Constants.USERS_BUNDLE_OBJ, user)
                putSerializable(Constants.QUIZ_BUNDLE_OBJ, quiz)

            }

            findNavController().navigate(
                R.id.action_quizFragment_to_enterQuizFragment,
                bundle
            )


        }


    }

    private fun observeData() {


        viewModel.allQuizQuizs.observe(viewLifecycleOwner, {

            when (it) {
                is ResponseState.Success -> {

                    binding.progressBar.gone()
                    it.data?.let { data ->
                        if (data.isEmpty()) {
                            binding.emptyLay.visible()

                        } else {
                            binding.emptyLay.gone()
                        }

                        quizAdapter.submitList(data)

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
        course?.courseCode?.let { viewModel.getAllQuizQuiz(it) }

    }

    private fun setData() {

        binding.quizRc.apply {
            adapter = quizAdapter
        }


    }


}