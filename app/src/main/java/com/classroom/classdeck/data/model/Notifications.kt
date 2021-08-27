package com.classroom.classdeck.data.model

import java.io.Serializable

data class Notifications(
    var id: String,
    var userId: String = "",
    var notifDate: String? = "",
    var notifTime: String? = "",
    var notifMessage:String?="",
    var notifTitle:String?="",
    var seen:Boolean= false,
) : Serializable {

    constructor() : this("") {

    }

}