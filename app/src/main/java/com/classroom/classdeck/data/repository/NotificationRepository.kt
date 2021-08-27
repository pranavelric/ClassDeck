package com.classroom.classdeck.data.repository

import androidx.lifecycle.MutableLiveData
import com.classroom.classdeck.data.model.Notifications
import com.classroom.classdeck.util.Constants
import com.classroom.classdeck.util.ResponseState
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton


@Singleton
class NotificationRepository @Inject constructor() {

    private val firebaseAuth: FirebaseAuth = FirebaseAuth.getInstance()
    private val rootRef: FirebaseFirestore = FirebaseFirestore.getInstance()
    private val usersRef: CollectionReference = rootRef.collection(Constants.USERS)
    private val notificationRef: CollectionReference = rootRef.collection(Constants.NOTIFICATIONS)


    val allGlobalNotificationlist = ArrayList<Notifications?>()
    suspend fun getGlobalNotifications(): MutableLiveData<ResponseState<List<Notifications?>>> {


        val gameMutableLiveData: MutableLiveData<ResponseState<List<Notifications?>>> =
            MutableLiveData()

        allGlobalNotificationlist.clear()
        notificationRef.get()
            .addOnCompleteListener { task ->

                if (task.isSuccessful) {

                    if (task.result != null && !task.result!!.isEmpty) {


                        for (doc: DocumentSnapshot in task.result!!.documents) {
                            val obj = doc.toObject(Notifications::class.java)

                            allGlobalNotificationlist.add(obj)
                        }


                        gameMutableLiveData.value = ResponseState.Success(allGlobalNotificationlist)


                    } else {
                        gameMutableLiveData.value =
                            ResponseState.Error("No new notification.")
                    }
                } else {
                    gameMutableLiveData.value = task.exception!!.message?.let {
                        ResponseState.Error(
                            it
                        )
                    }
                }

            }.await()


        return gameMutableLiveData
    }


    val allUserNotificationlist = ArrayList<Notifications?>()
    suspend fun getUserNotifications(userId: String): MutableLiveData<ResponseState<List<Notifications?>>> {


        val gameMutableLiveData: MutableLiveData<ResponseState<List<Notifications?>>> =
            MutableLiveData()

        allUserNotificationlist.clear()
        usersRef.document(userId).collection(Constants.NOTIFICATIONS).get()
            .addOnCompleteListener { task ->

                if (task.isSuccessful) {

                    if (task.result != null && !task.result!!.isEmpty) {


                        for (doc: DocumentSnapshot in task.result!!.documents) {
                            val obj = doc.toObject(Notifications::class.java)

                            allUserNotificationlist.add(obj)
                        }


                        gameMutableLiveData.value = ResponseState.Success(allUserNotificationlist)


                    } else {
                        gameMutableLiveData.value =
                            ResponseState.Error("No new notification.")
                    }
                } else {
                    gameMutableLiveData.value = task.exception!!.message?.let {
                        ResponseState.Error(
                            it
                        )
                    }
                }

            }.await()

        return gameMutableLiveData
    }


    suspend fun clearUserNotifications(uid: String?) {

        if (uid != null) {
            usersRef.document(uid).collection(Constants.NOTIFICATIONS)
                .get().addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        for (document in task.result!!) {
                            usersRef.document(uid).collection(Constants.NOTIFICATIONS)
                                .document(document.id).delete()
                        }
                    }
                }.await()
        }

    }

    suspend fun clearGlobalNotification() {
        notificationRef
            .get().addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    for (document in task.result!!) {
                        notificationRef
                            .document(document.id).delete()
                    }
                }
            }.await()
    }

    suspend fun setSeenAllNotifications(userId:String){
        val file: MutableMap<String, Any> = HashMap()
        file["seen"] = true

        notificationRef.get().addOnCompleteListener { task->
            if(task.isSuccessful){

                for (document in task.result!!) {
                    notificationRef
                        .document(document.id).set(file, SetOptions.merge())
                }

            }

        }.await()



        usersRef.document(userId).collection(Constants.NOTIFICATIONS).get().addOnCompleteListener { task->
            if(task.isSuccessful){

                for (document in task.result!!) {
                    usersRef.document(userId).collection(Constants.NOTIFICATIONS)
                        .document(document.id).set(file, SetOptions.merge())
                }

            }

        }.await()

    }


}