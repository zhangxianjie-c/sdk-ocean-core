package com.android.origin.data.kInject.core.qualifier

open class Qualifier<D> {

    private var key : D? = null

    fun getKey() = key

    fun setKeyName(key:D){
        this.key = key
    }


    override fun hashCode(): Int {
        return 31 * getKey().hashCode()
    }

    override fun equals(other: Any?): Boolean {
        return if(other is Qualifier<*>){
            other.key == this.key
        }else{
            false
        }
    }

    override fun toString(): String {
        return "Qualifier[Key:${key}]"
    }

}