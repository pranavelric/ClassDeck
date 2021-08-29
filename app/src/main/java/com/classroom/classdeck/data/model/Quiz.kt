package com.classroom.classdeck.data.model

import  java.io.Serializable

data class Quiz(


    var id: String,
    var isEnded: Boolean? = false,
    var isStarted: Boolean? = false,

    var date: String? = null,
    var time: String? = null,

    var startTime: String? = null,
    var startDate: String? = null,
    var question_numbers: Int? = 10,
    var answerTime: Int? = 10,
    var quizTitle: String = "",
    var marks: Int? = 0,

    var joined: Int? = 0,

    var courseCode: String = "",
    var CourseName: String = "",


    ) : Serializable {

    constructor() : this("") {

    }

}

