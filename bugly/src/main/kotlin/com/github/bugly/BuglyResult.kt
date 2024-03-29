package com.github.bugly

import java.io.Serializable

data class BuglyResult<T>(
    val msg: String?,
    val code: String,
    val data: T,
    val traceId: String,
    val timestamp: String
) : Serializable

data class BuglySearchResult<T>(
    val status: Int,
    val msg: String,
    val ret: T
) : Serializable

data class CrashList(
    val issueList: List<String>,
    val crashIdList: List<String>,
    val crashDatas: Map<String, CrashData>,
    val crashNums: Int,
    val anrNums: Int,
    val errorNums: Int,
    val reqSendTimestamp: Long,
    val rspReceivedTimestamp: Long,
    val rspSendTimestamp: Long,
    val statusCode: Int,
    val numFound: Int,
) : Serializable

data class CrashData(
    val productVersion: String,
    val model: String,
    val id: String,
    val uploadTime: String,
    val crashId: String,
    val osVer: String,
) : Serializable

data class CrashDetail(
    val crashMap: CrashMap,
    val detailMap: DetailMap,
    val reponseCode: Int,
    val reponseDesc: String,
    val launchTime: String
) : Serializable

data class CrashMap(
    val id: String,
    val issueId: Int,
    val productVersion: String,
    val model: String,
    val userId: String,
    val expName: String,
    val deviceId: String,
    val crashCount: Int,
    val type: String,
    val processName: String,
    val isRooted: String,
    val retraceStatus: Int,
    val uploadTime: String,
    val crashTime: String,
    val mergeVersion: String,
    val messageVersion: String,
    val isSystemStack: Int,
    val rqdUuid: String,
    val sysRetracStatus: Int,
    val nativeRQDVersion: String,
    val appInBack: String,
    val cpuType: String,
    val isRestore: Boolean,
    val subVersionIssueId: Int,
    val crashId: Int,
    val bundleId: String,
    val sdkVersion: String,
    val osVer: String,
    val expAddr: String,
    val threadName: String,
    val detailDir: String,
    val memSize: String,
    val diskSize: String,
    val imei: String,
    val imsi: String,
    val cpuName: String,
    val brand: String,
    val freeMem: String,
    val freeStorage: String,
    val freeSdCard: String,
    val mac: String,
    val country: String,
    val totalSD: String,
    val channelId: String,
    val startTime: String,
    val isReRetrace: Int,
    val isReClassify: Int,
    val retraceCount: Int,
    val callStack: String,
    val retraceCrashDetail: String,
    val buildNumber: String,
    val rom: String,
    val sendType: String,
    val sendProcess: String,
    val apn: String,
    val appInAppstore: Boolean,
    val modelOriginalName: String,
    val soInfo: String,
) : Serializable

data class DetailMap(
    val attatchCount: Int,
    val stackName: String,
    val retraceCrashDetail: String,
    val freeMem: Int,
    val battery: Int,
    val attachName: String,
    val id: String,
    val fileList: List<File>?,
    val srcIp: String,
    val freeSdCard: Int,
    val serverKey: String,
    val isGZIP: Int,
    val cpu: Int,
    val uploadTime: String,
    val userKey: String,
    val romName: String,
    val contactAll: String,
    val callStack: String,
    val fileDir: String,
    val sdkVersion: String,
    val freeStorage: Int,
) : Serializable

data class File(
    val fileName: String,
    val codeType: Int,
    val fileType: Int,
    val content: String,
) : Serializable

data class IssueList(
    val appId: String,
    val platformId: String,// "1",
    val issueList: List<Issue>,
    val reqSendTimestamp: Long,
    val rspReceivedTimestamp: Long,
    val rspSendTimestamp: Long,
    val statusCode: Int,
    val numFound: Int,
) : Serializable

data class Issue(
    val issueId: String,
    val crashNum: Int,
    val exceptionName: String,
    val exceptionMessage: String,
    val keyStack: String,
    val firstUploadTime: String,
    val lastestUploadTime: String,
    val imeiCount: Int,
    val processor: String,
    val status: Int,
    val tagInfoList: List<TagInfo>,
    val count: Int,
    val sysCount: Int,
    val version: String,
    val issueHash: String,
    val ftName: String,
    val issueVersions: List<IssueVersion>
) : Serializable

data class IssueVersion(
    val version: String,
    val count: Int,
    val deviceCount: Int,
    val firstUploadTime: String,
    val lastUploadTime: String,
) : Serializable

data class TagInfo(
    val tagId: Int,
    val tagType: Int,
    val tagCount: Int,
    val tagName: String
) : Serializable

data class CrashAttachment(
    val attachName: String,
    val stackName: String,
    val attachList: List<File>?,
    val reponseCode: Int,
    val reponseDesc: String,
    val sysLogs: List<String>,
    val userLogs: List<String>
) : Serializable

data class UserInfo(
    val address: String,
    val email: String,
    val idCard: String,
    val isSuper: Int,
    val newUserId: String,
    val nickname: String,
    val phone: String,
    val position: String,
    val qqNickName: String,
    val realname: String,
    val registerTime: String,
    val sex: String,
    val signedAgreement: Int,
    val status: String,
    val userId: String,
    val validated: Int,
    val workingYears: String,
) : Serializable

data class App(
    val appFrom: Int,
    val appId: String,
    val appKey: String,
    val appName: String,
    val betaEnable: Int,
    val createTime: String,
    val enableUserAuit: Int,
    val isGray: Boolean,
    val isSdkApp: Int,
    val logoUrl: String,
    val memberCount: Int,
    val ownerId: String,
    val pid: Int,
    val showAuit: Int,
    val type: Int,
    val userdataEnable: Int,
) : Serializable

data class AppInfo(
    val bundleIdList: List<String>,
    val channelList: List<String>,
    val processorList: List<Processor>,
    val tagList: List<Tag>,
    val versionList: List<Version>,
) : Serializable

data class Processor(
    val name: String,
    val newUserId: String,
    val qqNickName: String,
    val userId: String
) : Serializable

data class Tag(
    val appId: String,
    val platform: Int,
    val tagId: Int,
    val tagName: String,
) : Serializable

data class Version(
    val name: String,
    val enable: Int,
    val sdkVersion: String
) : Serializable

data class IssueInfo(
    val issueList: List<Issue>,
    val reqSendTimestamp: Long,
    val rspReceivedTimestamp: Long,
    val statusCode: Int,
    val numFound: Int,
) : Serializable