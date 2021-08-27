package com.gaming.earningvalley.data.repository

import androidx.lifecycle.MutableLiveData
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.classroom.classdeck.data.model.User
import com.classroom.classdeck.util.Constants.USERS
import com.classroom.classdeck.util.ResponseState
import javax.inject.Inject
import javax.inject.Singleton


@Singleton
class SplashRepository @Inject constructor() {

    private val firebaseAuth: FirebaseAuth = FirebaseAuth.getInstance()
    private val rootRef: FirebaseFirestore = FirebaseFirestore.getInstance()
    private val usersRef: CollectionReference = rootRef.collection(USERS)

    fun checkIfUserIsAuthenticatedInFirebase(): MutableLiveData<ResponseState<User?>> {
        val isUserAuthenticatedLiveData: MutableLiveData<ResponseState<User?>> = MutableLiveData()
        val firebaseUser = firebaseAuth.currentUser
        if (firebaseUser == null) {
            isUserAuthenticatedLiveData.value = ResponseState.Success(null)
        } else {
            val uid = firebaseUser.uid
            val name = firebaseUser.displayName
            val email = firebaseUser.email
            val phoneNumber = firebaseUser.phoneNumber
            val profilePic = firebaseUser.photoUrl?.toString()
            val user = User(uid = uid, name = name, email = email,phoneNumber = phoneNumber,profilePic)
            isUserAuthenticatedLiveData.value = ResponseState.Success(user)

        }
        return isUserAuthenticatedLiveData
    }

    fun addUserToLiveData(uid: String): MutableLiveData<ResponseState<User>> {
        val userMutableLiveData: MutableLiveData<ResponseState<User>> = MutableLiveData()

        usersRef.document(uid).get().addOnCompleteListener { userTask: Task<DocumentSnapshot?> ->
            if (userTask.isSuccessful) {
                val document = userTask.result
                if (document!!.exists()) {
                    val user = document.toObject(User::class.java)
                    userMutableLiveData.value = ResponseState.Success(user!!)
                } else {
                    userMutableLiveData.value = ResponseState.Error("Some error occured!!")

                }
            } else {
                userMutableLiveData.value =
                    userTask.exception!!.message?.let { ResponseState.Error(it) }


            }
        }
        return userMutableLiveData
    }


}