package com.classroom.classdeck.util

import com.classroom.classdeck.data.model.Quiz
import com.classroom.classdeck.data.model.QuizUser


object Mappers {


    fun contestToUserContest(contest: Quiz): QuizUser {

        val cu = QuizUser()
        cu.id = contest.id
        cu.answerTime = contest.answerTime

        cu.completed = false
        cu.correct = 0
        cu.date = contest.date

        cu.gameEndDate = contest.gameEndDate

        cu.isEnded = contest.isEnded

        cu.isStarted = contest.isStarted



        cu.question_numbers = contest.question_numbers
        cu.rank = 0

        cu.startTime = contest.startTime
        cu.time = contest.time

        cu.wrong = 0


        return cu
    }

    fun userContestToContest(contest: QuizUser):Quiz{

        val cu = Quiz()
        cu.id = contest.id
        cu.answerTime = contest.answerTime

        cu.date = contest.date

        cu.gameEndDate = contest.gameEndDate
        cu.gameEndTime = contest.gameEndTime
        cu.isEnded = contest.isEnded

        cu.isStarted = contest.isStarted
        cu.joined = contest.joined


        cu.question_numbers = contest.question_numbers

        cu.startTime = contest.startTime
        cu.time = contest.time


        return cu
    }


}