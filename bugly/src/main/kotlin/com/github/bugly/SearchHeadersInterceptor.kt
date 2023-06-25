package com.github.bugly

import okhttp3.Interceptor
import okhttp3.Response
import java.util.UUID

class SearchHeadersInterceptor(private val cookie: String, private val xToken: String) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        return chain.proceed(
            chain.request().newBuilder()
                .addHeader("Accept", "application/json;charset=utf-8")
                .addHeader("Content-Type", "application/json;charset=utf-8")
                .addHeader("Cookie", cookie)
                .addHeader("Host", "bugly.qq.com")
                .addHeader("Referer", "https://bugly.qq.com/v2/workbench/apps")
                .addHeader("User-Agent","Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/114.0.0.0 Safari/537.36")
                .addHeader("X-token", xToken)
                .build()
        )
    }
}