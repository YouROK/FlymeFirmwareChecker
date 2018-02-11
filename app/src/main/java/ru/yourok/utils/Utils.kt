package ru.yourok.utils

import android.util.Log
import org.json.JSONObject

import java.io.BufferedReader
import java.io.File
import java.io.FileReader
import java.io.FileWriter
import java.net.URLEncoder

/**
 * Created by yourok on 20.09.17.
 */

object Utils {
    fun has(json: JSONObject?, vararg names: String): Boolean {
        try {
            var js = json ?: return false
            for (i in 0 until names.size - 1) {
                if (js.has(names[i]))
                    js = js.getJSONObject(names[i])
                else
                    return false
            }
            return js.has(names[names.size - 1]) && !js.isNull(names[names.size - 1])
        } catch (e: Exception) {
            return false
        }
    }

    fun readFile(file: File): String {
        val br = BufferedReader(FileReader(file))
        val text = br.readText()
        br.close()
        return text
    }

    fun writeFile(file: File, txt: String) {
        if (file.exists())
            file.delete()
        val fw = FileWriter(file)
        fw.write(txt)
        fw.close()
    }

    fun translate(trTo: String, update: JSONObject, onEndRequest: Runnable?) {
        val th = Thread(Runnable {
            var trans = ""
            var orig: String
            try {
                if (Utils.has(update, "reply", "value", "new", "releaseNote")) {
                    orig = update.getJSONObject("reply").getJSONObject("value").getJSONObject("new").getString("releaseNote")
                    orig = orig.replace("<p>".toRegex(), "\n").replace("</p>".toRegex(), "\n")
                    orig = orig.replace("<br>".toRegex(), "\n")
                    orig = orig.replace("<.+?>".toRegex(), "")
                    orig = URLEncoder.encode(orig, "utf-8")

                    val req = Request("http://lang.baidu.com/v2transapi", "Mozilla/5.0 (Windows NT 6.1; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/58.0.3029.110 Safari/537.36")
//                    val req = Request("http://fanyi.baidu.com/#zh/${trTo.toLowerCase()}/$orig", "Mozilla/5.0 (Windows NT 6.1; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/58.0.3029.110 Safari/537.36")
                    req.sendRequest("from=zh&to=${trTo.toLowerCase()}&query=$orig&transtype=translang")
                    val resp = req.recvResponce()
                    val json = JSONObject(resp)
                    Log.i("FlymeFirmwareChecker", json.toString(1))
                    if (Utils.has(json, "trans_result", "data")) {
                        val arr = json.getJSONObject("trans_result").getJSONArray("data")
                        for (i in 0 until arr.length())
                            trans += arr.getJSONObject(i).getString("dst") + "<br><br>"
                        update.getJSONObject("reply").getJSONObject("value").getJSONObject("new").put("releaseNote$trTo", trans)
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }

            onEndRequest?.run()
        })
        th.start()
    }
}
