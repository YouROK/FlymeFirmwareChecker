package ru.yourok.packagemanager

import android.app.Activity
import android.content.Context
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.graphics.drawable.Drawable
import android.widget.Toast
import com.jrummyapps.android.shell.Shell
import ru.yourok.utils.Utils
import java.io.File
import kotlin.concurrent.thread


/**
 * Created by yourok on 08.02.18.
 */

class PackageRemover(val context: Context) {
    private val TAG: String
        get() = this::class.java.simpleName

    private var listApps: MutableList<AppInfo> = mutableListOf()

    fun updateList() {
        val pm = context.getPackageManager()
        val apps = pm.getInstalledApplications(PackageManager.GET_META_DATA)
        listApps.clear()
        apps?.forEach { it ->
            var ignore = false
            ignorePackages.forEach { ignorePkg ->
                if (it.packageName.startsWith(ignorePkg)) {
                    ignore = true
                    return@forEach
                }
            }
            if ((!ignore || isRemove(it)) && File(it.sourceDir).exists()) {
                val icon = context.getPackageManager().getApplicationIcon(it.packageName)
                val name = it.name ?: it.loadLabel(pm).toString()
                listApps.add(AppInfo(it, name, icon, isRemove(it)))
            }
        }

        sortList()
    }

    fun getList(): List<AppInfo> {
        return listApps.toList()
    }

    fun getAppInfo(i: Int): AppInfo? {
        if (i in 0 until listApps.size)
            return listApps[i]
        return null
    }

    fun size(): Int {
        return listApps.size
    }

    fun saveRemoveList() {
        var removeList = mutableListOf<String>()//removeApps.toMutableList()
        listApps.forEach {
            val pkg = it.appInfo.packageName
            if (it.remove)
                removeList.add(pkg)
        }
        saveRemoveList(removeList)
    }

    fun removeApps(context: Activity, onOutput: ((output: String) -> Unit)?, onEnd: (() -> Unit)?) {
        thread {
            if (!Shell.SU.available()) {
                context.runOnUiThread {
                    Toast.makeText(context, "root access denied", Toast.LENGTH_SHORT).show()
                }
            } else {
                var result = Shell.SU.run("mount -o rw,remount /system")
                if (!result.isSuccessful) {
                    val err = result.stderr.joinToString("\n") + "\n"
                    onOutput?.invoke("Error remount system: $err")
                    onEnd?.invoke()
                    return@thread
                }

                listApps.forEach {
                    if (it.remove) {
                        val apkDir = File(it.appInfo.sourceDir).parentFile.absolutePath
                        val dataDir = File(it.appInfo.dataDir).absolutePath

                        val cmdRmApk = "rm -rf ${apkDir}"
                        val cmdRmData = "rm -rf ${dataDir}"
                        onOutput?.invoke("Delete: ${it.nameApp}")
                        var err = ""
                        result = Shell.SU.run(cmdRmApk)
                        if (!result.isSuccessful) {
                            err += result.stderr.joinToString("\n")
                            onOutput?.invoke("Error remove: ${err}")
                        }
                        if (File(dataDir).exists()) {
                            result = Shell.SU.run(cmdRmData)
                            if (!result.isSuccessful) {
                                err += result.stderr.joinToString("\n")
                                onOutput?.invoke("Error remove: ${err}")
                            }
                        }
                    }
                }
                onEnd?.invoke()
            }
        }
    }

    private fun isRemove(appInfo: ApplicationInfo): Boolean {
        val list = getRemoveList()
        if (list.contains(appInfo.packageName))
            return true
        list.forEach {
            if (File(appInfo.sourceDir).nameWithoutExtension == it)
                return true
        }
        return false
    }

    fun defaultRemoveList() {
        if (File(context.getFilesDir().getPath(), "RemoveList.txt").delete())
            updateList()
    }

    private fun getRemoveList(): Set<String> {
        var file: File? = null
        try {
            file = File(context.getFilesDir().getPath(), "RemoveList.txt")
            val fstr = Utils.readFile(file).trim()
            if (fstr.isNotEmpty()) {
                var list = fstr.split("\n")
                list = list.filter { !it.isEmpty() }
                if (list.size > 0)
                    return list.toSet()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return removeApps
    }

    private fun saveRemoveList(list: MutableList<String>) {
        try {
            val file = File(context.getFilesDir().getPath(), "RemoveList.txt")
            Utils.writeFile(file, list.joinToString("\n"))
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun sortList() {
        listApps.sortWith(compareBy(
                { !it.remove },
                { !it.appInfo.packageName.startsWith("com.meizu") },
                { !it.appInfo.packageName.startsWith("com.flyme") },
                { it.appInfo.packageName }))
    }

    private var removeApps = setOf<String>(
            "com.meizu.account.pay",
            "com.meizu.cloud",
            "com.meizu.compaign",
            "com.meizu.feedback",
            "com.meizu.flyme.gamecenter",
            "com.meizu.flyme.update",
            "com.meizu.flyme.weather",
            "com.meizu.flyme.wallet",
            "com.meizu.gamecenter.service",
            "com.meizu.media.ebook",
            "com.meizu.media.life",
            "com.meizu.media.reader",
            "com.meizu.mstore",
            "com.meizu.mznfcpay",
            "com.meizu.netcontactservice",
            "com.meizu.remotecooperation",
            "com.meizu.yellowpage",
            "com.meizu.voiceassistant",
            "com.flyme.videoclips",
            "com.android.browser",
            "com.iflytek.speechsuite",
            "com.unionpay.tsmservice",

            "com.meizu.flyme.telecom",
            "com.snowballtech.walletservice",
            "com.android.flyme.bridge.softsim",
            "com.flyme.virtual.softsim",
            "com.flyme.roamingpay",

            "com.sankuai.meituan",
            "com.baidu.BaiduMap",
            "ctrip.android.view",
            "com.netease.newsreader.activity",
            "com.qq.reader",
            "com.sohu.inputmethod.sogou",
            "com.tmall.wireless",
            "com.achievo.vipshop",
            "com.sina.weibo",
            "com.meizu.flyme.mall",
            "com.jingdong.app.mall",
            "com.sina.weibo",
            "com.ximalaya.ting.android",
            "com.autonavi.minimap",
            "com.netease.newsreader.activity",
            "com.Qunar"
    )

    private val ignorePackages = setOf<String>(
            "android",
            "ru.yourok"
    )

    data class AppInfo(var appInfo: ApplicationInfo,
                       var nameApp: String,
                       var icon: Drawable,
                       var remove: Boolean)
}