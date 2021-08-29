package com.classroom.classdeck.data.repository

import androidx.lifecycle.MutableLiveData
import com.classroom.classdeck.data.model.*
import com.classroom.classdeck.util.Constants
import com.classroom.classdeck.util.ResponseState
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton


@Singleton
class DashBoardRepository @Inject constructor() {

    private val rootRef: FirebaseFirestore = FirebaseFirestore.getInstance()
    private val usersRef: CollectionReference = rootRef.collection(Constants.USERS)







    val studentCourseList = ArrayList<Course?>()
    suspend fun getCourseListOfStudent(userId: String): MutableLiveData<ResponseState<List<Course?>>> {
        val courselistLiveData: MutableLiveData<ResponseState<List<Course?>>> = MutableLiveData()

        studentCourseList.clear()
        usersRef.document(userId).collection(Constants.COURSE).get()
            .addOnCompleteListener { task ->

                if (task.isSuccessful) {
                    if (task.result != null && !task.result!!.isEmpty) {


                        for (doc: DocumentSnapshot in task.result!!.documents) {
                            val obj = doc.toObject(Course::class.java)
                            studentCourseList.add(obj)
                        }


                        courselistLiveData.value = ResponseState.Success(studentCourseList)

                    } else {
                        courselistLiveData.value = ResponseState.Error("No courses found")

                    }

                } else {
                    courselistLiveData.value =
                        ResponseState.Error("Some error occured: ${task.exception?.message}")

                }


            }.await()

        return courselistLiveData
    }





    val allCourseList = ArrayList<Course?>()
    suspend fun getAllCourse(): MutableLiveData<ResponseState<List<Course?>>> {
        val courselistLiveData: MutableLiveData<ResponseState<List<Course?>>> = MutableLiveData()

       allCourseList.clear()
       rootRef.collection(Constants.COURSE).get()
            .addOnCompleteListener { task ->

                if (task.isSuccessful) {
                    if (task.result != null && !task.result!!.isEmpty) {


                        for (doc: DocumentSnapshot in task.result!!.documents) {
                            val obj = doc.toObject(Course::class.java)
                           allCourseList.add(obj)
                        }


                        courselistLiveData.value = ResponseState.Success(allCourseList)

                    } else {
                        courselistLiveData.value = ResponseState.Error("No courses found")

                    }

                } else {
                    courselistLiveData.value =
                        ResponseState.Error("Some error occured: ${task.exception?.message}")

                }


            }.await()

        return courselistLiveData
    }





    val quizsList = ArrayList<QuizUser?>()
    suspend fun getQAllQuizs(userId: String): MutableLiveData<ResponseState<List<QuizUser?>>> {
        val courselistLiveData: MutableLiveData<ResponseState<List<QuizUser?>>> =
            MutableLiveData()

        quizsList.clear()
        usersRef.document(userId).collection(Constants.QUIZ).get()
            .addOnCompleteListener { task ->

                if (task.isSuccessful) {
                    if (task.result != null && !task.result!!.isEmpty) {


                        for (doc: DocumentSnapshot in task.result!!.documents) {
                            val obj = doc.toObject(QuizUser::class.java)
                            quizsList.add(obj)
                        }


                        courselistLiveData.value = ResponseState.Success(quizsList)

                    } else {
                        courselistLiveData.value = ResponseState.Error("No quizzes found")

                    }

                } else {
                    courselistLiveData.value =
                        ResponseState.Error("Some error occured: ${task.exception?.message}")

                }


            }.await()

        return courselistLiveData
    }








    val announcementList = ArrayList<Announcements?>()
        suspend fun getCourseAnnouncements(userId: String): MutableLiveData<ResponseState<List<Announcements?>>> {
            val courselistLiveData: MutableLiveData<ResponseState<List<Announcements?>>> =
                MutableLiveData()

            announcementList.clear()
            usersRef.document(userId).collection(Constants.ANNOUNCEMENTS).get()
                .addOnCompleteListener { task ->

                    if (task.isSuccessful) {
                        if (task.result != null && !task.result!!.isEmpty) {


                            for (doc: DocumentSnapshot in task.result!!.documents) {
                                val obj = doc.toObject(Announcements::class.java)
                                announcementList.add(obj)
                            }


                            courselistLiveData.value = ResponseState.Success(announcementList)

                        } else {
                            courselistLiveData.value = ResponseState.Error("No announcements found")

                        }

                    } else {
                        courselistLiveData.value =
                            ResponseState.Error("Some error occured: ${task.exception?.message}")

                    }


                }.await()

            return courselistLiveData
        }





    val assignmentList = ArrayList<Assignments?>()
    suspend fun getCourseAssignments(userId: String): MutableLiveData<ResponseState<List<Assignments?>>> {
        val courselistLiveData: MutableLiveData<ResponseState<List<Assignments?>>> =
            MutableLiveData()

       assignmentList.clear()
        usersRef.document(userId).collection(Constants.ASSIGNMENTS).get()
            .addOnCompleteListener { task ->

                if (task.isSuccessful) {
                    if (task.result != null && !task.result!!.isEmpty) {


                        for (doc: DocumentSnapshot in task.result!!.documents) {
                            val obj = doc.toObject(Assignments::class.java)
                          assignmentList.add(obj)
                        }


                        courselistLiveData.value = ResponseState.Success(assignmentList)

                    } else {
                        courselistLiveData.value = ResponseState.Error("No assignments found")

                    }

                } else {
                    courselistLiveData.value =
                        ResponseState.Error("Some error occured: ${task.exception?.message}")

                }


            }.await()

        return courselistLiveData
    }



    val eventList = ArrayList<Event?>()
    suspend fun getCourseEvent(userId: String): MutableLiveData<ResponseState<List<Event?>>> {
        val courselistLiveData: MutableLiveData<ResponseState<List<Event?>>> =
            MutableLiveData()

       eventList.clear()
        usersRef.document(userId).collection(Constants.EVENTS).get()
            .addOnCompleteListener { task ->

                if (task.isSuccessful) {
                    if (task.result != null && !task.result!!.isEmpty) {


                        for (doc: DocumentSnapshot in task.result!!.documents) {
                            val obj = doc.toObject(Event::class.java)
                          eventList.add(obj)
                        }


                        courselistLiveData.value = ResponseState.Success(eventList)

                    } else {
                        courselistLiveData.value = ResponseState.Error("No events found")

                    }

                } else {
                    courselistLiveData.value =
                        ResponseState.Error("Some error occured: ${task.exception?.message}")

                }


            }.await()

        return courselistLiveData
    }












}