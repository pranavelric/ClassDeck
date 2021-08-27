package com.classroom.classdeck.data.model

data class ResultModel(
    var userId: String? = "",
    var userPhoneNumber:String="",
    var correct: Int? = 0,
    var wrong: Int? = 0,
    var contestId: String? = "",
    )
