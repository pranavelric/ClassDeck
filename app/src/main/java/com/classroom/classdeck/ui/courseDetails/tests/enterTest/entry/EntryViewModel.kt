package com.classroom.classdeck.ui.courseDetails.tests.enterTest.entry


import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.classroom.classdeck.data.model.Course
import com.classroom.classdeck.data.model.Quiz
import com.classroom.classdeck.data.model.QuizUser
import com.classroom.classdeck.data.repository.QuizEntryRepository
import com.classroom.classdeck.util.NetworkHelper
import com.classroom.classdeck.util.ResponseState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class EntryViewModel @Inject constructor(
    val repository: QuizEntryRepository,
    val networkHelper: NetworkHelper
) : ViewModel() {


    private var _userContest: MutableLiveData<ResponseState<QuizUser?>> =
        MutableLiveData()
    val userContest: LiveData<ResponseState<QuizUser?>> get() = _userContest


    private var _userPracticeContest: MutableLiveData<ResponseState<QuizUser?>> =
        MutableLiveData()
    val userPracticeContest: LiveData<ResponseState<QuizUser?>> get() = _userPracticeContest


    fun getUserContest(userId: String, contestId: String) = viewModelScope.launch {
        _userContest.postValue(repository.getUserContest(userId, contestId).value)
    }

    fun setGameStarted(contest: Quiz, course: Course) = viewModelScope.launch {


        repository.setQuizStarted(contest.id, course.courseCode)


    }


}