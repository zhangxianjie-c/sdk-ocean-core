package com.ocean.core.data.kInject.core

import android.util.Log



object Logger {

    private val defaultTag = "Kinit"
    private var enableLog = false
    fun enableLog(){
        enableLog = true
    }
    fun log(any: Any?){
        if(enableLog)
        Log.e(defaultTag,"${any}")
    }

}