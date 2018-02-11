package ru.yourok.flymefirmwarechecker

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.webkit.WebView
import android.widget.*
import android.widget.AdapterView.OnItemSelectedListener
import org.json.JSONObject
import ru.yourok.utils.Params
import ru.yourok.utils.Utils

class UpdateViewActivity : AppCompatActivity() {

    var update: JSONObject? = null
    var lang = "orig"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_update_view)

        if (intent == null) {
            finish()
            return
        }
        update = JSONObject(intent.getStringExtra("Update"))

        val spinner = findViewById(R.id.spinnerTranslate) as Spinner
        val adapter = ArrayAdapter.createFromResource(this, R.array.translates, android.R.layout.simple_spinner_item)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner.adapter = adapter

        spinner.onItemSelectedListener = object : OnItemSelectedListener {
            override fun onNothingSelected(p0: AdapterView<*>?) {
            }

            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, position: Int, p3: Long) {
                lang = spinner.selectedItem.toString()
                if (lang != "Orig") {
                    if (Utils.has(update, "reply", "value", "new", "releaseNote$lang"))
                        update()
                    else {
                        showProgress(true)
                        Utils.translate(lang, update!!, Runnable {
                            update()
                            showProgress(false)
                            Params(this@UpdateViewActivity).update = update.toString()
                        })
                    }
                } else
                    update()
            }
        }

        update()
    }

    fun translate(lang: String) {
        showProgress(true)
        Utils.translate(lang, update!!, Runnable {
            update()
            showProgress(false)
            Params(this@UpdateViewActivity).update = update.toString()
        })
    }

    fun showProgress(isShow: Boolean) {
        runOnUiThread {
            if (isShow)
                findViewById<View>(R.id.progress).visibility = View.VISIBLE
            else
                findViewById<View>(R.id.progress).visibility = View.GONE
            findViewById<View>(R.id.spinnerTranslate).isEnabled = !isShow
        }
    }

    fun update() {
        try {
            var releaseNotes = "<h4>Firmware</h4>"
            val jnew = update!!.getJSONObject("reply").getJSONObject("value").getJSONObject("new")
            releaseNotes += "<p>" + jnew.getString("systemVersion") +
                    "<br>" + jnew.getString("fileSize") +
                    "<br>" + jnew.getString("releaseDate") + "</p>" +
                    "<h4>" + jnew.getString("updateUrl") + "</h4>"

            if (lang != "Orig" && jnew.has("releaseNote$lang"))
                releaseNotes += "<br>" + jnew.getString("releaseNote$lang")
            else
                releaseNotes += "<br>" + jnew.getString("releaseNote")

            runOnUiThread {
                (findViewById(R.id.textViewUrl) as TextView).text = jnew.getString("updateUrl")
                (findViewById(R.id.webView) as WebView).loadDataWithBaseURL(null, releaseNotes, "text/html", "utf-8", null)
            }

        } catch (e: Exception) {
            e.printStackTrace()
            e.message?.let {
                runOnUiThread { Toast.makeText(this, it, Toast.LENGTH_SHORT).show() }
            }
        }
    }
}
