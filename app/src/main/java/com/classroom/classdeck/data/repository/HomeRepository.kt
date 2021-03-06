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
class HomeRepository @Inject constructor() {
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


    suspend fun getNotificationsSize(userId: String): MutableLiveData<ResponseState<Int>> {
        val gameMutableLiveData: MutableLiveData<ResponseState<Int>> =
            MutableLiveData()
        var size = 0
        var uSize = 0


        notificationRef.whereEqualTo("seen", false).get().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                if (task.result != null && !task.result!!.isEmpty) {

                    size = task.result!!.size()
                    gameMutableLiveData.value = ResponseState.Success(task.result!!.size())


                } else {
                    size = 0
                    gameMutableLiveData.value = ResponseState.Success(0)
                }

            } else {

                gameMutableLiveData.value = task.exception?.message?.let { ResponseState.Error(it) }
            }
        }.await()

        uSize = getUserNotifSize(userId)
        if (uSize >= 0)
            size += uSize

        if (gameMutableLiveData.value is ResponseState.Success)
            gameMutableLiveData.value = ResponseState.Success(size)

        return gameMutableLiveData


    }

    private suspend fun getUserNotifSize(userId: String): Int {

        var size = 0
        usersRef.document(userId).collection(Constants.NOTIFICATIONS).whereEqualTo("seen", false)
            .get()
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    if (task.result != null && !task.result!!.isEmpty) {

                        size = task.result!!.size()


                    } else {
                        size = 0

                    }

                } else {
                    size = -1
                }
            }.await()
        return size
    }


    suspend fun joinClass(
        course: Course,
        user: User
    ): MutableLiveData<ResponseState<String>> {
        val courseMutableLiveData: MutableLiveData<ResponseState<String>> =
            MutableLiveData()

        val userCourseRef =
            usersRef.document(user.uid).collection(Constants.COURSE).document(course.courseCode)

        val mCourseRef = courseRef.document(course.courseCode)
        val studentInCourseRef =
            courseRef.document(course.courseCode).collection(Constants.REGISTERED_STUDENTS)
                .document(user.uid)


        rootRef.runBatch { batch ->


            course.let { batch.set(userCourseRef, it) }

            user.let { batch.set(studentInCourseRef, it) }



            batch.update(
                mCourseRef,
                "studentCount",
                FieldValue.increment(1)
            )


        }.addOnCompleteListener {
            if (it.isSuccessful) {
                courseMutableLiveData.value =
                    ResponseState.Success("Registered for course successfully")

            } else {
                courseMutableLiveData.value =
                    ResponseState.Error("Registration failed:${it.exception?.message}")
            }


        }.await()

        return courseMutableLiveData


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



   suspend fun addCourse(course: Course, user: User): MutableLiveData<ResponseState<String?>>{
       val courseMutableLiveData: MutableLiveData<ResponseState<String?>> =
           MutableLiveData()

       val userCourseRef =
           usersRef.document(user.uid).collection(Constants.COURSE).document(course.courseCode)

       val mCourseRef = courseRef.document(course.courseCode)



       rootRef.runBatch { batch ->
           course.let { batch.set(userCourseRef, it) }
           course.let { batch.set(mCourseRef,it) }


       }.addOnCompleteListener {
           if (it.isSuccessful) {
               courseMutableLiveData.value =
                   ResponseState.Success("Course created  successfully")

           } else {
               courseMutableLiveData.value =
                   ResponseState.Error("Course creation failed:${it.exception?.message}")
           }


       }.await()

       return courseMutableLiveData
    }


}