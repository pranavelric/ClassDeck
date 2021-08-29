package com.classroom.classdeck.data.repository

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.classroom.classdeck.data.model.*
import com.classroom.classdeck.util.Constants
import com.classroom.classdeck.util.ResponseState
import com.classroom.classdeck.util.getTodaysDate
import com.google.firebase.firestore.*
import kotlinx.coroutines.tasks.await
import javax.inject.Inject


class StartQuizRepository
@Inject constructor() {


    private val rootRef: FirebaseFirestore = FirebaseFirestore.getInstance()
    private val courseRef: CollectionReference =
        rootRef.collection(Constants.COURSE)


    private val usersRef: CollectionReference = rootRef.collection(Constants.USERS)
    val questionsList = ArrayList<Question?>()


    suspend fun getQuizQuestions(
        contestId: String,
        course: Course
    ): MutableLiveData<ResponseState<List<Question?>>> {
        val updateUserLiveData: MutableLiveData<ResponseState<List<Question?>>> = MutableLiveData()

        questionsList.clear()
        courseRef.document(course.courseCode).collection(Constants.QUIZ).document(contestId)
            .collection(Constants.QUESTIONS)
            .orderBy("period", Query.Direction.ASCENDING).get()
            .addOnCompleteListener { task ->

                if (task.isSuccessful) {
                    if (task.result != null && !task.result!!.isEmpty) {


                        for (doc: DocumentSnapshot in task.result!!.documents) {
                            val obj = doc.toObject(Question::class.java)

                            questionsList.add(obj)
                        }

                        updateUserLiveData.value = ResponseState.Success(questionsList)

                    } else {
                        updateUserLiveData.value = ResponseState.Error("No questions found")

                    }

                } else {
                    updateUserLiveData.value =
                        ResponseState.Error("Some error occured: ${task.exception?.message}")

                }


            }.await()

        return updateUserLiveData
    }


    suspend fun submitQuiz(
        contest: Quiz,
        contestUsr: QuizUser,
        user: User?,
        course: Course
    ): MutableLiveData<ResponseState<Boolean>> {
        val updateUserLiveData: MutableLiveData<ResponseState<Boolean>> = MutableLiveData()
        val userContestRef =
            user?.uid?.let {
                usersRef.document(it).collection(Constants.QUIZ).document(contest?.id.toString())
            }

        val results = courseRef.document(course.courseCode).collection(Constants.QUIZ).document(
            contest.id.toString()
        ).collection(Constants.RESULTS)
            .document(user?.uid.toString())


        val file: HashMap<String, Any> = HashMap()
        contestUsr.wrong?.also { file["wrong"] = it }
        contestUsr.correct?.also { file["correct"] = it }
        contestUsr.completed?.also { file["completed"] = it }


        val result = ResultModel()
        result.contestId = contest.id
        result.courseCode = course.courseCode
        result.userId = user?.uid
        result.userEmail = user?.email.toString()
        result.userName = user?.name.toString()
        result.correct = contestUsr.correct
        result.wrong = contestUsr.wrong
        result.userPhoneNumber = user?.phoneNumber.toString()



        rootRef.runBatch { batch ->

            batch.set(results, result)
            if (userContestRef != null) {
                batch.update(userContestRef, file)
            }


        }.addOnCompleteListener {
            if (it.isSuccessful) {

                updateUserLiveData.value =
                    ResponseState.Success(true)

            } else {

                updateUserLiveData.value =
                    ResponseState.Error("Some error occured:${it.exception?.message}")
            }


        }.await()

        return updateUserLiveData


    }


}