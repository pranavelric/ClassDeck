package com.classroom.classdeck.util

sealed class ResponseState<T>(
    val data: T? = null,
    val message: String? = null
) {


    class Success<T>(data: T) : ResponseState<T>(data)
    class Error<T>(message: String) : ResponseState<T>(message = message)
    class Loading<T> : ResponseState<T>()

}