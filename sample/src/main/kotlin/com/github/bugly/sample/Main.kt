package com.github.bugly.sample

import com.github.bugly.*
import com.google.gson.Gson
import io.reactivex.rxjava3.core.Observable
import java.io.File
import java.io.FileInputStream
import java.lang.NullPointerException
import java.util.*
import java.util.concurrent.atomic.AtomicInteger

private val properties = Properties().apply {
    load(FileInputStream(File("bugly.properties")))
}
private val appId = properties["app.id"].toString()

fun main(args: Array<String>) {

    val crashList = mutableListOf<CrashData>()

    val models = mutableMapOf<String, Int>()
    val osVers = mutableMapOf<String, Int>()
    val users = mutableMapOf<String, Pair<Int, MutableSet<String>>>()

    val time = mutableMapOf<String, Int>()

    val registerInfos = mutableMapOf<String, Int>()

    var count = AtomicInteger(0)
    var errorCount = AtomicInteger(0)

    Observable.zip(
        IntRange(0, 21).toList()
            .map { it * 100 }
            .map { start ->
                queryCrashList(start).retry(3)
                    .map {
                        it.data.crashDatas.values
                    }
            }
    ) { results ->
        mutableListOf<CrashData>().apply {
            results.map {
                it as Iterable<CrashData>
            }.forEach {
                addAll(it)
            }
        }
    }.doOnNext {
        crashList.addAll(it)
    }.doOnNext {
        it.forEach { crash ->
            models[crash.model] = (models[crash.model] ?: 0) + 1
            osVers[crash.osVer] = (osVers[crash.osVer] ?: 0) + 1
        }
    }.map {
        crashList.map { it.id }.map {
            Bugly.getCrashDetail(appId, 1, it).map {
                it.data
            }.doOnNext {
                if (it == null || it.reponseCode == -1) {
                    println("${it} -> ${errorCount.incrementAndGet()} ERROR")
                    throw NullPointerException(it.toString())
                }
            }
        }
    }.flatMap {
        Observable.mergeDelayError(it)
    }.doOnEach {
        count.getAndIncrement()
    }.doOnError {
        errorCount.getAndIncrement()
    }.doOnNext { detail ->
        // println(Gson().toJson(detail))
        // println("${detail.crashMap.id} -> ${count.incrementAndGet()}")
        users[detail.crashMap.userId] = (users[detail.crashMap.userId] ?: Pair(0, mutableSetOf())).let {
            Pair(it.first + 1, it.second.apply {
                add(detail.crashMap.model)
            })
        }
        // detail.detailMap.fileList?.filter {
        //     it.fileName == "crashInfos.txt"
        // }?.forEach {
        //     Regex("r[0-9]{1,2}=0x[0-9a-z]{8,16}[\\s\\n]").findAll(it.fileContent).map {
        //         it.value.trim().split("=")[1]
        //     }.toList().apply {
        //         println("${detail.crashMap.id} -> ${count.get()} ${joinToString(",")}")
        //     }.forEach {
        //         registerInfos[it] = (registerInfos[it] ?: 0) + 1
        //     }
        // }
        val t = when (detail.launchTime.toLong()) {
            in 0..2 -> "<2"
            in 3..5 -> "3~5"
            in 6..10 -> "6~10"
            in 11..15 -> "11~15"
            in 16..20 -> "16~20"
            in 21..30 -> "21~30"
            in 31..50 -> "31~50"
            in 51..100 -> "51~100"
            in 101..500 -> "101~500"
            in 501..1500 -> "501~1500"
            else -> "1501~"
        }
        time[t] = (time[t] ?: 0) + 1
    }.doOnNext {
        writeLog(it)
    }.doFinally {
        models.forEach {
            println("${it.key}\t${it.value}\t${String.format("%.2f", (it.value.toFloat() * 100 / crashList.size))}%")
        }
        osVers.forEach {
            println("${it.key}\t${it.value}\t${String.format("%.2f", (it.value.toFloat() * 100 / crashList.size))}%")
        }
        users.forEach {
            println(
                "${it.key}\t${it.value.first}\t${
                    String.format(
                        "%.2f",
                        (it.value.first.toFloat() * 100 / crashList.size)
                    )
                }%\t${it.value.second.joinToString(",")}"
            )
        }
        time.forEach {
            println("${it.key}\t${it.value}\t${String.format("%.2f", (it.value.toFloat() * 100 / crashList.size))}%")
        }
        registerInfos.filter {
            it.value >2
        }.forEach {
            println("${it.key}\t${it.value}")
        }
        println("count:${count.get()}")
        println("errorCount:${errorCount.get()}")
    }.subscribe({

    }) {
        it.printStackTrace()
    }
}

fun writeLog(detail: CrashDetail) {
    val path = "logs/${detail.crashMap.productVersion}/${detail.crashMap.model}/${detail.crashMap.crashId}"
    File(path).mkdirs()
    val crashMap = StringBuilder()
    CrashMap::class.java.declaredFields.forEach {
        it.isAccessible = true
        if (it.name.equals("callStack", true)
            || it.name.equals("retraceCrashDetail", true)
        ) {
            crashMap.append("${it.name} : ${System.lineSeparator()}")
            crashMap.append(
                "${it.get(detail.crashMap)}".lines().map { "  $it" }
                    .joinToString(System.lineSeparator())
            )
            crashMap.append(System.lineSeparator())
        } else {
            crashMap.append("${it.name} : ${it.get(detail.crashMap)}\n")
        }
    }
    writeLogIfFileEmpty(path, "crashMap.txt", crashMap.toString())
    detail.detailMap.fileList.orEmpty().filter {
        !it.fileContent.isNullOrBlank()
    }.filter {
        it.fileType != 2
    }.filter {
        !arrayListOf(
            "APP_ID",
            "deviceId",
            "fingerId",
            "userId",
            "tomb.zip"
        ).contains(it.fileName)
    }.forEach {
        writeLogIfFileEmpty(path, it.fileName, it.fileContent)
    }
}

fun writeLogIfFileEmpty(path: String, name: String, content: String) {
    val file = File(path, if (name.endsWith(".txt", true)) name else "$name.txt")
    if (!file.exists() && file.length() == 0L) {
        file.createNewFile()
        file.writeText(content)
    }
}

fun queryCrashList(start: Int) = Bugly.getCrashList(
    appId,
    1,
    start,
    "detail",
    "Crash,Native,ExtensionCrash",
    "unSystemExit",
    1,
    3050032,
    100,
)
