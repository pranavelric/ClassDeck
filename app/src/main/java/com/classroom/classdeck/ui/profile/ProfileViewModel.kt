package com.classroom.classdeck.ui.profile

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.classroom.classdeck.data.model.User
import com.classroom.classdeck.data.repository.ProfileRepository
import com.classroom.classdeck.util.ResponseState
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(val profileRepository: ProfileRepository) :ViewModel(){



    private var _userLiveData: MutableLiveData<ResponseState<User>> = MutableLiveData()
    val userLiveData: LiveData<ResponseState<User>> get() = _userLiveData


    private var _uploadLiveData: MutableLiveData<ResponseState<String>> = MutableLiveData()
    val uploadLiveData: LiveData<ResponseState<String>> get() = _uploadLiveData



    fun getUserFromDataBase(uid: String) {
        _userLiveData = profileRepository.getUser(uid)
    }

    fun uploadImageInFirebaseStorage(uid: String, uri: Uri) {
        _uploadLiveData = profileRepository.uploadImageToFirebaseStorage(uid, uri)
    }


}