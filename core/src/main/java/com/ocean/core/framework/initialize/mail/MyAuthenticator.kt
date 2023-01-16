package com.ocean.core.framework.initialize.mail

import javax.mail.Authenticator
import javax.mail.PasswordAuthentication

/**
 * Created by Zebra-RD张先杰 on 2022年7月8日13:45:49
 * Description:
 */
class MyAuthenticator : Authenticator {
    var username: String? = null
    var password: String? = null

    constructor() {}
    constructor(username: String?, password: String?) {
        this.username = username
        this.password = password
    }

    override fun getPasswordAuthentication(): PasswordAuthentication {
        return PasswordAuthentication(username, password)
    }
}