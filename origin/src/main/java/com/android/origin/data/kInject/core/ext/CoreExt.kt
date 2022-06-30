package com.android.origin.data.kInject.core.ext

import android.app.Application
import com.android.origin.data.kInject.core.Components
import com.android.origin.data.kInject.core.Logger
import com.android.origin.data.kInject.core.qualifier.StringQualifier
import com.android.origin.data.kInject.core.register.GlobalRegister
import com.android.origin.data.kInject.core.register.ModuleRegister


fun androidContext(): Application? = getSingle(Components.ANDROID_APPLICATION_KEY)

fun androidContextNotNull(): Application {
    return getSingle(Components.ANDROID_APPLICATION_KEY)
        ?: error("Android application context is null")
}

inline fun <reified T> single(typeName: String = T::class.java.name): Lazy<T> =
    lazy(mode = LazyThreadSafetyMode.SYNCHRONIZED) {
        GlobalRegister.instant.getEntry(StringQualifier().apply {
            setKeyName(typeName)
        }) as T
    }

inline fun <reified T> getSingle(typeName: String = T::class.java.name): T =
    GlobalRegister.instant.getEntry(StringQualifier().apply {
        setKeyName(typeName)
    }) as T


inline fun <reified T> inject(scopeClazz: Any, groupName: String = T::class.java.name): Lazy<T> {
    return lazy {
        ModuleRegister.instant.getEntry(StringQualifier().apply {
            setKeyName("$scopeClazz")
            Logger.log("inject Qualifier ${this}")
        })?.get(groupName) as T
    }
}

inline fun <reified T> getInject(scopeClazz: Any, groupName: String = T::class.java.name): T =
    ModuleRegister.instant.getEntry(
        StringQualifier().apply {
            setKeyName("$scopeClazz")
            Logger.log("inject Qualifier ${this}")
        })?.get(groupName) as T


inline fun <reified T> factory(scopeClazz: Any, groupName: String = T::class.java.name): T {
    return ModuleRegister.instant.getEntry(StringQualifier().apply {
        setKeyName("$scopeClazz")
        Logger.log("inject Qualifier ${this}")
    })?.getFactory(groupName) as T
}


inline fun <reified T> lazyFactory(scopeClazz: Any, groupName: String = T::class.java.name): Lazy<T> {
    return lazy {
        ModuleRegister.instant.getEntry(StringQualifier().apply {
            setKeyName("$scopeClazz")
            Logger.log("inject Qualifier ${this}")
        })?.getFactory(groupName) as T
    }
}