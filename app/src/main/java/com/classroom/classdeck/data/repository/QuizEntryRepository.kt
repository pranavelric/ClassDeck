package com.classroom.classdeck.data.repository

import androidx.lifecycle.MutableLiveData
import com.classroom.classdeck.data.model.QuizUser
import com.classroom.classdeck.util.Constants
import com.classroom.classdeck.util.ResponseState
import com.classroom.classdeck.util.getTodaysDate
import com.google.firebase.firestore.*
import kotlinx.coroutines.tasks.await
import javax.inject.Inject


class QuizEntryRepository
@Inject constructor() {


    private val rootRef: FirebaseFirestore = FirebaseFirestore.getInstance()
    private val courseRef: CollectionReference =  rootRef.collection(Constants.COURSE)
    private val usersRef: CollectionReference = rootRef.collection(Constants.USERS)


    suspend fun getUserContest(
        userId: String,
        contestId: String
    ): MutableLiveData<ResponseState<QuizUser?>> {

        val contestUserLiveData: MutableLiveData<ResponseState<QuizUser?>> = MutableLiveData()

        usersRef.document(userId).collection(Constants.QUIZ).document(contestId).get()
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    if (task.result != null && task.result!!.exists()) {
                        val contestUser = task.result!!.toObject(QuizUser::class.java)
                        contestUserLiveData.value = ResponseState.Success(contestUser)
                    } else {
                        contestUserLiveData.value = ResponseState.Success(null)
                    }
                } else {
                    contestUserLiveData.value =
                        task.exception?.message?.let { ResponseState.Error(it) }
                }

            }.await()

        return contestUserLiveData


    }

    suspend fun setQuizStarted(id: String,courseId: String) {

        val file = HashMap<String, Any>()
        file["started"] = true
        courseRef.document(courseId).collection(Constants.QUIZ).document(id).update(file).await()

    }

}