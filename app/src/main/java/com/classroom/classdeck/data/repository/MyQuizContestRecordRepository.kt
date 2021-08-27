package com.classroom.classdeck.data.repository

import androidx.lifecycle.MutableLiveData
import com.classroom.classdeck.data.model.Quiz
import com.classroom.classdeck.data.model.QuizUser
import com.classroom.classdeck.util.Constants
import com.classroom.classdeck.util.ResponseState
import com.classroom.classdeck.util.getTodaysDate
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class MyQuizContestRecordRepository
@Inject constructor() {


    private val rootRef: FirebaseFirestore = FirebaseFirestore.getInstance()
    private val quizRef: CollectionReference =
        rootRef.collection(Constants.QUIZ).document("Contest").collection(getTodaysDate())
    private val notificationsRef: CollectionReference = rootRef.collection(Constants.NOTIFICATIONS)
    private val megaeRef: CollectionReference =
        rootRef.collection(Constants.QUIZ).document("Contest").collection("Mega_Contest")
    private val usersRef: CollectionReference = rootRef.collection(Constants.USERS)


    val userContestsList = ArrayList<QuizUser?>()
    suspend fun getAllUserQuizs(userId: String): MutableLiveData<ResponseState<List<QuizUser?>>> {
        val updateUserLiveData: MutableLiveData<ResponseState<List<QuizUser?>>> =
            MutableLiveData()
        userContestsList.clear()
        usersRef.document(userId).collection(Constants.QUIZ).whereEqualTo("ended", true).get()
            .addOnCompleteListener { task ->

                if (task.isSuccessful) {
                    if (task.result != null && !task.result!!.isEmpty) {


                        for (doc: DocumentSnapshot in task.result!!.documents) {
                            val obj = doc.toObject(QuizUser::class.java)

                            userContestsList.add(obj)
                        }

                        updateUserLiveData.value = ResponseState.Success(userContestsList)

                    } else {
                        updateUserLiveData.value = ResponseState.Error("No Quizzes found")

                    }

                } else {
                    updateUserLiveData.value =
                        ResponseState.Error("Some error occured: ${task.exception?.message}")

                }


            }.await()

        return updateUserLiveData
    }


    suspend fun getQuizzes(id: String): MutableLiveData<ResponseState<Quiz?>> {
        val updateUserLiveData: MutableLiveData<ResponseState<Quiz?>> =
            MutableLiveData()

        quizRef.document(id).get().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                if (task.result != null) {
                    val contest = task.result!!.toObject(Quiz::class.java)
                    updateUserLiveData.value =
                        ResponseState.Success(contest)
                } else {
                    updateUserLiveData.value =
                        ResponseState.Error("No Quiz found")
                }
            } else {
                updateUserLiveData.value =
                    ResponseState.Error("Some error occured : ${task.exception?.message}")
            }
        }.await()
        return updateUserLiveData

    }


}