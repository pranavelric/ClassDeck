package com.classroom.classdeck.ui.courseDetails.tests.enterTest.result


import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.classroom.classdeck.data.model.Course
import com.classroom.classdeck.data.model.Quiz
import com.classroom.classdeck.data.model.RankingModel
import com.classroom.classdeck.data.model.ResultModel
import com.classroom.classdeck.data.repository.QuizResultRepository
import com.classroom.classdeck.util.NetworkHelper
import com.classroom.classdeck.util.ResponseState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class QuizResultViewModel @Inject constructor(
    val repository: QuizResultRepository,
    val networkHelper: NetworkHelper
) : ViewModel() {


    private var _quizResult: MutableLiveData<ResponseState<List<ResultModel?>>> =
        MutableLiveData()
    val quizResult: LiveData<ResponseState<List<ResultModel?>>> get() = _quizResult


    private var _submitQuizRanking: MutableLiveData<ResponseState<Boolean>> =
        MutableLiveData()
    val submitQuizRanking: LiveData<ResponseState<Boolean>> get() = _submitQuizRanking


    fun getQuizResult(contest: Quiz, course: Course) = viewModelScope.launch {

        _quizResult.postValue(repository.getQuizResult(contest.id, course.courseCode).value)


    }


    fun addUserRanking(contest: Quiz, userList: ArrayList<RankingModel>, course: Course) =
        viewModelScope.launch {


            _submitQuizRanking.postValue(
                repository.addQuizRanking(
                    contest.id,
                    userList,
                    course
                ).value
            )


        }


}