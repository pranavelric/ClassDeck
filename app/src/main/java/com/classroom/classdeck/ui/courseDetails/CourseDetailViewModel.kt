package com.classroom.classdeck.ui.courseDetails

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.classroom.classdeck.data.model.Course
import com.classroom.classdeck.data.model.User
import com.classroom.classdeck.data.repository.CourseDetailsRepository
import com.classroom.classdeck.util.ResponseState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class CourseDetailViewModel   @Inject constructor(val courseDetailsRepository: CourseDetailsRepository): ViewModel() {

    private var _userLiveData: MutableLiveData<ResponseState<User>> = MutableLiveData()
    val userLiveData: LiveData<ResponseState<User>> get() = _userLiveData


    private val _getCourseFromCode: MutableLiveData<ResponseState<Course?>> = MutableLiveData()
    val getCourseFromCode: LiveData<ResponseState<Course?>> get() = _getCourseFromCode

    fun getUserFromDataBase(uid: String) {
        _userLiveData = courseDetailsRepository.getUser(uid)
    }

    fun getCourseFromCode(courseCode: String) = viewModelScope.launch {
        _getCourseFromCode.postValue(courseDetailsRepository.getCourseFromCode(courseCode).value)
    }




}