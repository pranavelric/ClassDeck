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

        cu.startDate = contest.startDate

        cu.isEnded = contest.isEnded

        cu.isStarted = contest.isStarted

        cu.quizTitle = contest.quizTitle


        cu.courseCode = contest.courseCode
        cu.courseName = contest.CourseName
        cu.question_numbers = contest.question_numbers
        cu.rank = 0

        cu.startTime = contest.startTime
        cu.time = contest.time

        cu.wrong = 0

        cu.marks = contest.marks ?: 0

        return cu
    }

    fun userContestToContest(contest: QuizUser): Quiz {

        val cu = Quiz()
        cu.id = contest.id
        cu.answerTime = contest.answerTime

        cu.date = contest.date

        cu.startDate = contest.startDate
        cu.startTime = contest.startTime
        cu.isEnded = contest.isEnded

        cu.isStarted = contest.isStarted
        cu.joined = contest.joined


        cu.quizTitle = contest.quizTitle
        cu.marks = contest.marks
        cu.question_numbers = contest.question_numbers

        cu.startTime = contest.startTime
        cu.time = contest.time

        cu.courseCode = contest.courseCode
        cu.CourseName = contest.courseName

        return cu
    }


}