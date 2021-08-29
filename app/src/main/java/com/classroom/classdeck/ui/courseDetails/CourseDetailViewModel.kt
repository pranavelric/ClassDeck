package com.classroom.classdeck.ui.courseDetails

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.classroom.classdeck.data.model.*
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


    //events
    private val _addEvents: MutableLiveData<ResponseState<String?>> = MutableLiveData()
    val addEvents:LiveData<ResponseState<String?>> get() = _addEvents

    private val _getEvents: MutableLiveData<ResponseState<List<Event?>>> =
        MutableLiveData()
    val getEvents: LiveData<ResponseState<List<Event?>>> get() = _getEvents


    fun getUserFromDataBase(uid: String) {
        _userLiveData = courseDetailsRepository.getUser(uid)
    }

    fun getCourseFromCode(courseCode: String) = viewModelScope.launch {
        _getCourseFromCode.postValue(courseDetailsRepository.getCourseFromCode(courseCode).value)
    }


    fun addAnnouncements(announcements: Announcements,userId:String) = viewModelScope.launch {
        _addAnnouncement.postValue(courseDetailsRepository.addAnnouncement(announcements,userId).value)
    }


    fun getAnnouncements(courseCode: String) = viewModelScope.launch {
        _getAnnouncement.postValue(courseDetailsRepository.getCourseAnnouncements(courseCode).value)
    }


    fun addAssignments(announcements: Assignments,userId: String) = viewModelScope.launch {
        _addAssignment.postValue(courseDetailsRepository.addAssignment(announcements,userId).value)
    }


    fun getAssignments(courseCode: String) = viewModelScope.launch {
        _getAssignment.postValue(courseDetailsRepository.getCourseAssignments(courseCode).value)
    }



    fun addEvents(announcements: Event,userId: String) = viewModelScope.launch {
        _addEvents.postValue(courseDetailsRepository.addEvent(announcements,userId).value)
    }


    fun getEvents(courseCode: String) = viewModelScope.launch {
        _getEvents.postValue(courseDetailsRepository.getCourseEvent(courseCode).value)
    }




}