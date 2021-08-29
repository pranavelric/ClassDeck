package com.classroom.classdeck.ui.dashBorad

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.classroom.classdeck.data.model.*
import com.classroom.classdeck.data.repository.CourseDetailsRepository
import com.classroom.classdeck.data.repository.DashBoardRepository
import com.classroom.classdeck.util.ResponseState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class DashBoardViewModel @Inject constructor(val courseDetailsRepository: DashBoardRepository) :
    ViewModel() {


    private val _getAnnouncement: MutableLiveData<ResponseState<List<Announcements?>>> =
        MutableLiveData()
    val getAnnouncement: LiveData<ResponseState<List<Announcements?>>> get() = _getAnnouncement

    private val _getAssignment: MutableLiveData<ResponseState<List<Assignments?>>> =
        MutableLiveData()
    val getAssignment: LiveData<ResponseState<List<Assignments?>>> get() = _getAssignment


    private val _getEvents: MutableLiveData<ResponseState<List<Event?>>> =
        MutableLiveData()
    val getEvents: LiveData<ResponseState<List<Event?>>> get() = _getEvents


    private val _getCourse: MutableLiveData<ResponseState<List<Course?>>> =
        MutableLiveData()
    val getCourse: LiveData<ResponseState<List<Course?>>> get() = _getCourse


    private val _getAllCourse: MutableLiveData<ResponseState<List<Course?>>> =
        MutableLiveData()
    val getAllCourse: LiveData<ResponseState<List<Course?>>> get() = _getAllCourse


    private val _getAllQuiz: MutableLiveData<ResponseState<List<QuizUser?>>> =
        MutableLiveData()
    val getAllQuiz: LiveData<ResponseState<List<QuizUser?>>> get() = _getAllQuiz




    fun getAnnouncements(courseCode: String) = viewModelScope.launch {
        _getAnnouncement.postValue(courseDetailsRepository.getCourseAnnouncements(courseCode).value)
    }

    fun getAssignments(courseCode: String) = viewModelScope.launch {
        _getAssignment.postValue(courseDetailsRepository.getCourseAssignments(courseCode).value)
    }

    fun getEvents(courseCode: String) = viewModelScope.launch {
        _getEvents.postValue(courseDetailsRepository.getCourseEvent(courseCode).value)
    }


    fun getCourse(courseCode: String) = viewModelScope.launch {
        _getCourse.postValue(courseDetailsRepository.getCourseListOfStudent(courseCode).value)
    }
    fun getAllCourse() = viewModelScope.launch {
        _getAllCourse.postValue(courseDetailsRepository.getAllCourse().value)
    }


    fun getAllQuizs(userId:String) = viewModelScope.launch {
        _getAllQuiz.postValue(courseDetailsRepository.getQAllQuizs(userId).value)
    }

}