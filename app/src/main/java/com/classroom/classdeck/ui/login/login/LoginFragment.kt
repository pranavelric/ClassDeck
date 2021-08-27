package com.gaming.earningvalley.ui.login

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.common.api.ApiException
import com.google.firebase.FirebaseException
import com.google.firebase.FirebaseTooManyRequestsException
import com.google.firebase.auth.*
import com.classroom.classdeck.data.model.User
import com.gaming.earningvalley.databinding.FragmentLoginBinding
import com.gaming.earningvalley.ui.activities.MainActivity
import com.gaming.earningvalley.utils.*
import com.gaming.earningvalley.utils.Constants.USERS_BUNDLE_OBJ
import dagger.hilt.android.AndroidEntryPoint

import java.util.concurrent.TimeUnit
import javax.inject.Inject

@AndroidEntryPoint
class LoginFragment : Fragment() {


    private lateinit var binding: FragmentLoginBinding
    private val loginViewModel: LoginViewModel by lazy {
        ViewModelProvider(this).get(LoginViewModel::class.java)
    }


    @Inject
    lateinit var sharedPrefrences: MySharedPrefrences
    private var inivationID: String = ""


    companion object {
        const val RC_SIGN_IN = 121
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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        checkForInvitation()
    }

    private fun  checkForInvitation() {
        if (sharedPrefrences.getInvitationId() != null) {
            inivationID = sharedPrefrences.getInvitationId()!!
        }
    }

    private fun getData() {
    }

    private fun setClickListeners() {
        binding.phoneLog.setOnClickListener {
            binding.phoneLog.gone()
            binding.emailLog.gone()
            binding.googleSignInButton.gone()
            binding.layoutPhone.root.visible()


        }
        binding.emailLog.setOnClickListener {
            binding.phoneLog.gone()
            binding.emailLog.gone()
            binding.googleSignInButton.gone()
            binding.layoutLogin.root.visible()
        }
        binding.layoutLogin.cirLoginButton.setOnClickListener {
            if (binding.layoutLogin.editTextEmail.text.isNullOrBlank()) {
                binding.layoutLogin.editTextEmail.error = "Please enter an email."
            } else if (binding.layoutLogin.editTextPassword.text.isNullOrBlank()) {
                binding.layoutLogin.editTextPassword.error = "Please enter password."
            } else {
                signInUsingEmailPassword(
                    binding.layoutLogin.editTextEmail.text.toString(),
                    binding.layoutLogin.editTextPassword.text.toString()
                )
            }

        }

        binding.layoutPhone.cirPhoneLoginButton.setOnClickListener {
            if (binding.layoutPhone.editTextPhone.text.isNullOrBlank()) {
                binding.layoutPhone.editTextPhone.error = "Please enter your phone number"
            } else {
                signInUsingPhoneNumber(binding.layoutPhone.editTextPhone.text.toString())
                binding.layoutPhone.root.gone()
                binding.layoutOtp.root.visible()
            }
        }

//        binding.googleSignInButton.setOnClickListener {
//            signInUsingGoolge()
//        }
        binding.layoutLogin.textSignup.setOnClickListener {
            goToRegister()
        }
        binding.layoutPhone.textSignupPhone.setOnClickListener {
            goToRegister()
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
                            updateUserIsNew(authenticatedUser.data,false)
                            goToMainFragment(authenticatedUser.data)
                        }
                }
                is ResponseState.Loading -> {
                }
            }
        })

    }

    private fun updateUserIsNew(b1: User, b: Boolean) {
        loginViewModel.updateUserIsNew(b1,b)
    }


    private fun signInUsingEmailPassword(email: String, password: String) {
        binding.progressBar.visible()
        loginViewModel.signInWithEmailPass(email, password)
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
                            goToMainFragment(authenticatedUser.data)
                        }
                }
                is ResponseState.Loading -> {
                }
            }
        })
    }

//    private fun signInUsingGoolge() {
//        val signInGoogleIntent = (activity as MainActivity).googleSignInClient.signInIntent
//        startActivityForResult(signInGoogleIntent, RC_SIGN_IN)
//    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == RC_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                // Google Sign In was successful, authenticate with Firebase
                val account = task.getResult(ApiException::class.java)!!
                Log.d("RRR", "firebaseAuthWithGoogle:" + account.id)
                if (account != null) {
                    getGoogleAuthCredential(account)
                }

            } catch (e: ApiException) {
                // Google Sign In failed, update UI appropriately
                context?.toast("Google sign in fialed")
                Log.w("RRR", "Google sign in failed", e)
            }
        }
    }

    private fun getGoogleAuthCredential(account: GoogleSignInAccount) {
        binding.progressBar.visible()
        val googleTokeId = account.idToken
        val googleAuthCredential = GoogleAuthProvider.getCredential(googleTokeId, null)
        signInWithGoogleAuthCredential(googleAuthCredential)
    }

    private fun signInWithGoogleAuthCredential(googleAuthCredential: AuthCredential) {

        loginViewModel.signInWithGoogle(googleAuthCredential)
        loginViewModel.authenticateUserLiveData.observe(viewLifecycleOwner, { authenticatedUser ->
            when (authenticatedUser) {
                is ResponseState.Error -> {
                    authenticatedUser.message?.let { context?.toast(it) }
                }
                is ResponseState.Success -> {
                    if (authenticatedUser.data != null)
                        if (authenticatedUser.data!!.isNew == true) {
                            createNewUser(authenticatedUser.data)
                        } else {
                            goToMainFragment(authenticatedUser.data)
                        }
                }
                is ResponseState.Loading -> {
                }
            }
        })

    }

    private fun createNewUser(authenticatedUser: User?) {
        binding.progressBar.visible()
        if (authenticatedUser != null) {
            loginViewModel.createUser(authenticatedUser)
        } else {
            binding.progressBar.gone()
            context?.toast("Some error occured")
        }
        loginViewModel.createdUserLiveData.observe(viewLifecycleOwner, { user ->

            when (user) {
                is ResponseState.Success -> {
                    binding.progressBar.gone()
                    if (user.data != null) {
                        if (user.data.isCreated) {
                            context?.toast("${user.data.name}")
                        }
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
                    findNavController().navigate(R.id.action_loginFragment_to_mainFragment, bundle)

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


    private fun goToRegister() {
        findNavController().navigate(R.id.action_loginFragment_to_registrationFragment)
    }


}