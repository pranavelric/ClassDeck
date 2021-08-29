package com.classroom.classdeck.data.repository

import androidx.lifecycle.MutableLiveData
import com.classroom.classdeck.data.model.Course
import com.classroom.classdeck.data.model.RankingModel
import com.classroom.classdeck.data.model.RankingModelList
import com.classroom.classdeck.data.model.ResultModel
import com.classroom.classdeck.util.Constants
import com.classroom.classdeck.util.ResponseState
import com.classroom.classdeck.util.getTodaysDate
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.tasks.await
import javax.inject.Inject


class QuizResultRepository @Inject constructor() {


    private val rootRef: FirebaseFirestore = FirebaseFirestore.getInstance()
    private val courseRef: CollectionReference =
        rootRef.collection(Constants.COURSE)


    val resultList = ArrayList<ResultModel?>()
    suspend fun getQuizResult(
        contestId: String,
        courseId: String
    ): MutableLiveData<ResponseState<List<ResultModel?>>> {
        val updateUserLiveData: MutableLiveData<ResponseState<List<ResultModel?>>> =
            MutableLiveData()

        resultList.clear()
  courseRef.document(courseId).collection(Constants.QUIZ).document(contestId).collection(Constants.RESULTS)
            .orderBy("correct", Query.Direction.DESCENDING).get()
            .addOnCompleteListener { task ->

                if (task.isSuccessful) {
                    if (task.result != null && !task.result!!.isEmpty) {


                        for (doc: DocumentSnapshot in task.result!!.documents) {
                            val obj = doc.toObject(ResultModel::class.java)

                            resultList.add(obj)
                        }

                        updateUserLiveData.value = ResponseState.Success(resultList)

                    } else {
                        updateUserLiveData.value = ResponseState.Error("No results found")

                    }

                } else {
                    updateUserLiveData.value =
                        ResponseState.Error("Some error occured: ${task.exception?.message}")

                }


            }.await()

        return updateUserLiveData
    }


    suspend fun addQuizRanking(
        id: String,
        userList: ArrayList<RankingModel>,
        course: Course
    ): MutableLiveData<ResponseState<Boolean>> {
        val updateUserLiveData: MutableLiveData<ResponseState<Boolean>> = MutableLiveData()

        val rankingModelList = RankingModelList()
        rankingModelList.rankings = userList
        courseRef.document(course.courseCode).collection(Constants.QUIZ).document(id).collection(Constants.RESULTS).document(Constants.RANKING)
            .set(rankingModelList)
            .addOnCompleteListener {

                if (it.isSuccessful) {
                    updateUserLiveData.value = ResponseState.Success(true)
                } else {
                    updateUserLiveData.value =
                        ResponseState.Error("Some error occured: ${it.exception?.message}")
                }

            }.await()
        return updateUserLiveData
    }


}