package com.github.bugly

import okhttp3.Cache
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.adapter.rxjava3.RxJava3CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.io.File
import java.io.FileInputStream
import java.util.*

private val properties = Properties().apply {
    load(FileInputStream(File("bugly.properties")))
}

private val cookie = properties["cookie"].toString()
private val cacheSize = properties["cache.size"].toString().toLong()
private val cacheDir = properties["cache.dir"].toString()

private val okHttpClient = OkHttpClient.Builder()
    .cache(
        Cache(
            directory = File(cacheDir),
            maxSize = cacheSize * 1024 * 1024 * 1024
        )
    )
    .addInterceptor(HeadersInterceptor(cookie))
    .sslSocketFactory(createSSLSocketFactory(), TrustAllManager())
    .build()

private val retrofit: Retrofit = Retrofit.Builder()
    .baseUrl(BASE_URL)
    .client(okHttpClient)
    .addCallAdapterFactory(RxJava3CallAdapterFactory.create())
    .addConverterFactory(GsonConverterFactory.create())
    .build()

private val service = retrofit.create(BuglyService::class.java)

object Bugly : BuglyService by service