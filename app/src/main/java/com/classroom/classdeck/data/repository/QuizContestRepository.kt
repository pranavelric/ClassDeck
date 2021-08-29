package com.classroom.classdeck.data.repository

import androidx.lifecycle.MutableLiveData
import com.classroom.classdeck.data.model.Question
import com.classroom.classdeck.data.model.Quiz
import com.classroom.classdeck.data.model.QuizUser
import com.classroom.classdeck.util.Constants
import com.classroom.classdeck.util.ResponseState
import com.classroom.classdeck.util.getTodaysDate
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class QuizContestRepository
@Inject constructor() {


    private val rootRef: FirebaseFirestore = FirebaseFirestore.getInstance()
    private val courseRef: CollectionReference =
        rootRef.collection(Constants.COURSE)
    private val usersRef: CollectionReference = rootRef.collection(Constants.USERS)


    val questionsList = ArrayList<Question?>()
    suspend fun getQuizQuestions(courseId:String,contestId: String): MutableLiveData<ResponseState<List<Question?>>> {
        val updateUserLiveData: MutableLiveData<ResponseState<List<Question?>>> = MutableLiveData()

        questionsList.clear()
    courseRef.document(courseId).collection(Constants.QUIZ).document(contestId).collection(Constants.QUESTIONS)
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



    val contestsList = ArrayList<Quiz?>()
    suspend fun getAllQuizContests(courseId: String): MutableLiveData<ResponseState<List<Quiz?>>> {
        val updateUserLiveData: MutableLiveData<ResponseState<List<Quiz?>>> = MutableLiveData()
        contestsList.clear()
       courseRef.document(courseId).collection(Constants.QUIZ).get()
            .addOnCompleteListener { task ->

                if (task.isSuccessful) {
                    if (task.result != null && !task.result!!.isEmpty) {


                        for (doc: DocumentSnapshot in task.result!!.documents) {
                            val obj = doc.toObject(Quiz::class.java)

                            contestsList.add(obj)
                        }

                        updateUserLiveData.value = ResponseState.Success(contestsList)

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





    val userContestsList = ArrayList<QuizUser?>()
    suspend fun getAllUserContests(userId: String): MutableLiveData<ResponseState<List<QuizUser?>>> {
        val updateUserLiveData: MutableLiveData<ResponseState<List<QuizUser?>>> =
            MutableLiveData()
        userContestsList.clear()
        usersRef.document(userId).collection(Constants.QUIZ).get()
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


    suspend fun getContest(courseId: String,id: String): MutableLiveData<ResponseState<Quiz?>> {
        val updateUserLiveData: MutableLiveData<ResponseState<Quiz?>> =
            MutableLiveData()

        courseRef.document(courseId).collection(Constants.QUIZ).document(id).get().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                if (task.result != null) {
                    val contest = task.result!!.toObject(Quiz::class.java)
                    updateUserLiveData.value =
                        ResponseState.Success(contest)
                } else {
                    updateUserLiveData.value =
                        ResponseState.Error("No quiz found")
                }
            } else {
                updateUserLiveData.value =
                    ResponseState.Error("Some error occured : ${task.exception?.message}")
            }
        }.await()
        return updateUserLiveData

    }



        suspend fun createQuizContest(courseId: String,contest: Quiz): MutableLiveData<ResponseState<Quiz?>> {

            val updateUserLiveData: MutableLiveData<ResponseState<Quiz?>> = MutableLiveData()



                val quizGameRef =    courseRef.document(courseId).collection(Constants.QUIZ).document()
                contest.id = quizGameRef.id

                quizGameRef.set(contest).addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        updateUserLiveData.value = ResponseState.Success(contest)
                    } else {
                        updateUserLiveData.value =
                            ResponseState.Error("QUiz failed to crease: ${task.exception?.message}")
                    }

                }.await()


            return updateUserLiveData

        }


        suspend fun createQuizQuestions(
            courseId: String,
            quizId: String,
            mQuestion: Question
        ): MutableLiveData<ResponseState<String>> {
            val updateUserLiveData: MutableLiveData<ResponseState<String>> = MutableLiveData()

            courseRef.document(courseId).collection(Constants.QUIZ).document(quizId).collection(Constants.QUESTIONS)
                .document(mQuestion.period.toString()).set(mQuestion).addOnCompleteListener {
                    if (it.isSuccessful) {

                        updateUserLiveData.value = ResponseState.Success("Question added")

                    } else {
                        updateUserLiveData.value =
                            ResponseState.Error("Some error occured: ${it.exception?.message}")
                    }
                }.await()


            return updateUserLiveData
        }





}