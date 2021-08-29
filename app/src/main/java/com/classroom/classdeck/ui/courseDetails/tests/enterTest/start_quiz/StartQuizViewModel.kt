package com.classroom.classdeck.ui.courseDetails.tests.enterTest.start_quiz

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.classroom.classdeck.data.model.*
import com.classroom.classdeck.data.repository.StartQuizRepository
import com.classroom.classdeck.util.NetworkHelper
import com.classroom.classdeck.util.ResponseState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class StartQuizViewModel @Inject constructor(
    val repository: StartQuizRepository,
    val networkHelper: NetworkHelper
) : ViewModel() {


    private var _quizQuestions: MutableLiveData<ResponseState<List<Question?>>> =
        MutableLiveData()
    val quizQuestions: LiveData<ResponseState<List<Question?>>> get() = _quizQuestions


    private var _submitQuizStatus: MutableLiveData<ResponseState<Boolean>> =
        MutableLiveData()
    val submitQuizStatus: LiveData<ResponseState<Boolean>> get() = _submitQuizStatus


    fun getQuizQuestions(contest: Quiz, course: Course) = viewModelScope.launch {

        _quizQuestions.postValue(repository.getQuizQuestions(contest.id, course).value)


    }


    fun userCompletedContest(contest: Quiz, contestUsr: QuizUser, user: User?, course: Course) =
        viewModelScope.launch {


            _submitQuizStatus.postValue(
                repository.submitQuiz(
                    contest,
                    contestUsr,
                    user,
                    course
                ).value
            )


        }


}
