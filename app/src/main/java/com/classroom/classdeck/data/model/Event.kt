package com.classroom.classdeck.data.model

import java.io.Serializable

data class Event(
    var id:String="",
    var title:String="",
    var message:String="",
    var time:String = "",
    var date:String = "",
    var courseCode:String = "",
    var eventTime:String = "",
    var eventDate:String="",
    var teacherName:String=""


): Serializable
