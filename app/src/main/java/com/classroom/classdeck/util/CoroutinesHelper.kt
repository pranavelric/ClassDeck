package com.classroom.classdeck.util

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

object CoroutinesHelper {

    fun delayWithMain(delayTime: Long, work: suspend (() -> Unit)) {
        CoroutineScope(Dispatchers.Main).launch {
            delay(delayTime)
            work()
        }

    }

}