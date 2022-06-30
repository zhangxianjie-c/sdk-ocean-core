package com.jie.tungcheung.utils.kInject.coroutines

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import com.android.origin.data.kInject.core.Logger
import com.android.origin.data.kInject.core.module.Module
import com.android.origin.data.kInject.core.qualifier.StringQualifier
import com.android.origin.data.kInject.core.register.GlobalRegister
import com.android.origin.data.kInject.core.register.ModuleRegister
import com.jie.tungcheung.utils.kInject.lifecyleowner.module.LifeModule
import kotlinx.coroutines.*


suspend fun coroutineModule(scope: suspend Module.()->Unit): Module {
    val moduleBean = Module()
    scope.invoke(moduleBean)
    return moduleBean
}

suspend fun LifecycleOwner.coroutineModule(scope: suspend LifeModule.()->Unit): Module {
    val moduleBean = LifeModule(this)
    scope.invoke(moduleBean)
    return moduleBean
}

fun initAsyncScope(component: AsyncComponents.()->Unit){
    component.invoke(AsyncComponents.get())
}

class AsyncComponents  {

    companion object{

        private val instant by lazy { AsyncComponents() }
        fun get() = instant

        @ObsoleteCoroutinesApi
        val fixedThread = newFixedThreadPoolContext(4,"AsyncComponents")
    }

    fun enableLog(){
        Logger.enableLog()
    }


    @ObsoleteCoroutinesApi
    inline fun <reified T>singleAsync(typeName:String = T::class.java.name, crossinline single:()->T){
        GlobalScope.launch(fixedThread) {
            GlobalRegister.instant.addEntry(StringQualifier().apply {
                setKeyName(T::class.java.name)
            },single.invoke())
        }
    }

    @ObsoleteCoroutinesApi
    fun <T :Any>moduleAsync(scopeClazz:T, scope: suspend CoroutineScope.() -> Module){
        GlobalScope.launch(fixedThread) {
            val module = scope()
            ModuleRegister.instant.addEntry(StringQualifier().apply {
                setKeyName(scopeClazz.toString())
                Logger.log("inject into clazz ${scopeClazz::class.java}")
            },module)

        }
    }

    @ObsoleteCoroutinesApi
    fun <T :Any>LifecycleOwner.moduleLifeAsync(scopeClazz:T, scope: suspend CoroutineScope.() -> Module){
        lifecycleScope.launchWhenCreated {
             withContext(fixedThread){
                 val module = scope()
                 ModuleRegister.instant.addEntry(StringQualifier().apply {
                     setKeyName(scopeClazz.toString())
                     Logger.log("inject into clazz ${scopeClazz::class.java}")
                 },module)
             }
        }
    }

}
