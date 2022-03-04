package com.github.bugly

import java.security.SecureRandom
import java.security.cert.X509Certificate
import javax.net.ssl.SSLContext
import javax.net.ssl.SSLSocketFactory
import javax.net.ssl.X509TrustManager

fun createSSLSocketFactory(): SSLSocketFactory {
    return SSLContext.getInstance("TLS").apply {
        init(null, arrayOf(TrustAllManager()), SecureRandom())
    }.socketFactory
}

class TrustAllManager : X509TrustManager {
    override fun checkClientTrusted(chain: Array<X509Certificate?>?, authType: String?) {}

    override fun checkServerTrusted(chain: Array<X509Certificate?>?, authType: String?) {}

    override fun getAcceptedIssuers(): Array<X509Certificate?> = arrayOfNulls(0)
}