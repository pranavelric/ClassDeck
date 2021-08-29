package com.classroom.classdeck.ui.courseDetails.tests

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.classroom.classdeck.data.model.Question
import com.classroom.classdeck.data.model.Quiz
import com.classroom.classdeck.data.repository.QuizContestRepository
import com.classroom.classdeck.util.ResponseState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class QuizViewModel @Inject constructor(
    val repository: QuizContestRepository
) : ViewModel() {


    private var _allQuizQuizs: MutableLiveData<ResponseState<List<Quiz?>>> = MutableLiveData()
    val allQuizQuizs: LiveData<ResponseState<List<Quiz?>>> get() = _allQuizQuizs


    private var _clickedQuizs: MutableLiveData<ResponseState<Quiz?>> = MutableLiveData()
    val clickedQuizs: LiveData<ResponseState<Quiz?>> get() = _clickedQuizs


    private var _createQuiz: MutableLiveData<ResponseState<Quiz?>> = MutableLiveData()
    val createQuizStatus: LiveData<ResponseState<Quiz?>> get() = _createQuiz

    private var _quizQuestions: MutableLiveData<ResponseState<List<Question?>>> = MutableLiveData()
    val quizQuestions: LiveData<ResponseState<List<Question?>>> get() = _quizQuestions

    private var _createQuizQuestions: MutableLiveData<ResponseState<String>> = MutableLiveData()
    val createQuizQuestionsStatus: LiveData<ResponseState<String>> get() = _createQuizQuestions


    fun getAllQuizQuiz(courseId: String) = viewModelScope.launch {
        _allQuizQuizs.postValue(repository.getAllQuizContests(courseId).value)
    }


    fun getQuiz(courseId: String, quizId: String) = viewModelScope.launch {

        _clickedQuizs.postValue(repository.getContest(courseId, quizId).value)
    }

    fun createQuizContest(courseId: String,contest:Quiz) = viewModelScope.launch {
        _createQuiz.postValue(repository.createQuizContest(courseId,contest).value)
    }
    fun addQuestionToContest(courseId: String,quizId: String, mQuestion: Question) = viewModelScope.launch {
        _createQuizQuestions.postValue(repository.createQuizQuestions(courseId,quizId, mQuestion).value)
    }

    fun getQuizQuestions(courseId: String,contestId: String) = viewModelScope.launch {
        _quizQuestions.postValue(repository.getQuizQuestions(courseId,contestId).value)
    }


}