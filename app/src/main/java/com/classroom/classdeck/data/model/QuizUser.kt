package com.classroom.classdeck.data.model

import java.io.Serializable

data class QuizUser(
    var id: String,
    var isEnded: Boolean? = false,
    var isStarted: Boolean? = false,
    var date: String? = null,
    var time: String? = null,
    var startTime: String? = null,
    var quizEndTime: String? = null,
    var correct: Int? = 0,
    var wrong: Int? = 0,
    var question_numbers: Int? = 0,
    var completed: Boolean? = false,
    var rank: Long? = 0,
    var gameEndDate: String? = null,
    var answerTime: Int? = 10,
    var joined:Int?= 0,
    var gameEndTime: String? = null,

    ) : Serializable {

    constructor() : this("") {

    }
}