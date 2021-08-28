package com.classroom.classdeck.ui.register

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.classroom.classdeck.data.model.User
import com.classroom.classdeck.data.repository.AuthRepository
import com.classroom.classdeck.util.ResponseState
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class RegistrationViewModel @Inject constructor(val authRepository: AuthRepository) :
    ViewModel() {

    private var _authenticateUserLiveData: MutableLiveData<ResponseState<User>> = MutableLiveData()
    private var _createdUserLiveData: MutableLiveData<ResponseState<User>> = MutableLiveData()

    val createdUserLiveData: LiveData<ResponseState<User>> get() = _createdUserLiveData
    val authenticateUserLiveData: LiveData<ResponseState<User>> get() = _authenticateUserLiveData


    fun registerUserWithEmailPass(username:String,email: String, pass: String,mobile:String) {
        _authenticateUserLiveData = authRepository.firebaseRegisterUserWithEmailPass(username,email, pass,mobile)
    }


    fun createUser(authenticatedUser: User,isStudent:Boolean) {
        _createdUserLiveData = authRepository.createUserInFireStoreIfNotExist(authenticatedUser,isStudent)
    }


}