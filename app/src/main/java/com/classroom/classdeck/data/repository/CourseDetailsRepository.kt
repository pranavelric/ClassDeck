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


                    courseMutableLiveData.value =
                        ResponseState.Error("No course exist with this course code")
                }

            } else {

                courseMutableLiveData.value = ResponseState.Error("" + task.exception?.message)
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

    suspend fun addAnnouncement(announcements: Announcements,userId: String): MutableLiveData<ResponseState<String?>> {
        val courseMutableLiveData: MutableLiveData<ResponseState<String?>> =
            MutableLiveData()

        val mcourseRef =
            courseRef.document(announcements.courseCode).collection(Constants.ANNOUNCEMENTS)
                .document()
        val id = mcourseRef.id
        val teacherRef = usersRef.document(userId).collection(Constants.ANNOUNCEMENTS).document(id)


        announcements.id = id


        var documentsInBatch: List<DocumentSnapshot>? = null


        courseRef.document(announcements.courseCode).collection(Constants.REGISTERED_STUDENTS).get()
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    if (it.result != null && !it.result!!.isEmpty) {
                        documentsInBatch = it.result!!.documents
                    }
                }
            }.await()

        val studentList: ArrayList<User> = ArrayList()

        rootRef.runBatch { batch ->
            announcements.let { batch.set(mcourseRef, it) }
            announcements.let { batch.set(teacherRef, it) }


            if (documentsInBatch != null && documentsInBatch!!.isNotEmpty()) {
                documentsInBatch?.forEach {

                    val users = it.toObject(User::class.java)

                    if (users != null) {
                        studentList.add(users)
                    }

                }

                for( i in studentList){

                    val mUserRef = i.uid?.let {
                        usersRef.document(it).collection(Constants.ANNOUNCEMENTS).document(id)
                    }
                    batch.set(mUserRef,announcements)
                }


            }



            }.addOnCompleteListener {
                if (it.isSuccessful) {
                    courseMutableLiveData.value =
                        ResponseState.Success("Announcement created  successfully")

                } else {
                    courseMutableLiveData.value =
                        ResponseState.Error("Announcement creation failed:${it.exception?.message}")
                }


            }.await()

            return courseMutableLiveData
        }


        val announcementList = ArrayList<Announcements?>()
        suspend fun getCourseAnnouncements(courseCode: String): MutableLiveData<ResponseState<List<Announcements?>>> {
            val courselistLiveData: MutableLiveData<ResponseState<List<Announcements?>>> =
                MutableLiveData()

            announcementList.clear()
            courseRef.document(courseCode).collection(Constants.ANNOUNCEMENTS).get()
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





    suspend fun addAssignment(announcements: Assignments,userId: String): MutableLiveData<ResponseState<String?>> {
        val courseMutableLiveData: MutableLiveData<ResponseState<String?>> =
            MutableLiveData()

        val mcourseRef =
            courseRef.document(announcements.courseCode).collection(Constants.ASSIGNMENTS)
                .document()
        val id = mcourseRef.id
        val teacherRef = usersRef.document(userId).collection(Constants.ASSIGNMENTS).document(id)

        announcements.id = id


        var documentsInBatch: List<DocumentSnapshot>? = null


        courseRef.document(announcements.courseCode).collection(Constants.REGISTERED_STUDENTS).get()
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    if (it.result != null && !it.result!!.isEmpty) {
                        documentsInBatch = it.result!!.documents
                    }
                }
            }.await()

        val studentList: ArrayList<User> = ArrayList()

        rootRef.runBatch { batch ->
            announcements.let { batch.set(mcourseRef, it) }
            announcements.let { batch.set(teacherRef, it) }


            if (documentsInBatch != null && documentsInBatch!!.isNotEmpty()) {
                documentsInBatch?.forEach {

                    val users = it.toObject(User::class.java)

                    if (users != null) {
                        studentList.add(users)
                    }

                }

                for( i in studentList){

                    val mUserRef = i.uid?.let {
                        usersRef.document(it).collection(Constants.ASSIGNMENTS).document(id)
                    }
                    batch.set(mUserRef,announcements)
                }


            }



        }.addOnCompleteListener {
            if (it.isSuccessful) {
                courseMutableLiveData.value =
                    ResponseState.Success("Assignemnt created  successfully")

            } else {
                courseMutableLiveData.value =
                    ResponseState.Error("Assignment creation failed:${it.exception?.message}")
            }


        }.await()

        return courseMutableLiveData
    }


    val assignmentList = ArrayList<Assignments?>()
    suspend fun getCourseAssignments(courseCode: String): MutableLiveData<ResponseState<List<Assignments?>>> {
        val courselistLiveData: MutableLiveData<ResponseState<List<Assignments?>>> =
            MutableLiveData()

       assignmentList.clear()
        courseRef.document(courseCode).collection(Constants.ASSIGNMENTS).get()
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







    suspend fun addEvent(announcements:Event,userId: String): MutableLiveData<ResponseState<String?>> {
        val courseMutableLiveData: MutableLiveData<ResponseState<String?>> =
            MutableLiveData()

        val mcourseRef =
            courseRef.document(announcements.courseCode).collection(Constants.EVENTS)
                .document()
        val id = mcourseRef.id
        val teacherRef = usersRef.document(userId).collection(Constants.EVENTS).document(id)

        announcements.id = id


        var documentsInBatch: List<DocumentSnapshot>? = null


        courseRef.document(announcements.courseCode).collection(Constants.REGISTERED_STUDENTS).get()
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    if (it.result != null && !it.result!!.isEmpty) {
                        documentsInBatch = it.result!!.documents
                    }
                }
            }.await()

        val studentList: ArrayList<User> = ArrayList()

        rootRef.runBatch { batch ->
            announcements.let { batch.set(mcourseRef, it) }
            announcements.let { batch.set(teacherRef, it) }


            if (documentsInBatch != null && documentsInBatch!!.isNotEmpty()) {
                documentsInBatch?.forEach {

                    val users = it.toObject(User::class.java)

                    if (users != null) {
                        studentList.add(users)
                    }

                }

                for( i in studentList){

                    val mUserRef = i.uid?.let {
                        usersRef.document(it).collection(Constants.EVENTS).document(id)
                    }
                    batch.set(mUserRef,announcements)
                }


            }



        }.addOnCompleteListener {
            if (it.isSuccessful) {
                courseMutableLiveData.value =
                    ResponseState.Success("Event created  successfully")

            } else {
                courseMutableLiveData.value =
                    ResponseState.Error("Event creation failed:${it.exception?.message}")
            }


        }.await()

        return courseMutableLiveData
    }


    val eventList = ArrayList<Event?>()
    suspend fun getCourseEvent(courseCode: String): MutableLiveData<ResponseState<List<Event?>>> {
        val courselistLiveData: MutableLiveData<ResponseState<List<Event?>>> =
            MutableLiveData()

       eventList.clear()
        courseRef.document(courseCode).collection(Constants.EVENTS).get()
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