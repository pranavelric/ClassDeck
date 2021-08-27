package com.classroom.classdeck.data.model

import java.io.Serializable

data class User(
    val uid: String,
    val name: String?,
    val email: String?,
    val phoneNumber: String?,
    var imageUrl: String?,
    var isNew: Boolean? = false,
    var isStudent:Boolean? = false,

) : Serializable {

    constructor() : this("", "", "", "", "") {

    }




}

