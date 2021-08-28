package com.classroom.classdeck.ui.splash

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.classroom.classdeck.R
import com.classroom.classdeck.data.model.User
import com.classroom.classdeck.databinding.FragmentSplashBinding
import com.classroom.classdeck.ui.activity.MainActivity
import com.classroom.classdeck.util.*
import com.classroom.classdeck.util.Constants.USERS_BUNDLE_OBJ

import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SplashFragment : Fragment() {

    private val splashViewModel: SplashViewModel by lazy {
        ViewModelProvider(this).get(SplashViewModel::class.java)
    }
    private lateinit var binding: FragmentSplashBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSplashBinding.inflate(inflater, container, false)
        (activity as MainActivity).setFullScreen()
        checkIfUserIsAuthenticated()

        return binding.root
    }


    private fun checkIfUserIsAuthenticated() {

        binding.progressBar.visible()

        splashViewModel.checkIfUserIsAuthenticated()
        splashViewModel.authenticatedUserLiveData.observe(viewLifecycleOwner, { user ->

            when (user) {
                is ResponseState.Success -> {
                    if (user.data != null) {
                        getUserFromDatabase(user.data.uid)
                    } else {
                        goToAuthFragment()
                    }
                }

                is ResponseState.Error -> {
                    user.message?.let { context?.toast(it) }
                }
                is ResponseState.Loading -> {
                }
            }
        })


    }

    private fun getUserFromDatabase(uid: String) {
        splashViewModel.setUid(uid)
        splashViewModel.userLiveData.observe(viewLifecycleOwner, { user ->
            when (user) {
                is ResponseState.Success -> {
                    if (user.data != null)
                        goToMainActivity(user.data)
                }

                is ResponseState.Error -> {
                    user.message?.let { context?.toast(it) }
                }
                is ResponseState.Loading -> {

                }
            }
        })
    }

    private fun goToMainActivity(user: User) {

        val bundle = Bundle().apply {
            putSerializable(USERS_BUNDLE_OBJ, user)
        }
        CoroutinesHelper.delayWithMain(2000L) {
            binding.progressBar.gone()
            findNavController().navigate(R.id.action_splashFragment_to_homeFragment, bundle)
        }
    }

    private fun goToAuthFragment() {

        CoroutinesHelper.delayWithMain(2000L) {
            binding.progressBar.gone()
            findNavController().navigate(R.id.action_splashFragment_to_selectTypeFragment)
        }
    }


    override fun onStart() {
        super.onStart()
        (activity as MainActivity).setFullScreen()
    }


}