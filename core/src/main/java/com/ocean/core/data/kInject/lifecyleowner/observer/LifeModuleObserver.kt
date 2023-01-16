package com.ocean.core.data.kInject.lifecyleowner.observer

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import com.android.core.data.kInject.core.qualifier.Qualifier
import com.android.core.data.kInject.core.register.ModuleRegister


class LifeModuleObserver(var qualifier: Qualifier<*>) : LifecycleObserver {

    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    fun onDestroy(){
        ModuleRegister.instant.removeEntry(qualifier)
    }

}