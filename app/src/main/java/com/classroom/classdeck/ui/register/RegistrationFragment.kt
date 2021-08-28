package com.classroom.classdeck.ui.register

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.classroom.classdeck.R
import com.classroom.classdeck.data.model.User
import com.classroom.classdeck.databinding.RegistrationFragmentBinding
import com.classroom.classdeck.util.*
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class RegistrationFragment : Fragment() {


    private val viewModel: RegistrationViewModel by lazy {
        ViewModelProvider(this).get(RegistrationViewModel::class.java)
    }
    private lateinit var binding: RegistrationFragmentBinding



    var userType: String = "";
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        arguments?.let {
            userType = (it.getSerializable(Constants.USERS_BUNDLE_OBJ) as String?).toString()
        }

    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = RegistrationFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setClickListeners()
    }

    private fun setClickListeners() {

        binding.layoutRegister.loginText.setOnClickListener {
            goToLogin()
        }
        binding.layoutRegister.cirSignUpButton.setOnClickListener {
            if (binding.layoutRegister.editTextEmail.text.isNullOrBlank()) {
                binding.layoutRegister.editTextEmail.error = "Please enter an email"
            } else if (binding.layoutRegister.editTextUsername.text.isNullOrBlank()) {
                binding.layoutRegister.editTextUsername.error = "Please enter your fullname"
            } else if (binding.layoutRegister.editTextPassword.text.isNullOrBlank()) {
                binding.layoutRegister.editTextPassword.error = "Please enter a password"
            } else if (binding.layoutRegister.editTextMobile.text.isNullOrBlank()) {
                binding.layoutRegister.editTextMobile.error = "Please enter your mobile number"
            } else {
                registerUser(
                    binding.layoutRegister.editTextUsername.text.toString(),
                    binding.layoutRegister.editTextEmail.text.toString(),
                    binding.layoutRegister.editTextPassword.text.toString(),
                    binding.layoutRegister.editTextMobile.text.toString()
                )
            }


        }

    }

    private fun registerUser(username: String, email: String, password: String, mobile: String) {
        binding.progressBar.visible()
        viewModel.registerUserWithEmailPass(username, email, password, mobile)
        viewModel.authenticateUserLiveData.observe(viewLifecycleOwner, { userState ->
            when (userState) {
                is ResponseState.Success -> {
                    if (userState.data != null)
                        if (userState.data.isNew == true) {
                            createNewUser(userState.data)
                        } else {
                            goToMainFragment(userState.data)
                        }
                }
                is ResponseState.Error -> {

                }
                is ResponseState.Loading -> {

                }

            }

        })


    }


    private fun goToMainFragment(authenticatedUser: User?) {
        binding.progressBar.gone()
        val bundle = Bundle().apply {
            putSerializable(Constants.USERS_BUNDLE_OBJ, authenticatedUser!!)
        }
        findNavController().navigate(R.id.action_registrationFragment_to_homeFragment, bundle)

    }
    private fun createNewUser(authenticatedUser: User?) {

        if (authenticatedUser != null) {
            viewModel.createUser(authenticatedUser,userType=="student")
        } else {
            binding.progressBar.gone()
            context?.toast("Some error occured")
        }
        viewModel.createdUserLiveData.observe(viewLifecycleOwner, { user ->
            when (user) {
                is ResponseState.Success -> {
                    if (user.data != null) {

                        goToMainFragment(user.data)
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



    private fun goToLogin() {

        val bundle = Bundle().apply {
            putSerializable(Constants.USERS_BUNDLE_OBJ, userType)
        }


        findNavController().navigate(R.id.action_registrationFragment_to_loginFragment,bundle)
    }

}