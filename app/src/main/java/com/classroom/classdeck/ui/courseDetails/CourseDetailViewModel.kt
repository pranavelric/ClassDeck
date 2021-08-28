package com.classroom.classdeck.ui.courseDetails

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.classroom.classdeck.data.model.Announcements
import com.classroom.classdeck.data.model.Assignments
import com.classroom.classdeck.data.model.Course
import com.classroom.classdeck.data.model.User
import com.classroom.classdeck.data.repository.CourseDetailsRepository
import com.classroom.classdeck.util.ResponseState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class CourseDetailViewModel @Inject constructor(val courseDetailsRepository: CourseDetailsRepository) :
    ViewModel() {

    private var _userLiveData: MutableLiveData<ResponseState<User>> = MutableLiveData()
    val userLiveData: LiveData<ResponseState<User>> get() = _userLiveData


    private val _getCourseFromCode: MutableLiveData<ResponseState<Course?>> = MutableLiveData()
    val getCourseFromCode: LiveData<ResponseState<Course?>> get() = _getCourseFromCode


    //announcement
    private val _addAnnouncement: MutableLiveData<ResponseState<String?>> = MutableLiveData()
    val addAnnouncement: LiveData<ResponseState<String?>> get() = _addAnnouncement

    private val _getAnnouncement: MutableLiveData<ResponseState<List<Announcements?>>> =
        MutableLiveData()
    val getAnnouncement: LiveData<ResponseState<List<Announcements?>>> get() = _getAnnouncement



    //assignments
    private val _addAssignment: MutableLiveData<ResponseState<String?>> = MutableLiveData()
    val addAssignment:LiveData<ResponseState<String?>> get() = _addAssignment

    private val _getAssignment: MutableLiveData<ResponseState<List<Assignments?>>> =
        MutableLiveData()
    val getAssignment: LiveData<ResponseState<List<Assignments?>>> get() = _getAssignment




    fun getUserFromDataBase(uid: String) {
        _userLiveData = courseDetailsRepository.getUser(uid)
    }

    fun getCourseFromCode(courseCode: String) = viewModelScope.launch {
        _getCourseFromCode.postValue(courseDetailsRepository.getCourseFromCode(courseCode).value)
    }


    fun addAnnouncements(announcements: Announcements) = viewModelScope.launch {
        _addAnnouncement.postValue(courseDetailsRepository.addAnnouncement(announcements).value)
    }


    fun getAnnouncements(courseCode: String) = viewModelScope.launch {
        _getAnnouncement.postValue(courseDetailsRepository.getCourseAnnouncements(courseCode).value)
    }


    fun addAssignments(announcements: Assignments) = viewModelScope.launch {
        _addAssignment.postValue(courseDetailsRepository.addAssignment(announcements).value)
    }


    fun getAssignments(courseCode: String) = viewModelScope.launch {
        _getAssignment.postValue(courseDetailsRepository.getCourseAssignments(courseCode).value)
    }




}