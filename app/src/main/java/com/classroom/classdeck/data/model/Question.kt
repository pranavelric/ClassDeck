package com.classroom.classdeck.data.model

data class Question(
    var id: String? = "",
    var answer: String? = "",
    var question: String? = "",
    var optionA: String? = "",
    var optionD: String? = "",
    var optionC: String? = "",
    var optionB: String? = "",
    var period: Int? = 0,

    )