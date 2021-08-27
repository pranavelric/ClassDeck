package com.classroom.classdeck.data.model

import  java.io.Serializable

data class Quiz(


    var id: String,
    var isEnded: Boolean? = false,
    var isStarted: Boolean? = false,
    var date: String? = null,
    var time: String? = null,
    var startTime: String? = null,
    var gameEndTime: String? = null,
    var question_numbers: Int? = 10,
    var gameEndDate: String? = null,
    var answerTime: Int? = 10,
    var joined: Int? = 0,


    ) : Serializable {

    constructor() : this("") {

    }

}

