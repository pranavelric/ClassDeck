package com.classroom.classdeck.ui.home

import android.app.Dialog
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment

import com.classroom.classdeck.databinding.HomeFragmentBinding
import dagger.hilt.android.AndroidEntryPoint


import android.view.*
import android.widget.EditText
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.Observer

import androidx.navigation.fragment.findNavController
import com.classroom.classdeck.R
import com.classroom.classdeck.adapters.CourseAdapter
import com.classroom.classdeck.data.model.Course
import com.classroom.classdeck.data.model.User
import com.classroom.classdeck.ui.activity.MainActivity
import com.classroom.classdeck.util.*
import com.classroom.classdeck.util.Constants.COURSE_BUNDLE_OBJ
import com.classroom.classdeck.util.Constants.USERS_BUNDLE_OBJ
import com.google.android.material.button.MaterialButton
import me.ibrahimsn.lib.OnItemSelectedListener
import javax.inject.Inject


@AndroidEntryPoint
class HomeFragment : Fragment() {


    private lateinit var viewModel: HomeViewModel

    var user: User? = null;
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        arguments?.let {
            user = (it.getSerializable(Constants.USERS_BUNDLE_OBJ) as User?)
        }

        viewModel = ViewModelProvider(this).get(HomeViewModel::class.java)

    }

    @Inject
    lateinit var courseAdapter: CourseAdapter
    private lateinit var binding: HomeFragmentBinding

    private lateinit var courseList: ArrayList<Course?>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = HomeFragmentBinding.inflate(inflater, container, false)

        binding.toolbar.setOnMenuItemClickListener {
            onOptionsItemSelected(it)
        }



        if (user?.isStudent == true) {
            binding.toolbar.menu.findItem(R.id.action_add_class).isVisible = false
            binding.toolbar.menu.findItem(R.id.action_join_class).isVisible = true
        } else {
            binding.toolbar.menu.findItem(R.id.action_add_class).isVisible = true
            binding.toolbar.menu.findItem(R.id.action_join_class).isVisible = false
        }



        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        courseList = ArrayList()
        setHasOptionsMenu(true)
        setRecyclerView()
        observeData()
        setData()
        setMyClickListeners()

    }

    private fun observeData() {


        viewModel.joinClassLiveData.observe(viewLifecycleOwner, { joinClasstask ->

            if (viewLifecycleOwner.lifecycle.currentState == Lifecycle.State.RESUMED)
                when (joinClasstask) {

                    is ResponseState.Success -> {
                        binding.progressBar.gone()

                        joinClasstask.data?.let { msg ->
                            context?.toast(msg)
                        }


                        binding.progressBar.visible()
                        user?.uid?.let { viewModel.getAllStudentCourses(it) }
                        courseAdapter.notifyDataSetChanged()


                    }
                    is ResponseState.Error -> {
                        binding.progressBar.gone()

                        joinClasstask.message?.let { context?.toast(it) }

                    }
                    is ResponseState.Loading -> {
                    }


                }

        })


        // getCourse from code
        viewModel.getCourseFromCode.observe(viewLifecycleOwner, {


            if (viewLifecycleOwner.lifecycle.currentState == Lifecycle.State.RESUMED)
                when (it) {
                    is ResponseState.Success -> {


                        it.data?.let { it1 -> user?.let { it2 -> viewModel.joinClass(it1, it2) } }
                    }

                    is ResponseState.Error -> {
                        binding.progressBar.gone()
                        it.message?.let { it1 -> context?.toast(it1) }
                    }
                    is ResponseState.Loading -> {
                    }

                }

        })

        viewModel.getCourseList.observe(viewLifecycleOwner, {

            if (viewLifecycleOwner.lifecycle.currentState == Lifecycle.State.RESUMED)
                when (it) {
                    is ResponseState.Success -> {
                        binding.progressBar.gone()



                        it.data?.let { list ->

                            if (list.isEmpty()) {
                                binding.emptyLay.visible()
                            } else {
                                binding.emptyLay.gone()



                            }

                            courseAdapter.submitList(list)
                            courseAdapter.notifyDataSetChanged()


                        }

                    }

                    is ResponseState.Error -> {
                        binding.progressBar.gone()
                        it.message?.let { it1 -> context?.toast(it1) }
                        binding.emptyLay.visible()


                    }
                    is ResponseState.Loading -> {
                    }

                }

        })


        viewModel.addCourse.observe(viewLifecycleOwner, {

            if (viewLifecycleOwner.lifecycle.currentState == Lifecycle.State.RESUMED)
                when (it) {
                    is ResponseState.Success -> {
                        binding.progressBar.gone()
                        it.data?.let { it1 -> context?.toast(it1) }


                        binding.progressBar.visible()
                        user?.uid?.let { viewModel.getAllStudentCourses(it) }
                        courseAdapter.notifyDataSetChanged()

                    }

                    is ResponseState.Error -> {
                        binding.progressBar.gone()
                        it.message?.let { it1 -> context?.toast(it1) }

                    }
                    is ResponseState.Loading -> {
                    }

                }

        })

    }

    private fun setMyClickListeners() {


        binding.bottomBar.onItemSelectedListener = object : OnItemSelectedListener {

            override fun onItemSelect(pos: Int): Boolean {
                when (pos) {
                    0 -> {
                        return true
                    }
                    1 -> {
                        val bundle = Bundle().apply {
                            putSerializable(USERS_BUNDLE_OBJ, user)
                        }

                        findNavController().navigate(
                            R.id.action_homeFragment_to_dashBoardFragment,
                            bundle
                        )
                        return true
                    }

                    2 -> {
                        val bundle = Bundle().apply {
                            putSerializable(USERS_BUNDLE_OBJ, user)
                        }


                        viewModel.getCourseList.removeObservers(viewLifecycleOwner)
                        viewModel.addCourse.removeObservers(viewLifecycleOwner)


                        findNavController().navigate(
                            R.id.action_homeFragment_to_profileFragment,
                            bundle
                        )

                        return true
                    }

                    else -> {

                        return false
                    }
                }

            }
        }


        courseAdapter.setOnItemClickListener { course, pos ->

            val bundle = Bundle().apply {
                putSerializable(COURSE_BUNDLE_OBJ, course)
            }
            findNavController().navigate(R.id.action_homeFragment_to_courseDetailFragment, bundle)

        }



    }

    private fun setData() {

        binding.progressBar.visible()
        user?.uid?.let { viewModel.getAllStudentCourses(it) }
    }

    private fun setRecyclerView() {
        binding.courseRc.apply {
            adapter = courseAdapter
        }
    }

    override fun onStart() {
        super.onStart()
        (activity as MainActivity).setFullScreenWithBtmNav()

    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {


        when (item.itemId) {
            R.id.action_profile -> {
                val bundle = Bundle().apply {
                    putSerializable(USERS_BUNDLE_OBJ, user)
                }
                findNavController().navigate(R.id.action_homeFragment_to_profileFragment, bundle)
                return true
            }
            R.id.action_logout -> {


                (activity as MainActivity).firebaseAuth.signOut()
                (activity as MainActivity).googleSignInClient.signOut()
                findNavController().navigate(R.id.action_homeFragment_to_selectTypeFragment)

                return true
            }
            R.id.action_share -> {
                activity?.share("Playstore link", "text")

                return true
            }

            R.id.action_notification -> {
                val bundle = Bundle().apply {
                    putSerializable(USERS_BUNDLE_OBJ, user)
                }

//                findNavController().navigate(
//                    R.id.action_mainFragment_to_notificationsFragment,
//                    bundle
//                )
                return true
            }

            R.id.action_Settings->{
                findNavController().navigate(R.id.action_homeFragment_to_settingsFragment)
            return true
            }

            R.id.action_join_class -> {
                showJoinClassDialog()
                return true
            }
            R.id.action_add_class -> {
                showAddClassDialog()
                return true
            }

            else -> {
                return false
            }
        }
    }

    private fun showAddClassDialog() {
        val dialog = Dialog(requireActivity())
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(false)
        dialog.window?.setLayout(500, WindowManager.LayoutParams.WRAP_CONTENT);
        dialog.setContentView(R.layout.add_class_dialog)


        val dialogButton = dialog.findViewById<MaterialButton>(R.id.create_class_btn)
        val canceButton = dialog.findViewById<MaterialButton>(R.id.cancel)
        val courseCode = dialog.findViewById<EditText>(R.id.editTextClassCode)
        val courseName = dialog.findViewById<EditText>(R.id.editTextClassName)



        canceButton.setOnClickListener { dialog.dismiss() }
        dialogButton.setOnClickListener {

            if (courseCode.text.isNullOrBlank()) {
                // context?.showInfoBottomSheet("Error","Please enter a valid course code")
                courseCode.error = "Enter a valid course code"
            } else if (courseName.text.isNullOrBlank()) {
                courseName.error = "Enter a valid course Name"

            } else {
                addClass(courseCode.text.toString(), courseName.text.toString())
                dialog.dismiss()
            }


        }

        dialog.show()

    }

    private fun addClass(courseCode: String, courseName: String) {

        val course = Course(courseCode, courseName, user?.name ?: "Proffesor", 0)
        binding.progressBar.visible()
        user?.let { viewModel.addCourse(course, it) }


    }

    private fun showJoinClassDialog() {


        val dialog = Dialog(requireActivity())
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(false)
        dialog.window?.setLayout(500, WindowManager.LayoutParams.WRAP_CONTENT);
        dialog.setContentView(R.layout.join_class_dialog)


        val dialogButton = dialog.findViewById<MaterialButton>(R.id.join_class_btn)
        val canceButton = dialog.findViewById<MaterialButton>(R.id.cancel)
        val courseCode = dialog.findViewById<EditText>(R.id.editTextClassCode)


        canceButton.setOnClickListener { dialog.dismiss() }
        dialogButton.setOnClickListener {

            if (courseCode.text.isNullOrBlank()) {

                courseCode.error = "Enter a valid course code"
            } else {

                joinClass(courseCode.text.toString())
                dialog.dismiss()
            }


        }

        dialog.show()


    }


    private fun joinClass(courseCode: String) {

        binding.progressBar.visible()
        viewModel.getCourseFromCode(courseCode)


    }


}
