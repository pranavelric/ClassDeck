package com.classroom.classdeck.ui.splash

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.classroom.classdeck.data.model.User
import com.classroom.classdeck.util.ResponseState
import com.gaming.earningvalley.data.repository.SplashRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class SplashViewModel @Inject constructor(val authRepository: SplashRepository) :
    ViewModel() {


    private var _authenticateUserLiveData: MutableLiveData<ResponseState<User?>> = MutableLiveData()
    private var _userLiveData: MutableLiveData<ResponseState<User>> = MutableLiveData()


    val authenticatedUserLiveData: LiveData<ResponseState<User?>> get() = _authenticateUserLiveData
    val userLiveData: LiveData<ResponseState<User>> get() = _userLiveData
    fun checkIfUserIsAuthenticated() {

        _authenticateUserLiveData = authRepository.checkIfUserIsAuthenticatedInFirebase()
    }

    fun setUid(uid: String) {
        _userLiveData = authRepository.addUserToLiveData(uid)
    }

}