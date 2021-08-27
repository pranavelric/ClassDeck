package com.classroom.classdeck.data.repository

import androidx.lifecycle.MutableLiveData
import com.classroom.classdeck.data.model.User
import com.classroom.classdeck.util.Constants
import com.classroom.classdeck.util.ResponseState
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.DocumentSnapshot
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton


@Singleton
class HomeRepository @Inject constructor() {
    private val firebaseAuth: FirebaseAuth = FirebaseAuth.getInstance()
    private val rootRef: FirebaseFirestore = FirebaseFirestore.getInstance()
    private val usersRef: CollectionReference = rootRef.collection(Constants.USERS)
    private val notificationRef: CollectionReference = rootRef.collection(Constants.NOTIFICATIONS)




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


        notificationRef.whereEqualTo("seen",false).get().addOnCompleteListener { task ->
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
        usersRef.document(userId).collection(Constants.NOTIFICATIONS).whereEqualTo("seen",false).get()
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





}