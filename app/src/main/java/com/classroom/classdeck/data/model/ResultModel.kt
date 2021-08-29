package com.classroom.classdeck.data.model

data class ResultModel(
    var userId: String? = "",
    var userPhoneNumber:String="",
    var userEmail:String="",
    var userName:String="",

    var correct: Int? = 0,
    var wrong: Int? = 0,
    var contestId: String? = "",
    var courseCode:String?=""
    )
