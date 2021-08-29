package com.classroom.classdeck.ui.courseDetails.tests.enterTest

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.classroom.classdeck.data.model.*
import com.classroom.classdeck.data.repository.QuizContestInfoRepository
import com.classroom.classdeck.util.ResponseState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class QuizEntryViewModel @Inject constructor(
    val repository: QuizContestInfoRepository
) : ViewModel() {


    private var _allQuizQuizs: MutableLiveData<ResponseState<List<Quiz?>>> = MutableLiveData()
    val allQuizQuizs: LiveData<ResponseState<List<Quiz?>>> get() = _allQuizQuizs


    private var _quizQuestions: MutableLiveData<ResponseState<List<Question?>>> = MutableLiveData()
    val quizQuestions: LiveData<ResponseState<List<Question?>>> get() = _quizQuestions


    private var _quizEntryStatus: MutableLiveData<ResponseState<String?>> =
        MutableLiveData()
    val quizEntryStatus: LiveData<ResponseState<String?>> get() = _quizEntryStatus



    private var _isUserRegistered: MutableLiveData<ResponseState<Boolean?>> =
        MutableLiveData()
    val isUserResgistered: LiveData<ResponseState<Boolean?>> get() = _isUserRegistered


    private var _getUserRanking: MutableLiveData<ResponseState<RankingModelList?>> =
        MutableLiveData()
    val getUserRanking: LiveData<ResponseState<RankingModelList?>> get() = _getUserRanking




    fun enterInQuiz(user: User, contest: Quiz,course: Course) = viewModelScope.launch {

        _quizEntryStatus.postValue(repository.enterInContest(user, contest,course).value)


    }


    fun isUserEnteredInContest(user: User, contest: Quiz,course: Course) = viewModelScope.launch {

                _isUserRegistered.postValue(
                    repository.checkIfAlreadyRegisteredInContest(
                        user.uid,
                        contest.id,course
                    ).value
                )

    }

    fun getUserRanking(contest: Quiz,course: Course) = viewModelScope.launch {

                _getUserRanking.postValue(repository.getUsersRanking(contest.id,course).value)


    }




}