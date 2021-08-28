package com.classroom.classdeck.ui.home

import android.content.Context
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import com.classroom.classdeck.databinding.HomeFragmentBinding
import dagger.hilt.android.AndroidEntryPoint
import android.graphics.drawable.Drawable


import android.widget.LinearLayout

import androidx.cardview.widget.CardView

import android.text.TextUtils
import android.view.MenuItem
import android.widget.ImageView

import android.widget.TextView
import androidx.navigation.fragment.findNavController
import com.classroom.classdeck.R
import com.classroom.classdeck.adapters.CourseAdapter
import com.classroom.classdeck.data.model.Course
import com.classroom.classdeck.data.model.User
import com.classroom.classdeck.ui.activity.MainActivity
import com.classroom.classdeck.util.Constants
import com.classroom.classdeck.util.Constants.USERS_BUNDLE_OBJ
import com.classroom.classdeck.util.setFullScreenWithBtmNav
import com.classroom.classdeck.util.share
import com.classroom.classdeck.util.toast
import me.ibrahimsn.lib.OnItemSelectedListener
import javax.inject.Inject


@AndroidEntryPoint
class HomeFragment : Fragment() {


    private val viewModel: HomeViewModel by lazy {
        ViewModelProvider(this).get(HomeViewModel::class.java)
    }




    var user:User? =null;
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        arguments?.let {
            user = (it.getSerializable(Constants.USERS_BUNDLE_OBJ) as User?)
        }

    }

    @Inject
    lateinit var courseAdapter: CourseAdapter
    private lateinit var binding: HomeFragmentBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = HomeFragmentBinding.inflate(inflater, container, false)

        binding.toolbar.setOnMenuItemClickListener {
            onOptionsItemSelected(it)
        }

        setHasOptionsMenu(true)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)



        setRecyclerView()
        setData()
        observeData()
        setMyClickListeners()
    }

    private fun observeData() {

    }

    private fun setMyClickListeners(){

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

//                        findNavController().navigate(
//                            R.id.action_mainFragment_to_referFragment,
//                            bundle
//                        )
                        return true
                    }

                    2 -> {
                        val bundle = Bundle().apply {
                            putSerializable(USERS_BUNDLE_OBJ, user)
                        }

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




    }

    private fun setData() {





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



            else -> {
                return false
            }
        }
    }













}