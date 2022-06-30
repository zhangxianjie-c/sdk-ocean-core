package com.android.origin.data.kInject.lifecyleowner.observer

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import com.android.origin.data.kInject.core.qualifier.Qualifier
import com.android.origin.data.kInject.core.register.ModuleRegister


class LifeModuleObserver(var qualifier: Qualifier<*>) : LifecycleObserver {

    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    fun onDestroy(){
        ModuleRegister.instant.removeEntry(qualifier)
    }

}