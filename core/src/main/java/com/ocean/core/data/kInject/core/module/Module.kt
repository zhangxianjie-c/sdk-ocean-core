package com.ocean.core.data.kInject.core.module

import com.android.core.data.kInject.core.qualifier.Qualifier
import java.util.concurrent.ConcurrentHashMap



fun module(scope: Module.() -> Unit): Module {
    val moduleBean = Module()
    scope.invoke(moduleBean)
    return moduleBean
}


open class Module {

    var qualifier: Qualifier<*>? = null
        set(value) {
            field = value
            setParentKey(field)
        }
    private val entrySingle by lazy { ConcurrentHashMap<String, Any>() }

    private val entryFactory by lazy { ConcurrentHashMap<String, () -> Any?>() }

    private fun getEntry() = entrySingle

    private fun getFactoryEntry() = entryFactory

    /**
     * If the class type are the same , cause the data lose , Use Method {@link #scopeByName}
     */
    fun scope(single: () -> Any) {
        val scopeData = single()
        addSingle(scopeData::class.java.name, scopeData)
    }

    inline fun <reified T> factory(noinline factory: () -> T?) {
        addFactory(T::class.java.name, factory)
    }

    fun addFactory(groupName: String, any: () -> Any?) {
        if (getFactoryEntry()[groupName] == null) {
            getFactoryEntry()[groupName] = any
        }
    }


    /**
     * When class type are the same , distinguish them by name
     */
    fun scopeByName(groupName: String, single: () -> Any) {
        addSingle(groupName, single())
    }

    private fun addSingle(groupName: String, any: Any) {
        if (getEntry()[groupName] == null) {
            getEntry()[groupName] = any
        }
    }

    fun get(name: String): Any? {
        return getEntry()[name]
    }

    fun getFactory(name: String): Any? {
        return getFactoryEntry()[name]?.invoke()
    }

    open fun setParentKey(qualifier: Qualifier<*>?) {

    }

    /**
     * Be careful to use it, it will clean all value
     */
    fun clear(){
        entryFactory.clear()
        entrySingle.clear()
    }
}