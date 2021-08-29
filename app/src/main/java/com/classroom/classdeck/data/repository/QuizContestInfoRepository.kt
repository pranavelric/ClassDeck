package com.classroom.classdeck.data.repository

import androidx.lifecycle.MutableLiveData
import com.classroom.classdeck.data.model.*
import com.classroom.classdeck.util.Constants
import com.classroom.classdeck.util.Mappers
import com.classroom.classdeck.util.ResponseState
import com.classroom.classdeck.util.getTodaysDate
import com.google.firebase.firestore.*
import kotlinx.coroutines.tasks.await
import javax.inject.Inject


class QuizContestInfoRepository
@Inject constructor() {


    private val rootRef: FirebaseFirestore = FirebaseFirestore.getInstance()
    private val courseRef: CollectionReference =
        rootRef.collection(Constants.COURSE)
    private val notificationsRef: CollectionReference = rootRef.collection(Constants.NOTIFICATIONS)
    private val usersRef: CollectionReference = rootRef.collection(Constants.USERS)





    suspend fun enterInContest(
        user: User,
        contest: Quiz,
        course: Course
    ): MutableLiveData<ResponseState<String?>> {
        val newUserMutableLiveData: MutableLiveData<ResponseState<String?>> = MutableLiveData()

        val userQuizRef =
            usersRef.document(user.uid).collection(Constants.QUIZ).document(contest.id.toString())

        val registeredUsers =
           courseRef.document(course.courseCode).collection(Constants.QUIZ).document(contest.id.toString()).collection(Constants.REGISTERED_STUDENTS)
                .document(user.uid)

        val quizRef =   courseRef.document(course.courseCode).collection(Constants.QUIZ).document(contest.id.toString())
        val userContest = contest.let { Mappers.contestToUserContest(it) }



        rootRef.runBatch { batch ->


            userContest.let { batch.set(userQuizRef, it) }
            batch.set(registeredUsers, user)
            batch.update(
                quizRef,
                "joined",
                FieldValue.increment(1)
            )


        }.addOnCompleteListener {
            if (it.isSuccessful) {
                newUserMutableLiveData.value =
                    ResponseState.Success("Registered for Quiz successfully")

            } else {
                newUserMutableLiveData.value =
                    ResponseState.Error("Registration failed:${it.exception?.message}")
            }


        }.await()

        return newUserMutableLiveData

    }





    suspend fun checkIfAlreadyRegisteredInContest(
        userId: String,
        contestId: String,
        course: Course
    ): MutableLiveData<ResponseState<Boolean?>> {

        val newUserMutableLiveData: MutableLiveData<ResponseState<Boolean?>> = MutableLiveData()

        courseRef.document(course.courseCode).collection(Constants.QUIZ).document(contestId).collection(Constants.REGISTERED_STUDENTS)
            .whereEqualTo("uid", userId).get()
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    if (task.result != null && task.result!!.documents != null) {
                        if (task.result?.documents?.size!! > 0) {
                            newUserMutableLiveData.value = ResponseState.Success(true)

                        } else {
                            newUserMutableLiveData.value = ResponseState.Success(false)
                        }

                    } else {
                        newUserMutableLiveData.value = ResponseState.Success(false)
                    }
                } else {
                    newUserMutableLiveData.value = ResponseState.Error("${task.exception?.message}")

                }

            }.await()

        return newUserMutableLiveData
    }



    suspend fun getUsersRanking(id: String,course: Course): MutableLiveData<ResponseState<RankingModelList?>> {
        val newUserMutableLiveData: MutableLiveData<ResponseState<RankingModelList?>> =
            MutableLiveData()

        courseRef.document(course.courseCode).collection(Constants.QUIZ).document(id.toString()).collection(Constants.RESULTS).document(Constants.RANKING).get()
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    if (it.result != null && it.result!!.exists()) {
                        val res = it.result!!.toObject(RankingModelList::class.java)
                        newUserMutableLiveData.value = ResponseState.Success(res)

                    } else {
                        newUserMutableLiveData.value = ResponseState.Success(null)

                    }
                } else {
                    newUserMutableLiveData.value =
                        ResponseState.Error("Some error occured: ${it.exception?.message}")
                }
            }.await()

        return newUserMutableLiveData

    }



    fun getQuizContest(id: String,course: Course): MutableLiveData<ResponseState<Quiz?>> {
        val newUserMutableLiveData: MutableLiveData<ResponseState<Quiz?>> = MutableLiveData()

        courseRef.document(course.courseCode).collection(Constants.QUIZ).document(id).get().addOnCompleteListener {
            if (it.isSuccessful) {

                if (it.result != null && it.result!!.exists()) {
                    val obj = it.result!!.toObject(Quiz::class.java)

                    newUserMutableLiveData.value = ResponseState.Success(obj)
                } else {
                    newUserMutableLiveData.value =
                        ResponseState.Error("No Quiz found with this id")
                }

            } else {
                newUserMutableLiveData.value =
                    ResponseState.Error("Some error occured: ${it.exception?.message}")
            }

        }

        return newUserMutableLiveData

    }


}