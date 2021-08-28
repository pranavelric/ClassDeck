package com.classroom.classdeck.data.model
import java.io.Serializable
data class Assignments (
    var id:String="",
    var title:String="",
    var message:String="",
    var time:String = "",
    var date:String = "",
    var courseCode:String = "",
    var submitTo:String = "",

        ): Serializable{}