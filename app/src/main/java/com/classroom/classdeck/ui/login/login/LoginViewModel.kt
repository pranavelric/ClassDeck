package com.gaming.earningvalley.ui.login

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.classroom.classdeck.data.model.User
import com.classroom.classdeck.data.repository.AuthRepository
import com.gaming.earningvalley.utils.ResponseState
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.PhoneAuthCredential
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(val authRepository: AuthRepository) :
    ViewModel() {

    private var _authenticateUserLiveData: MutableLiveData<ResponseState<User>> = MutableLiveData()
    private var _createdUserLiveData: MutableLiveData<ResponseState<User>> = MutableLiveData()

    val createdUserLiveData: LiveData<ResponseState<User>> get() = _createdUserLiveData
    val authenticateUserLiveData: LiveData<ResponseState<User>> get() = _authenticateUserLiveData

    private var _getUserLiveData: MutableLiveData<ResponseState<User>> = MutableLiveData()
    val getUserLiveData: LiveData<ResponseState<User>> get() = _getUserLiveData



    fun signInWithGoogle(googleAuthCredential: AuthCredential) {
        _authenticateUserLiveData = authRepository.firebaseSignInWithGoogle(googleAuthCredential)
    }

    fun signInWithEmailPass(email: String, pass: String) {
        _authenticateUserLiveData = authRepository.firebaseSignInWithEmailPass(email, pass)
    }

    fun signInWithPhoneNumber(credential: PhoneAuthCredential) {
        _authenticateUserLiveData = authRepository.firebaseSignInWithPhone(credential)
    }

    fun createUser(authenticatedUser: User) {
        _createdUserLiveData = authRepository.createUserInFireStoreIfNotExist(authenticatedUser)
    }

    fun getUser(uid: String) {
        _getUserLiveData = authRepository.getUser(uid)
    }

    fun updateUserIsNew(id: User, b: Boolean) {

        authRepository.updateUserIsNew(id,b)

    }


}