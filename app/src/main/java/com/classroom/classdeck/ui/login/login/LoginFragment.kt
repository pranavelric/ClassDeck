package com.classroom.classdeck.ui.login.login

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.classroom.classdeck.data.model.User
import com.classroom.classdeck.databinding.FragmentLoginBinding
import com.classroom.classdeck.ui.activity.MainActivity
import com.classroom.classdeck.util.*
import com.classroom.classdeck.util.Constants.USERS_BUNDLE_OBJ
import com.google.firebase.FirebaseException
import com.google.firebase.FirebaseTooManyRequestsException
import com.google.firebase.auth.*
import dagger.hilt.android.AndroidEntryPoint
import com.classroom.classdeck.R
import java.util.concurrent.TimeUnit

@AndroidEntryPoint
class LoginFragment : Fragment() {


    private lateinit var binding: FragmentLoginBinding
    private val loginViewModel: LoginViewModel by lazy {
        ViewModelProvider(this).get(LoginViewModel::class.java)
    }


    var userType: String = "";
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        arguments?.let {
            userType = (it.getSerializable(USERS_BUNDLE_OBJ) as String?).toString()
        }

    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentLoginBinding.inflate(inflater, container, false)
        getData()
        setClickListeners()
        return binding.root
    }


    private fun getData() {
    }

    private fun setClickListeners() {


        binding.layoutPhone.cirPhoneLoginButton.setOnClickListener {
            if (binding.layoutPhone.editTextPhone.text.isNullOrBlank()) {
                binding.layoutPhone.editTextPhone.error = "Please enter your phone number"
            } else {
                signInUsingPhoneNumber(binding.layoutPhone.editTextPhone.text.toString())
                binding.layoutPhone.root.gone()
                binding.layoutOtp.root.visible()
            }
        }


    }


    private fun signInUsingPhoneNumber(phoneNumber: String) {

        binding.layoutOtp.phonenumberText.text = "+91-${phoneNumber}"

        val callbacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            override fun onVerificationCompleted(credential: PhoneAuthCredential) {
                signInWithPhoneAuthCredential(credential)
            }

            override fun onVerificationFailed(e: FirebaseException) {
                if (e is FirebaseAuthInvalidCredentialsException) {
                    context?.toast_long("Invalid request")
                } else if (e is FirebaseTooManyRequestsException) {
                    context?.toast_long("SMS quota for project has been exceeded")
                }
            }

            override fun onCodeSent(
                verificationId: String,
                token: PhoneAuthProvider.ForceResendingToken
            ) {
                super.onCodeSent(verificationId, token)
                // Save verification ID and resending token so we can use them later
                val storedVerificationId = verificationId
                val resendToken = token
                binding.layoutOtp.continueOtp.setOnClickListener {
                    val code = binding.layoutOtp.pinView.text.toString()
                    val credential = PhoneAuthProvider.getCredential(verificationId!!, code)
                    signInWithPhoneAuthCredential(credential)
                }
            }
        }
        val auth = FirebaseAuth.getInstance()
        auth.useAppLanguage()
        val options = PhoneAuthOptions.newBuilder(auth)
            .setPhoneNumber("+91$phoneNumber")       // Phone number to verify
            .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
            .setActivity(activity as MainActivity)                 // Activity (for callback binding)
            .setCallbacks(callbacks)          // OnVerificationStateChangedCallbacks
            .build()
        PhoneAuthProvider.verifyPhoneNumber(options)
    }

    private fun signInWithPhoneAuthCredential(credential: PhoneAuthCredential) {

        binding.progressBar.visible()
        loginViewModel.signInWithPhoneNumber(credential)
        loginViewModel.authenticateUserLiveData.observe(viewLifecycleOwner, { authenticatedUser ->
            when (authenticatedUser) {
                is ResponseState.Error -> {
                    binding.progressBar.gone()
                    authenticatedUser.message?.let { context?.toast(it) }
                }
                is ResponseState.Success -> {
                    binding.progressBar.gone()
                    if (authenticatedUser.data != null)
                        if (authenticatedUser.data.isNew == true) {
                            createNewUser(authenticatedUser.data)
                        } else {
                            updateUserIsNew(authenticatedUser.data, false)
                            goToMainFragment(authenticatedUser.data)
                        }
                }
                is ResponseState.Loading -> {
                }
            }
        })

    }

    private fun updateUserIsNew(b1: User, b: Boolean) {
        loginViewModel.updateUserIsNew(b1, b)
    }


    private fun createNewUser(authenticatedUser: User?) {
        binding.progressBar.visible()
        if (authenticatedUser != null) {
            loginViewModel.createUser(authenticatedUser, userType=="student")
        } else {
            binding.progressBar.gone()
            context?.toast("Some error occured")
        }
        loginViewModel.createdUserLiveData.observe(viewLifecycleOwner, { user ->

            when (user) {
                is ResponseState.Success -> {
                    binding.progressBar.gone()
                    if (user.data != null) {

                        goToMainFragment(user.data)
                    }
                }
                is ResponseState.Error -> {
                    binding.progressBar.gone()
                    user.message?.let { context?.toast(it) }

                }
                is ResponseState.Loading -> {

                }
            }


        })
    }


    private fun goToMainFragment(authenticatedUser: User?) {

        binding.progressBar.visible()
        authenticatedUser?.uid?.let { loginViewModel.getUser(it) }
        loginViewModel.getUserLiveData.observe(viewLifecycleOwner, { userTask ->
            when (userTask) {
                is ResponseState.Success -> {

                    binding.progressBar.gone()
                    val bundle = Bundle().apply {
                        putSerializable(USERS_BUNDLE_OBJ, userTask.data!!)
                    }

                    //  findNavController().navigate(R.id.action_loginFragment_to_mainFragment, bundle)

                }
                is ResponseState.Error -> {
                    binding.progressBar.gone()
                    context?.toast("Some Error occured")
                }
                is ResponseState.Loading -> {

                }
            }

        })


    }


}