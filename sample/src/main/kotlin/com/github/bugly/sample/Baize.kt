package com.github.bugly.sample

import com.github.bugly.*
import com.google.gson.Gson
import kotlinx.coroutines.*
import org.apache.poi.ss.usermodel.Row
import org.apache.poi.xssf.usermodel.XSSFWorkbook
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.util.*

private val properties = Properties().apply {
    load(FileInputStream(File("bugly.properties")))
}
private val appId = properties["app.id"].toString()
private val URL_PREFIX = "https://bugly.qq.com/v2/crash-reporting/crashes"

data class CrashRecord(
    val identifier: String,
    val crashTime: String,
    val platform: String,
    val crashType: String,
    val crashError: String,
    val deviceBrand: String,
    val deviceModel: String,
    val deviceOsVersion: String,
    val appVersion: String,
)

fun main(args: Array<String>) {
    runBlocking {

        val src = XSSFWorkbook(FileInputStream(File("crashes.xlsx")))
        val srcSheet = src.getSheetAt(0)

        val issues = (1 until srcSheet.physicalNumberOfRows).map {
            return@map async {
                findIssueByIdentifier(readCrashRecord(srcSheet.getRow(it)))
            }
        }.awaitAll()

        val dest = XSSFWorkbook()
        val destSheet = dest.createSheet()
        val destRow = destSheet.createRow(0)
        copyRow(srcSheet.getRow(0), destRow)
        addCellsToRow(destRow, 18, arrayOf("latest_crash", "crash_list"))

        issues.forEachIndexed { index, issue ->
            val rowNum = index + 1
            val srcRow = srcSheet.getRow(rowNum)
            val destRow = destSheet.createRow(rowNum)
            val latestCrashUrl = createLatestCrashUrl(issue)
            val crashListUrl = createCrashListUrl(issue)
            copyRow(srcRow, destRow)
            addCellsToRow(destRow, 18, arrayOf(latestCrashUrl, crashListUrl))
            println("latest crash: $latestCrashUrl\ncrash list: $crashListUrl")
        }

        src.close()

        val destFos = FileOutputStream(File("crashes-result.xlsx"))
        dest.write(destFos)
        destFos.close()
        dest.close()
        println("search finish.")
    }
}

fun createLatestCrashUrl(issue: Issue?): String {
    return issue?.let { "$URL_PREFIX/$appId/${issue.issueId}?pid=1" }.orEmpty()
}

fun createCrashListUrl(issue: Issue?): String {
    return issue?.let { "$URL_PREFIX/$appId/${issue.issueId}/report?pid=1" }.orEmpty()
}

fun readCrashRecord(src: Row): CrashRecord {
    return CrashRecord(
        src.getCell(0).stringCellValue,
        src.getCell(1).stringCellValue,
        src.getCell(2).stringCellValue,
        src.getCell(3).stringCellValue,
        src.getCell(4).stringCellValue,
        src.getCell(9).stringCellValue,
        src.getCell(10).stringCellValue,
        src.getCell(11).stringCellValue,
        src.getCell(14).stringCellValue,
    )
}

fun copyRow(src: Row, dest: Row) {
    src.cellIterator().forEach {
        dest.createCell(it.columnIndex).setCellValue(it.stringCellValue)
    }
}

fun addCellsToRow(dest: Row, start: Int, args: Array<String>) {
    args.forEachIndexed { index, s ->
        dest.createCell(start + index).setCellValue(s)
    }
}

suspend fun findIssueByIdentifier(crashRecord: CrashRecord, start: Int = 0): Issue? {
    if (!crashRecord.platform.contains("bugly")) return null
    println("search issue in issue list start by: $start")
    val issues = searchIssueList(crashRecord, start)
    println("issues find: ${issues?.numFound ?: 0}")
    val issue = issues?.issueList?.find {
        findCrashByIdentifier(crashRecord, it) != null
    }
    if (issue == null && (issues?.numFound ?: 0) > start + 100) {
        findIssueByIdentifier(crashRecord, start + 100)
    } else {
        return issue
    }
    return null
}

suspend fun findCrashByIdentifier(crashRecord: CrashRecord, issue: Issue, start: Int = 0): String? {
    println("search crash on ${issue.issueId} start by $start")
    val day = crashRecord.crashTime.substring(0..9)
    val crashes = getCrashList(issue.issueId, crashRecord.appVersion, day, start)
    println("crashes find: ${crashes?.numFound}")
    val crash = crashes?.crashDatas?.entries?.find {
        isCrashByIdentifier(crashRecord.identifier, it.key)
    }?.key
    if (crash == null && (crashes?.numFound ?: 0) > start + 100) {
        findCrashByIdentifier(crashRecord, issue, start + 100)
    } else {
        return crash
    }
    return null
}

suspend fun isCrashByIdentifier(identifier: String, crashHash: String): Boolean {
    println("check is crash : $crashHash")
    val attachment = getCrashAttachment(crashHash)
    return attachment?.attachList?.find {
        "identifier" == it.fileName && identifier == it.content
    } != null
}

suspend fun searchIssueList(crashRecord: CrashRecord, start: Int): IssueList? {
    return runCatching {
        val day = crashRecord.crashTime.substring(0..9)
        Bugly.searchIssue(
            appId, 1, start, crashRecord.crashError, crashRecord.deviceModel,
            crashRecord.deviceOsVersion, 1, crashRecord.appVersion, "custom",
            day, day, "desc", "matchCount", 100,
        ).apply {
            println(Gson().toJson(this))
        }.ret
    }.onFailure {
        println("cannot search issue list: $start")
        it.printStackTrace()
    }.getOrNull()
}

suspend fun getIssueList(start: Int): IssueList? {
    return runCatching {
        Bugly.getIssueList(
            appId, start, "errorType",
            "Crash,Native", "1",
            "last_2_day", "desc", "100",
            "uploadTime"
        ).data
    }.onFailure {
        println("cannot get issue list: $start")
        it.printStackTrace()
    }.getOrNull()
}

suspend fun getCrashList(issueId: String, version: String, day: String, start: Int): CrashList? {
    return runCatching {
        Bugly.getCrashList(
            appId, 1, start, "detail",
            "Crash,Native,ExtensionCrash", "undefined",
            1, issueId, version, day, day, 100
        ).data
    }.onFailure {
        println("cannot get crash list: $issueId")
        it.printStackTrace()
    }.getOrNull()
}

suspend fun getCrashAttachment(crashHash: String): CrashAttachment? {
    return runCatching {
        Bugly.getCrashAttachment(appId, 1, crashHash).data
    }.onFailure {
        println("cannot get crash attachment: $crashHash")
        it.printStackTrace()
    }.getOrNull()
}
