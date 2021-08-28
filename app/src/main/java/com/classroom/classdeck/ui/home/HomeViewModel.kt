package com.classroom.classdeck.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.classroom.classdeck.data.model.Course
import com.classroom.classdeck.data.model.User
import com.classroom.classdeck.data.repository.HomeRepository
import com.classroom.classdeck.util.ResponseState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class HomeViewModel @Inject constructor(val homeRepository: HomeRepository) : ViewModel() {


    private var _userLiveData: MutableLiveData<ResponseState<User>> = MutableLiveData()
    val userLiveData: LiveData<ResponseState<User>> get() = _userLiveData

    private var _notifSizeLiveData: MutableLiveData<ResponseState<Int>> = MutableLiveData()
    val notifSizeLiveData: LiveData<ResponseState<Int>> get() = _notifSizeLiveData


    private val _joinClassLiveData: MutableLiveData<ResponseState<String>> = MutableLiveData()
    val joinClassLiveData: LiveData<ResponseState<String>> get() = _joinClassLiveData

    private val _getCourseFromCode: MutableLiveData<ResponseState<Course?>> = MutableLiveData()
    val getCourseFromCode: LiveData<ResponseState<Course?>> get() = _getCourseFromCode


    private val _getCourseList: MutableLiveData<ResponseState<List<Course?>>> = MutableLiveData()
    val getCourseList: LiveData<ResponseState<List<Course?>>> get() = _getCourseList


    private val _addCourse: MutableLiveData<ResponseState<String?>> = MutableLiveData()
    val addCourse: LiveData<ResponseState<String?>> get() = _addCourse


    fun getUserFromDataBase(uid: String) {
        _userLiveData = homeRepository.getUser(uid)
    }


    fun notifSize(userId: String) = viewModelScope.launch {
        _notifSizeLiveData.postValue(homeRepository.getNotificationsSize(userId).value)
    }

    fun joinClass(course: Course, userId: User) = viewModelScope.launch {
        _joinClassLiveData.postValue(homeRepository.joinClass(course, userId).value)
    }

    fun getCourseFromCode(courseCode: String) = viewModelScope.launch {
        _getCourseFromCode.postValue(homeRepository.getCourseFromCode(courseCode).value)
    }


    fun getAllStudentCourses(userId: String) = viewModelScope.launch {
        _getCourseList.postValue(homeRepository.getCourseListOfStudent(userId).value)
    }

    fun addCourse(course: Course, user: User) = viewModelScope.launch {

        _addCourse.postValue(homeRepository.addCourse(course, user).value)

    }


}