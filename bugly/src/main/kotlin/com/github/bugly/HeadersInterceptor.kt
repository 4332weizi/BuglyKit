package com.github.bugly

import okhttp3.Interceptor
import okhttp3.Response
import java.util.UUID

class HeadersInterceptor(private val cookie: String) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        return chain.proceed(
            chain.request().newBuilder()
                .addHeader("Accept", "application/json;charset=utf-8")
                .addHeader("Content-Type", "application/json;charset=utf-8")
                .addHeader("Cookie", cookie)
                .addHeader("Host", "bugly.qq.com")
                .build()
        )
    }
}