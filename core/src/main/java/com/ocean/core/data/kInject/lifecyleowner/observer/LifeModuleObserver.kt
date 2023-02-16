package com.ocean.core.data.kInject.lifecyleowner.observer

import android.widget.Toast
import androidx.lifecycle.*
import com.ocean.core.data.kInject.core.qualifier.Qualifier
import com.ocean.core.data.kInject.core.register.ModuleRegister


class LifeModuleObserver(var qualifier: Qualifier<*>) : LifecycleEventObserver {
    override fun onStateChanged(source: LifecycleOwner, event: Lifecycle.Event) {
        if(event == Lifecycle.Event.ON_DESTROY ) {
            ModuleRegister.instant.removeEntry(qualifier)
        }
    }
}