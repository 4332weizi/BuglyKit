package com.github.bugly

import io.reactivex.rxjava3.core.Observable
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Query
import java.io.Serializable

const val BASE_URL = "https://bugly.qq.com/v4/api/old/"

// https://developer.mozilla.org/zh-CN/docs/Web/HTTP/Headers/Cache-Control
const val CACHE_NO = "no-store"
const val CACHE_MINUTE = "public, immutable, max-stale=60"
const val CACHE_HOUR = "public, immutable, max-stale=3600"
const val CACHE_DAY = "public, immutable, max-stale=86400"
const val CACHE_MONTH = "public, immutable, max-stale=2592000"
const val CACHE_MAX = "public, immutable, max-stale=${Integer.MAX_VALUE}"

private const val GET_USER_INIO = "info"
private const val GET_APP_LIST = "app-list"
private const val GET_APP_INFO = "app-info"
private const val GET_ISSUE_INFO = "get-issue-info"
private const val GET_ISSUE_LIST = "get-issue-list"
private const val GET_CRASH_LIST = "get-crash-list"
private const val GET_CRASH_DETAIL = "get-crash-detail"
private const val GET_CRASH_ATTACHMENT = "get-crash-attachment"
private const val GET_LAST_CRASH = "get-last-crash"

interface BuglyService {

    @GET(GET_USER_INIO)
    fun getUserInfo(
        @Header("Cache-Control") cacheControl: String = CACHE_DAY,
    ): Observable<BuglyResult<UserInfo>>

    @GET(GET_APP_LIST)
    fun getAppList(
        @Query("userId") userId: String,
        @Header("Cache-Control") cacheControl: String = CACHE_DAY,
    ): Observable<BuglyResult<List<App>>>

    @GET(GET_APP_INFO)
    fun getAppInfo(
        @Query("appId") appId: String,
        @Query("pid") pid: Int,
        @Query("types") types: String = "version,member,tag,channel,bundle",
        @Header("Cache-Control") cacheControl: String = CACHE_DAY,
    ): Observable<BuglyResult<AppInfo>>

    @GET(GET_ISSUE_INFO)
    fun getIssueInfo(
        @Query("appId") appId: String,
        @Query("pid") pid: Int,
        @Query("issueId") issueId: Int,
        @Query("crashDataType") crashDataType: String = "undefined",
        @Query("exceptionTypeList") exceptionTypeList: String = "Crash,Native,ExtensionCrash",
        @Header("Cache-Control") cacheControl: String = CACHE_MAX,
    ): Observable<BuglyResult<IssueInfo>>

    @GET(GET_ISSUE_LIST)
    fun getIssueList(
        @Query("appId") appId: String,
        @Query("start") start: Int,
        @Query("searchType") searchType: String,//=errorType&
        @Query("exceptionTypeList") exceptionTypeList: String,//=Crash,Native&
        @Query("platformId") platformId: String,//=1&
        @Query("date") date: String,//=last_7_day&
        @Query("sortOrder") sortOrder: String,//=desc&
        @Query("rows") rows: String,//=100&
        @Query("sortField") sortField: String,//=uploadTime&
        @Header("Cache-Control") cacheControl: String = CACHE_MINUTE,
    ): Observable<BuglyResult<IssueList>>

    @GET(GET_CRASH_LIST)
    fun getCrashList(
        @Query("appId") appId: String,
        @Query("pid") pid: Int,
        @Query("start") start: Int,//=100&
        @Query("searchType") searchType: String,//=detail&
        @Query("exceptionTypeList") exceptionTypeList: String,//=Crash,Native,ExtensionCrash&
        @Query("crashDataType") crashDataType: String = "unSystemExit",
        @Query("platformId") platformId: Int,//=1
        @Query("issueId") issueId: Int,//=1567363&
        @Query("rows") rows: Int,//=100&
        @Header("Cache-Control") cacheControl: String = CACHE_HOUR,
    ): Observable<BuglyResult<CrashList>>

    @GET(GET_CRASH_DETAIL)
    fun getCrashDetail(
        @Query("appId") appId: String,
        @Query("pid") pid: Int,
        @Query("crashHash") crashHash: String,
        @Header("Cache-Control") cacheControl: String = CACHE_MAX,
    ): Observable<BuglyResult<CrashDetail>>

    @GET(GET_CRASH_ATTACHMENT)
    fun getCrashAttachment(
        @Query("appId") appId: String,
        @Query("pid") pid: Int,
        @Query("crashHash") crashHash: String,
        @Header("Cache-Control") cacheControl: String = CACHE_MAX,
    ): Observable<BuglyResult<CrashAttachment>>

    @GET(GET_LAST_CRASH)
    fun getLastCrash(
        @Query("appId") appId: String,
        @Query("pid") pid: Int,
        @Query("issueId") issueId: Int,
        @Query("crashDataType") crashDataType: String,
        @Header("Cache-Control") cacheControl: String = CACHE_MINUTE,
    ): Observable<BuglyResult<CrashMap>>
}