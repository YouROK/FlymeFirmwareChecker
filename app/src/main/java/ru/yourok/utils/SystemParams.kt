package ru.yourok.utils

import android.content.Context
import android.os.Build
import android.telephony.TelephonyManager
import org.json.JSONObject
import java.io.File
import java.security.MessageDigest

/**
 * Created by yourok on 19.09.17.
 */

class SystemParams(private val context: Context) {

    val systemParams: JSONObject
        @Throws(Exception::class)
        get() {
            val basicParams = JSONObject()
            basicParams.put(DEVICE_TYPE, deviceType)
            basicParams.put(FIRMWARE, firmware)
            basicParams.put(ROOT, isRooted)
            basicParams.put(SYSVER, sysver)
            basicParams.put(VERSION, sysver)
            basicParams.put(DEVICE_ID, deviceId)
            basicParams.put(SN, sn)
            basicParams.put(IMEI, deviceId)
            return basicParams
        }

    val deviceType: String
        get() = SystemPropertiesProxy.get(context, "ro.meizu.product.model", "")

    val firmware: String
        get() = SystemPropertiesProxy.get(context, "ro.build.version.release", "")

    val sysver: String
        get() = SystemPropertiesProxy.get(context, "ro.build.mask.id", "")

    val isRooted: String
        get() = if (File("/system/xbin/su").exists()) "1" else "0"

    //imei
    val deviceId: String
        get() {
            try {
                val telephonyManager: TelephonyManager
                telephonyManager = context.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
                return telephonyManager.deviceId
            } catch (e: Exception) {
                e.printStackTrace()
                return ""
            }

        }

    val sn: String
        get() = Build.SERIAL

    companion object {
        val DEVICE_ID = "deviceId"
        val DEVICE_TYPE = "deviceType"
        val FIRMWARE = "firmware"
        val MD5_SIGN = "2635881a7ab0593849fe89e685fc56cd"
        val ROOT = "root"
        val SN = "sn"
        val SYSVER = "sysVer"
        val VERSION = "version"
        val IMEI = "imei"

        val U_HOST = "https://u.meizu.com"
        val U_HOST_INTERNATIONAL = "https://u.in.meizu.com"

        fun getHost(international: Boolean): String {
            return if (international) U_HOST_INTERNATIONAL else U_HOST
        }

        fun getSign(params: String): String? {
            return MD5(params + MD5_SIGN)
        }


        fun MD5(md5: String): String? {
            try {
                val sb = StringBuffer()
                val md = MessageDigest.getInstance("MD5")
                val digest = md.digest(md5.toByteArray())
                for (byte in digest) sb.append("%02x".format(byte))
                return sb.toString()
            } catch (e: java.security.NoSuchAlgorithmException) {
            }

            return null
        }
    }
}