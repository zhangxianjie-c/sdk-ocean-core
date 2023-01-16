package com.ocean.core.data.kInject.core.register

import com.ocean.core.data.kInject.core.qualifier.Qualifier
import com.ocean.core.data.kInject.core.qualifier.StringQualifier
import java.util.concurrent.ConcurrentHashMap



class GlobalRegister private constructor(){

    companion object{
        val instant by lazy(mode = LazyThreadSafetyMode.SYNCHRONIZED) { GlobalRegister() }
    }

    private val entry = ConcurrentHashMap<Qualifier<*>,Any?>()


    fun addEntry(qualifier: StringQualifier, any: Any?){
        entry[qualifier] = any
    }

    fun getEntry(qualifier: StringQualifier) = entry[qualifier]



}