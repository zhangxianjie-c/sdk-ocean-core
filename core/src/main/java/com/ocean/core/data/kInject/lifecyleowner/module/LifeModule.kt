package com.ocean.core.data.kInject.lifecyleowner.module

import androidx.lifecycle.LifecycleOwner
import com.android.core.data.kInject.core.module.Module
import com.android.core.data.kInject.core.qualifier.Qualifier
import com.android.core.data.kInject.lifecyleowner.observer.LifeModuleObserver

fun LifecycleOwner.lifeModule(scope: LifeModule.()->Unit): LifeModule {
    val moduleBean = LifeModule(this)
    scope.invoke(moduleBean)
    return moduleBean
}

class LifeModule(var lifecycleOwner: LifecycleOwner) : Module() {

    override fun setParentKey(qualifier: Qualifier<*>?) {
        qualifier?.apply {
            lifecycleOwner.lifecycle.addObserver(LifeModuleObserver(qualifier))
        }
    }

    fun scopeLifeOwner(scopeClazz:Any){
        scopeByName(scopeClazz.toString()) { lifecycleOwner }
    }

}