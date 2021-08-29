package com.classroom.classdeck.ui.courseDetails.tests.createTest.createQuestions


import android.app.Dialog
import android.os.Bundle
import android.view.*
import androidx.activity.addCallback
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.classroom.classdeck.adapters.QuestionAdapter
import com.classroom.classdeck.data.model.Course
import com.classroom.classdeck.data.model.Quiz
import com.classroom.classdeck.data.model.User
import com.classroom.classdeck.databinding.FragmentAddQuestionsBinding
import com.classroom.classdeck.ui.courseDetails.tests.QuizViewModel
import javax.inject.Inject
import com.classroom.classdeck.R
import com.classroom.classdeck.data.model.Question
import com.classroom.classdeck.util.*
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class AddQuestionsFragment : Fragment() {


    @Inject
    lateinit var questionAdapter: QuestionAdapter
    var questionIndex: Int = 0
    private lateinit var binding: FragmentAddQuestionsBinding
    private val viewModel: QuizViewModel by lazy {
        ViewModelProvider(this).get(QuizViewModel::class.java)
    }
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

        requireActivity().onBackPressedDispatcher.addCallback(this) {
            context?.toast("Please fill all details of this quiz than you can go back")
        }


    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentAddQuestionsBinding.inflate(inflater, container, false)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        getData()
        setData()
        observeData()
        clickListeners()

    }


    private fun clickListeners() {
        binding.addNewQa.setOnClickListener {
            showQuestionsDialog()

        }

        binding.done.setOnClickListener {
            val bundle = Bundle().apply {
                putSerializable(Constants.USERS_BUNDLE_OBJ, user)
                putSerializable(Constants.COURSE_BUNDLE_OBJ, course)


            }
            findNavController().navigate(
                R.id.action_addQuestionsFragment_to_quizFragment,
                bundle
            )

        }

    }

    private fun showQuestionsDialog() {

        val dialog = Dialog(requireActivity(), android.R.style.Theme_Translucent_NoTitleBar)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(false)
        dialog.setContentView(R.layout.question_dialog)
        val window = dialog.window
        val wlp = window!!.attributes
        wlp.gravity = Gravity.CENTER
        wlp.flags = wlp.flags and WindowManager.LayoutParams.FLAG_BLUR_BEHIND.inv()
        window!!.attributes = wlp
        dialog.window!!.setLayout(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.MATCH_PARENT
        )


        val dialogButton = dialog.findViewById<MaterialButton>(R.id.save_btn)

        val question = dialog.findViewById<TextInputEditText>(R.id.edit_text_question)
        val correctAns = dialog.findViewById<TextInputEditText>(R.id.edit_text_correct_answer)
        val optionb = dialog.findViewById<TextInputEditText>(R.id.edit_text_incorrect_answerb)
        val optionc = dialog.findViewById<TextInputEditText>(R.id.edit_text_incorrect_answerc)
        val optiond = dialog.findViewById<TextInputEditText>(R.id.edit_text_incorrect_answerd)


        val mQuestion = Question()


        dialogButton.setOnClickListener {

            mQuestion.period = questionIndex + 1

            if (!question.text.isNullOrBlank()) {

                mQuestion.question = question.text.toString()

            } else {
                question.error = "Please fill all field"
                return@setOnClickListener
            }

            if (!correctAns.text.isNullOrBlank()) {

                mQuestion.answer = correctAns.text.toString()
                mQuestion.optionA = correctAns.text.toString()

            } else {
                correctAns.error = "Please fill all field"
                return@setOnClickListener
            }
            if (!optionb.text.isNullOrBlank()) {

                mQuestion.optionB = optionb.text.toString()

            } else {
                optionb.error = "Please fill all field"
                return@setOnClickListener
            }

            if (!optionc.text.isNullOrBlank()) {
                mQuestion.optionC = optionc.text.toString()
            } else {
                optionc.error = "Please fill all field"
                return@setOnClickListener
            }

            if (!optiond.text.isNullOrBlank()) {
                mQuestion.optionD = optiond.text.toString()
            } else {
                optiond.error = "Please fill all field"
                return@setOnClickListener
            }

            val listOfAnswer: ArrayList<String> = arrayListOf(
                optionb.text.toString(),
                optionc.text.toString(),
                optiond.text.toString(),
                correctAns.text.toString()
            )

            listOfAnswer.shuffle()
            mQuestion.optionA = listOfAnswer[0]
            mQuestion.optionB = listOfAnswer[1]
            mQuestion.optionC = listOfAnswer[2]
            mQuestion.optionD = listOfAnswer[3]

            mQuestion.id = "Q${questionIndex + 1}"



            quiz?.id?.let { it1 ->
                course?.courseCode?.let { it2 ->
                    viewModel.addQuestionToContest(
                        it2,
                        it1, mQuestion
                    )
                }
            }

            dialog.dismiss()
            binding.progressBar.visible()

        }

        dialog.show()
    }

    private fun observeData() {


        course?.courseCode?.let { quiz?.id?.let { it1 -> viewModel.getQuizQuestions(it, it1) } }
        viewModel.quizQuestions.observe(viewLifecycleOwner, {
            when (it) {
                is ResponseState.Success -> {

                    it.data?.let { list ->
                        if (list.isEmpty()) {
                            binding.emptyLay.visible()
                        } else {
                            binding.emptyLay.gone()

                            questionIndex = list.size


                            if (questionIndex >= quiz?.question_numbers!!) {
                                binding.addNewQa.gone()
                                binding.done.visible()


                            } else {
                                binding.addNewQa.visible()
                                binding.done.gone()
                            }
                        }

                        questionAdapter.submitList(list)
                        questionAdapter.notifyDataSetChanged()
                    }

                }
                is ResponseState.Error -> {
                    binding.emptyLay.visible()
                    it.message?.let { it1 -> context?.toast(it1) }
                }
                is ResponseState.Loading -> {
                }

            }

        })
        viewModel.createQuizQuestionsStatus.observe(viewLifecycleOwner, {
            when (it) {
                is ResponseState.Success -> {

                    course?.courseCode?.let { it1 ->
                        quiz?.id?.let { it2 ->
                            viewModel.getQuizQuestions(
                                it1,
                                it2
                            )
                        }
                    }


                    it.data?.let { it1 -> context?.toast(it1) }
                    binding.progressBar.gone()

                }
                is ResponseState.Error -> {
                    it.message?.let { it1 -> context?.toast(it1) }
                    binding.progressBar.gone()
                }
                is ResponseState.Loading -> {
                }

            }

        })


    }

    private fun setData() {

        binding.questionsRc.apply {
            adapter = questionAdapter
        }


    }

    private fun getData() {


    }


}