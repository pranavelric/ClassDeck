package com.classroom.classdeck.data.repository

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.classroom.classdeck.data.model.Course
import com.classroom.classdeck.data.model.Question
import com.classroom.classdeck.data.model.User
import com.classroom.classdeck.util.Constants
import com.classroom.classdeck.util.Mappers
import com.classroom.classdeck.util.ResponseState
import com.google.firebase.auth.FirebaseAuth
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.*
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton


@Singleton
class CourseDetailsRepository @Inject constructor() {
    private val firebaseAuth: FirebaseAuth = FirebaseAuth.getInstance()
    private val rootRef: FirebaseFirestore = FirebaseFirestore.getInstance()
    private val usersRef: CollectionReference = rootRef.collection(Constants.USERS)
    private val notificationRef: CollectionReference = rootRef.collection(Constants.NOTIFICATIONS)
    private val courseRef: CollectionReference = rootRef.collection(Constants.COURSE)


    fun getUser(uid: String): MutableLiveData<ResponseState<User>> {
        val userMutableLiveData: MutableLiveData<ResponseState<User>> =
            MutableLiveData()

        usersRef.document(uid).get()
            .addOnCompleteListener { userTask: Task<DocumentSnapshot?> ->
                if (userTask.isSuccessful) {
                    val document = userTask.result
                    if (document!!.exists()) {
                        val user = document.toObject(User::class.java)
                        userMutableLiveData.value = ResponseState.Success(user!!)
                    } else {
                        userMutableLiveData.value =
                            ResponseState.Error("Some error occured!!")

                    }
                } else {
                    userMutableLiveData.value =
                        userTask.exception?.message?.let { ResponseState.Error(it) }


                }
            }
        return userMutableLiveData
    }


    suspend fun getCourseFromCode(courseCode: String): MutableLiveData<ResponseState<Course?>> {
        val courseMutableLiveData: MutableLiveData<ResponseState<Course?>> =
            MutableLiveData()


        courseRef.document(courseCode).get().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                if (task.result != null && task.result!!.exists()) {



                    val course = task.result!!.toObject(Course::class.java)
                    courseMutableLiveData.value = ResponseState.Success(course)


                } else {



                    courseMutableLiveData.value = ResponseState.Error("No course exist with this course code")
                }

            } else {

                courseMutableLiveData.value = ResponseState.Error(""+task.exception?.message)
            }
        }.await()


        return courseMutableLiveData


    }


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

                        Log.d("RRR", "getCourseListOfStudent: ${studentCourseList} ")
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



}