package com.ocean.core.framework.initialize.mail

import android.content.Context
import android.util.Log
import android.webkit.MimeTypeMap
import androidx.core.net.toUri
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.util.*
import javax.activation.CommandMap
import javax.activation.MailcapCommandMap
import javax.mail.Address
import javax.mail.Message
import javax.mail.Session
import javax.mail.Transport
import javax.mail.internet.*


/**
 * Created by Zebra-RD张先杰 on 2022年7月8日13:48:51
 *
 *
 * Description:邮件发送管理器与配置
 */
class MailUtils private constructor() {
    companion object {
        private val instance by lazy(mode = LazyThreadSafetyMode.SYNCHRONIZED) { MailUtils() }

        @Synchronized
        fun create() = instance
    }

    private lateinit var mailMessage: Message
    lateinit var context: Context
    fun config(init: Config.() -> Unit) {
        val config = Config.create()
        init(config)
        config.build()
    }

    fun Config.build() {
        // 身份认证器类
        var authenticator = if (validate) MyAuthenticator(username, password)
        else null
        // 邮件相关配置
        val properties = getProperties()
        // 根根配置以及验证器构造一个发送邮件的session
        val sendMailSession = Session.getDefaultInstance(properties, authenticator)
        sendMailSession.debug = true
        // 根据生成的session创建一个待发送的消息
        mailMessage = MimeMessage(sendMailSession)
        val from: Address = InternetAddress(fromAddress)
        // 设置邮件发送者的地址
        mailMessage.setFrom(from)
    }

    /**
     * 发送文本类型的邮件
     * @param
     */
    fun sendMail(context:Context,toAddress: String,copyAddress:String, subject: String, content: String,files:ArrayList<String> = arrayListOf(), error:(()->Unit),success:(()->Unit)) {
        GlobalScope.launch(Dispatchers.IO) {

            val to: Address = InternetAddress(toAddress)
            // 设置邮件接收者的地址
            mailMessage.setRecipient(Message.RecipientType.TO, to)
            // 设置邮件消息的标题
            mailMessage.subject = subject
            // 设置邮件消息发送的时间
            mailMessage.sentDate = Date()
            // 设置发送的正文文本
            val mimeBodyPart = MimeBodyPart()
           mimeBodyPart.setContent(content,"text/html; charset=UTF-8")
            val multipart = MimeMultipart()
            multipart.addBodyPart(mimeBodyPart)
            for (i in 0 until  files.size) {
               val baseFile =  File(context.getExternalFilesDir("Mail"), File.separator + "${System.currentTimeMillis()}.${MimeTypeMap.getFileExtensionFromUrl(files[i])}")
                try {
                    if (baseFile.exists()) {
                        baseFile.delete()
                    }
                    baseFile.createNewFile()
                } catch (e: IOException) {
                    e.printStackTrace()
                }
               val fos =  FileOutputStream(baseFile)
                val fis = context.contentResolver.openInputStream(files[i].toUri())
                try {
                    var len: Int
                    val bt = ByteArray(1024)
                    while (fis!!.read(bt).also { len = it } != -1) {
                        fos.write(bt, 0, len)
                    }
                    fos.flush()
                    fis.close()
                    fos.close()
                } catch (e: IOException) {
                    e.printStackTrace()
                }
               val mimeBodyPart = MimeBodyPart()
                mimeBodyPart.attachFile(baseFile)
                mimeBodyPart.fileName = MimeUtility.encodeText("附件${i}")
                multipart.addBodyPart(mimeBodyPart)
            }
            mailMessage.setRecipients(Message.RecipientType.CC, InternetAddress.parse(copyAddress))
            mailMessage.setContent(multipart)
            // 发送邮件
            try {
                val mc: MailcapCommandMap = CommandMap.getDefaultCommandMap() as MailcapCommandMap
                mc.addMailcap("text/html;; x-java-content-handler=com.sun.mail.handlers.text_html")
                mc.addMailcap("text/xml;; x-java-content-handler=com.sun.mail.handlers.text_xml")
                mc.addMailcap("text/plain;; x-java-content-handler=com.sun.mail.handlers.text_plain")
                mc.addMailcap("multipart/*;; x-java-content-handler=com.sun.mail.handlers.multipart_mixed")
                mc.addMailcap("message/rfc822;; x-java-content-handler=com.sun.mail.handlers.message_rfc822")
                CommandMap.setDefaultCommandMap(mc)
                Thread.currentThread().contextClassLoader = javaClass.classLoader
                Transport.send(mailMessage)
                withContext(Dispatchers.Main){
                    success.invoke()
                }
            }catch (e:java.lang.Exception){
                Log.e("TAG", "sendMail: ${e.toString()}")
                e.printStackTrace()
                withContext(Dispatchers.Main){
                    error.invoke()
                }
            }

        }
    }
}

class Config private constructor() {
    companion object {
        private val instance by lazy(mode = LazyThreadSafetyMode.SYNCHRONIZED) { Config() }

        @Synchronized
        fun create() = instance
    }

    //发送邮件的服务器IP
    lateinit var mailServerHost: String

    //发送邮件的服务器端口
    lateinit var mailServerPort: String

    //邮件服务器用户名
    lateinit var username: String

    //邮件服务器密码
    lateinit var password: String

    //发送者地址
    lateinit var fromAddress: String

    //是否身份认证
    var validate: Boolean = true

    fun getProperties(): Properties {
        val p = Properties()
        p["mail.smtp.host"] = mailServerHost
        p["mail.smtp.port"] = mailServerPort
        p["mail.smtp.auth"] = validate
        return p
    }
}