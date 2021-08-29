package com.classroom.classdeck.data.model

import java.io.Serializable

data class QuizUser(
    var id: String,

    var isEnded: Boolean? = false,
    var isStarted: Boolean? = false,
    var date: String? = null,
    var time: String? = null,
    var startTime: String? = null,

    var correct: Int? = 0,
    var wrong: Int? = 0,
    var question_numbers: Int? = 0,
    var completed: Boolean? = false,
    var rank: Long? = 0,
    var startDate: String? = null,
    var answerTime: Int? = 10,
    var joined:Int?= 0,

    var courseCode:String= "",
    var courseName:String="",

    var quizTitle:String = "",
    var marks:Int= 0,

    ) : Serializable {

    constructor() : this("") {

    }
}