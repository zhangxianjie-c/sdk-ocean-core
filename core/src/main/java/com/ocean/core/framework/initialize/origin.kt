package com.ocean.core.framework.initialize

import android.content.Context
import com.ocean.core.framework.SystemOS
import com.ocean.core.framework.initialize.crash.CrashHandler
import com.ocean.core.framework.initialize.mail.MailUtils
import com.ocean.core.framework.network.control.Network


/**
Created by Zebra-RD张先杰 on 2022年7月27日10:38:11

Description:模块汇总
 */
fun Context.origin(init: Origin.() -> Unit) {
    val origin = Origin.create()
    origin.context = this
    init.invoke(origin)
}
class Origin private constructor() {
    companion object {
        private val instance by lazy(mode = LazyThreadSafetyMode.SYNCHRONIZED) { Origin() }

        @Synchronized
        fun create() = instance
    }
    lateinit var context: Context
    fun network(onConfig: (Network.() -> Unit)) {
        val http = Network.create()
        http.context = context
        onConfig(http)
    }

    fun mail(init: MailUtils.() -> Unit) {
        val mail = MailUtils.create()
        mail.context = context
        init(mail)
    }

    fun crash(init: CrashHandler.() -> Unit) {
        val crash = CrashHandler.create()
        crash.context = context
        init(crash)
    }

    fun initializeSystemOS(init: SystemOS.() -> Unit) {
        var systemOS = SystemOS
        systemOS.initialize(context)
        init.invoke(systemOS)
    }
}