package com.classroom.classdeck.data.repository

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.*
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.classroom.classdeck.data.model.User
import com.classroom.classdeck.util.Constants.USERS
import com.classroom.classdeck.util.ResponseState
import javax.inject.Inject
import javax.inject.Singleton


@Singleton
class AuthRepository @Inject constructor() {

    private val firebaseAuth: FirebaseAuth = FirebaseAuth.getInstance()
    private val rootRef: FirebaseFirestore = FirebaseFirestore.getInstance()
    private val usersRef: CollectionReference = rootRef.collection(USERS)


    fun firebaseSignInWithPhone(credential: PhoneAuthCredential): MutableLiveData<ResponseState<User>> {
        val authenticatedUserMutableLiveData: MutableLiveData<ResponseState<User>> =
            MutableLiveData()
        firebaseAuth.signInWithCredential(credential).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val isNewUser = task.result?.additionalUserInfo?.isNewUser
                val firebaseUser: FirebaseUser? = firebaseAuth.currentUser
                if (firebaseUser != null) {
                    val uid = firebaseUser.uid
                    val name = firebaseUser.displayName
                    val email = firebaseUser.email
                    val phoneNumber = firebaseUser.phoneNumber
                    val profilePic = firebaseUser.photoUrl?.toString()
                    val user = User(
                        uid = uid,
                        name = name,
                        email = email,
                        phoneNumber = phoneNumber,
                        profilePic
                    )
                    user.isNew = isNewUser
                    authenticatedUserMutableLiveData.value = ResponseState.Success(user)
                }

            } else {

                if (task.exception is FirebaseAuthInvalidCredentialsException) {
                    authenticatedUserMutableLiveData.value =
                        ResponseState.Error("The verification code entered is invalid")
                }
            }

        }
        return authenticatedUserMutableLiveData
    }

    fun createUserInFireStoreIfNotExist(authenticatedUser: User,isStudent:Boolean): MutableLiveData<ResponseState<User>> {


        val newUserMutableLiveData: MutableLiveData<ResponseState<User>> = MutableLiveData()
        val uidRef: DocumentReference = usersRef.document(authenticatedUser.uid)

        uidRef.get().addOnCompleteListener { uidTask ->
            if (uidTask.isSuccessful) {

                val document: DocumentSnapshot? = uidTask.result

                if (document != null) {
                    if (!document.exists()) {


                        authenticatedUser.isNew = authenticatedUser.isNew
                        authenticatedUser.isStudent = isStudent

                        uidRef.set(authenticatedUser)
                            .addOnCompleteListener { userCreationTask ->
                                if (userCreationTask.isSuccessful) {

                                    newUserMutableLiveData.value =
                                        ResponseState.Success(authenticatedUser)
                                } else {

                                    newUserMutableLiveData.value =
                                        userCreationTask.exception?.message?.let {
                                            ResponseState.Error(
                                                it
                                            )
                                        }

                                }

                            }

                    } else {
                        newUserMutableLiveData.value = ResponseState.Success(authenticatedUser)

                        val file: MutableMap<String, Any> = HashMap()
                        file["isNew"] = false

                        uidRef.update(file).addOnCompleteListener {
                            if (it.isSuccessful) {
                                Log.d("RRR", "createUserInFireStoreIfNotExist:  success")
                            } else {
                                Log.d(
                                    "RRR",
                                    "createUserInFireStoreIfNotExist:${it.exception?.message} "
                                )
                            }
                        }


                    }

                } else {

                    newUserMutableLiveData.value =
                        ResponseState.Error("Some error occured, Please try again later")

                }

            } else {
                newUserMutableLiveData.value = uidTask.exception?.message?.let {
                    ResponseState.Error(
                        it
                    )
                }


            }
        }

        return newUserMutableLiveData

    }



    fun getUser(uid: String): MutableLiveData<ResponseState<User>> {
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
                    userTask.exception?.message?.let { ResponseState.Error(it) }


            }
        }
        return userMutableLiveData
    }

    fun updateUserIsNew(id: User, b: Boolean) {
        val file: MutableMap<String, Any> = HashMap()
        file["new"] = false

        usersRef.document(id.uid).update(file).addOnCompleteListener {
            if (it.isSuccessful) {
                Log.d("RRR", "createUserInFireStoreIfNotExist:  success")
            } else {
                Log.d("RRR", "createUserInFireStoreIfNotExist:${it.exception?.message} ")
            }
        }
    }


}