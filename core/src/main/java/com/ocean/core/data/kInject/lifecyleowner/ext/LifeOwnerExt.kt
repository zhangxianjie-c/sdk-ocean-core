package com.ocean.core.data.kInject.lifecyleowner.ext

import androidx.lifecycle.LifecycleOwner
import com.android.core.data.kInject.core.Logger
import com.android.core.data.kInject.core.qualifier.StringQualifier
import com.android.core.data.kInject.core.register.ModuleRegister

inline fun <reified T> getLifeOwner(scopeClazz: T) : LifecycleOwner {
    return ModuleRegister.instant.getEntry(StringQualifier().apply {
        setKeyName(scopeClazz.toString())
        Logger.log("inject Qualifier ${this}")
    })?.get(scopeClazz.toString()) as LifecycleOwner
}

inline fun <reified T> getLifeOwnerOrNull(scopeClazz: T) : LifecycleOwner? {
    return ModuleRegister.instant.getEntry(StringQualifier().apply {
        setKeyName(scopeClazz.toString())
        Logger.log("inject Qualifier ${this}")
    })?.get(scopeClazz.toString()) as LifecycleOwner?
}


inline fun <reified T> injectLifeOwner(scopeClazz: T) : Lazy<LifecycleOwner> {
    return lazy {
        ModuleRegister.instant.getEntry(StringQualifier().apply {
            setKeyName(scopeClazz.toString())
            Logger.log("inject Qualifier ${this}")
        })?.get(scopeClazz.toString()) as LifecycleOwner
    }
}
