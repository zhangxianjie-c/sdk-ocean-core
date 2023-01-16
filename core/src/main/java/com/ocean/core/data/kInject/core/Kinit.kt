package com.ocean.core.data.kInject.core

import android.app.Application
import com.ocean.core.data.kInject.core.module.Module
import com.ocean.core.data.kInject.core.qualifier.StringQualifier
import com.ocean.core.data.kInject.core.register.GlobalRegister
import com.ocean.core.data.kInject.core.register.ModuleRegister


fun initScope(component: Components.()->Unit){
    component.invoke(Components.get())
}

class Components {

    companion object{

        const val ANDROID_APPLICATION_KEY = "ANDROID_APPLICATION_KEY"

        private val instant by lazy { Components() }
        fun get() = instant
    }

    fun enableLog(){
        Logger.enableLog()
    }

    fun androidContext(context: Application){
        single(ANDROID_APPLICATION_KEY) { context }
    }

    inline fun <reified T> single(typeName:String = T::class.java.name,single: ()-> T){
        GlobalRegister.instant.addEntry(StringQualifier().apply {
            setKeyName(typeName)
        },single())
    }

    /**
     * scopeClazz where your want to inject
     */
    fun <T :Any>module(scopeClazz:T,module: Module){
        ModuleRegister.instant.addEntry(StringQualifier().apply {
            setKeyName(scopeClazz.toString())
            Logger.log("inject into clazz ${scopeClazz::class.java}")
        },module)
    }

}
