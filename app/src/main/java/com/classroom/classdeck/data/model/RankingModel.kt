package com.classroom.classdeck.data.model

data class RankingModel(
    val userId:String?="",
    var userPhoneNumber:String?="",
    var userName:String?="",
    var userEmail:String?="",
    var rank:Int?=0,
    var score:Int?=0
)
