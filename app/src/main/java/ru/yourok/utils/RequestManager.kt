package ru.yourok.utils

import android.util.Log
import org.json.JSONObject

/**
 * Created by yourok on 20.09.17.
 */

class RequestManager(internal var host: String, internal var params: JSONObject) {

    val ST_NONE = 0
    val ST_REQUEST = 1
    val ST_FOUND = 2
    val ST_NOTFOUND = 3
    val ST_ERROR = 4

    var update: JSONObject? = null
    var stat: String = ""
    var statI: Int = 0
    var timestamp: Long = 0

    private var thread: Thread? = null
    private var stop: Boolean = false

    val isFinding: Boolean
        get() = !stop

    private val tsFromMask: Long
        get() {
            try {
                val maskid = params.getString(SystemParams.SYSVER)
                val prop = maskid.split("-|_".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
                if (prop.size == 3)
                    return java.lang.Long.parseLong(prop[1])
            } catch (e: Exception) {
                e.printStackTrace()
            }

            return -1
        }

    init {
        statI = ST_NONE
    }

    fun check(onEndRequest: Runnable?) {
        thread = Thread(Runnable {
            try {
                statI = ST_REQUEST
                val conn = Request(host + "/sysupgrade/check")
                val sparams = params.toString()
                val sign = SystemParams.getSign(sparams)
                val body = "unitType=0&sys=$sparams&sign=$sign"
                conn.sendRequest(body)
                val resp = conn.recvResponce()

                val json = JSONObject(resp)
                Log.i("FlymeFirmwareChecker", json.toString(1))

                if (json.has("code") && json.getString("code") != "200") {
                    stat = json.getString("message")
                    statI = ST_ERROR
                }
                update = json
                if (!Utils.has(update, "reply", "value", "new")) {
                    stat = "No firmware"
                    statI = ST_NOTFOUND
                } else {
                    stat = ""
                    statI = ST_FOUND
                }
            } catch (e: Exception) {
                e.printStackTrace()
                statI = ST_ERROR
                stat = e.message ?: ""
            }

            onEndRequest?.run()
        })
        thread!!.start()
    }

    fun find(onEndRequest: Runnable?, onNext: Runnable?) {
        thread = Thread(Runnable {
            try {
                var count = 0
                stop = false
                while (!stop) {
                    statI = ST_REQUEST
                    val conn = Request(host + "/sysupgrade/check")

                    val ts = tsFromMask
                    if (ts == -1L) {
                        stat = "Wrong mask id"
                        statI = ST_ERROR
                        break
                    }
                    timestamp = ts - 86400/*одни сутки*/
                    setTSToMask(timestamp)

                    onNext?.run()

                    val sparams = params.toString()
                    val sign = SystemParams.getSign(sparams)
                    val body = "unitType=0&sys=$sparams&sign=$sign"
                    conn.sendRequest(body)
                    val resp = conn.recvResponce()

                    val json = JSONObject(resp)
                    Log.i("FlymeFirmwareChecker", json.toString(1))

                    if (json.has("code") && json.getString("code") != "200") {
                        stat = json.getString("message")
                        statI = ST_ERROR
                        break
                    }
                    update = json
                    if (Utils.has(update, "reply", "value", "new")) {
                        stat = ""
                        statI = ST_FOUND
                        break
                    }
                    count++
                    if (count >= 100) {
                        stat = "FW not found"
                        statI = ST_NOTFOUND
                        break
                    }
                    Thread.sleep(1000)
                }
            } catch (e: Exception) {
                e.printStackTrace()
                statI = ST_ERROR
                stat = e.message ?: ""
            }

            stop = true
            onEndRequest?.run()
        })
        thread!!.start()
    }

    fun stop() {
        stop = true
    }

    fun waitRequest() {
        try {
            if (thread != null)
                thread!!.join()
        } catch (e: InterruptedException) {
            e.printStackTrace()
        }
    }

    private fun setTSToMask(ts: Long) {
        try {
            var maskid = params.getString(SystemParams.SYSVER)
            val prop = maskid.split("-|_".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
            if (prop.size == 3) {
                maskid = prop[0] + "-" + ts + "_" + prop[2]
                params.put(SystemParams.SYSVER, maskid)
                params.put(SystemParams.VERSION, maskid)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }
}
