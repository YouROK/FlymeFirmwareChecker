package ru.yourok.flymefirmwarechecker

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.widget.*
import org.json.JSONObject
import ru.yourok.flymefirmwarechecker.dialogs.DateTimeDialog
import ru.yourok.flymefirmwarechecker.dialogs.ItemsDialog
import ru.yourok.utils.Params
import ru.yourok.utils.RequestManager
import ru.yourok.utils.SystemParams
import ru.yourok.utils.SystemParams.*
import ru.yourok.utils.Utils

class MainActivity : AppCompatActivity() {

    private var params: JSONObject? = null
    private var update: JSONObject? = null
    private var manager: RequestManager? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        (findViewById(R.id.checkBoxInternational) as CheckBox).setOnClickListener { setEditText(R.id.editTextHost, SystemParams.getHost((findViewById(R.id.checkBoxInternational) as CheckBox).isChecked)) }

        (findViewById(R.id.editTextFirmType) as EditText).setOnLongClickListener {
            val itemsDialog = ItemsDialog(this@MainActivity, R.array.firmware_type)
            itemsDialog.show {
                val item = itemsDialog.item
                if (item != null)
                    setEditText(R.id.editTextFirmType, item)
            }
            true
        }

        val cl = View.OnLongClickListener { v ->
            val itemsDialog = ItemsDialog(this@MainActivity, R.array.android_versions)
            itemsDialog.show {
                val item = itemsDialog.item
                if (item != null)
                    (v as EditText).setText(item)
            }
            true
        }

        (findViewById(R.id.editTextVersion) as EditText).setOnLongClickListener(cl)
        (findViewById(R.id.editTextFirmware) as EditText).setOnLongClickListener(cl)

        (findViewById(R.id.editTextTimestamp) as EditText).setOnLongClickListener {
            var timestamp: Long = -1
            if (!getEditText(R.id.editTextTimestamp).isEmpty())
                timestamp = java.lang.Long.parseLong(getEditText(R.id.editTextTimestamp))
            val dialog = DateTimeDialog(this@MainActivity, timestamp)
            dialog.show().onConfirm { setEditText(R.id.editTextTimestamp, dialog.timestamp.toString()) }
            true
        }

        (findViewById(R.id.editTextDeviceType) as EditText).setOnLongClickListener {
            val itemsDialog = ItemsDialog(this@MainActivity, R.array.devices_id)
            itemsDialog.show {
                var item = itemsDialog.item
                val tmp = item.split("-".toRegex()).dropLastWhile({ it.isEmpty() }).toTypedArray()
                if (tmp.size == 2)
                    item = tmp[0]
                setEditText(R.id.editTextDeviceType, item)
            }
            true
        }
        (findViewById(R.id.textViewUpdate) as TextView).setOnClickListener {
            try {
                val jnew = update!!.getJSONObject("reply").getJSONObject("value").getJSONObject("new")
                val link = jnew.getString("updateUrl")
                if (!link.isNullOrEmpty()) {
                    val sendIntent = Intent(Intent.ACTION_VIEW, Uri.parse(link))
                    sendIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    val chooser = Intent.createChooser(sendIntent, "")
                    chooser.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    startActivity(chooser)
                }
            } catch (e: Exception) {

            }
        }
    }

    private fun ShowMsg(msg: String) {
        runOnUiThread { Toast.makeText(this@MainActivity, msg, Toast.LENGTH_SHORT).show() }
    }

    override fun onStart() {
        try {
            val p = Params(this)
            params = p.params
            if (!p.update.isEmpty())
                update = JSONObject(p.update)
            if (params == null || params!!.getString(SN).isEmpty() && params!!.getString(IMEI).isEmpty())
                params = SystemParams(this).systemParams
            writeToUIParams(params!!)
        } catch (e: Exception) {
            e.printStackTrace()
            e.message?.let {
                ShowMsg(it)
            }
        }

        super.onStart()
    }

    override fun onStop() {
        try {
            params = readFromUIParams()
            val p = Params(this)
            p.params = params
            if (update != null)
                p.update = update!!.toString()
        } catch (e: Exception) {
            e.printStackTrace()
            e.message?.let {
                ShowMsg(it)
            }
        }

        super.onStop()
    }

    private fun getEditText(id: Int): String {
        return if (id == R.id.checkBoxRoot) {
            if ((findViewById(id) as CheckBox).isChecked) "1" else "0"
        } else (findViewById(id) as EditText).text.toString()

    }

    private fun setEditText(id: Int, `val`: String?) {
        runOnUiThread {
            if (id == R.id.checkBoxRoot)
                (findViewById(id) as CheckBox).isChecked = `val` == "1"
            else
                (findViewById(id) as EditText).setText(`val`)
        }
    }

    private fun readFromUIParams(): JSONObject {
        val andVer = getEditText(R.id.editTextVersion)
        val timestamp = getEditText(R.id.editTextTimestamp)
        val firmtype = getEditText(R.id.editTextFirmType)
        val maskid = andVer + "-" + timestamp + "_" + firmtype

        val basicParams = JSONObject()
        basicParams.put(DEVICE_TYPE, getEditText(R.id.editTextDeviceType))
        basicParams.put(FIRMWARE, getEditText(R.id.editTextFirmware))
        basicParams.put(ROOT, getEditText(R.id.checkBoxRoot))
        basicParams.put(SYSVER, maskid)
        basicParams.put(VERSION, maskid)
        basicParams.put(DEVICE_ID, getEditText(R.id.editTextImei))
        basicParams.put(SN, getEditText(R.id.editTextSN))
        basicParams.put(IMEI, getEditText(R.id.editTextImei))
        return basicParams
    }

    private fun writeToUIParams(params: JSONObject) {
        val maskid = params.getString(SYSVER)
        val prop = maskid.split("-|_".toRegex()).dropLastWhile({ it.isEmpty() }).toTypedArray()

        setEditText(R.id.editTextDeviceType, params.getString(DEVICE_TYPE))
        setEditText(R.id.editTextFirmware, params.getString(FIRMWARE))
        if (prop.size > 2) {
            setEditText(R.id.editTextVersion, prop[0])
            setEditText(R.id.editTextTimestamp, prop[1])
            setEditText(R.id.editTextFirmType, prop[2])
        }
        setEditText(R.id.checkBoxRoot, params.getString(ROOT))
        setEditText(R.id.editTextImei, params.getString(DEVICE_ID))
        setEditText(R.id.editTextSN, params.getString(SN))
        UpdateView()
    }

    fun onBtnRead(view: View) {
        try {
            params = SystemParams(this).systemParams
            writeToUIParams(params!!)
        } catch (e: Exception) {
            e.printStackTrace()
            e.message?.let {
                ShowMsg(it)
            }
        }

    }

    fun onBtnCheck(view: View) {
        try {
            (findViewById(R.id.textViewUpdate) as TextView).text = "Requesting..."
            params = readFromUIParams()
            if (params == null) {
                ShowMsg("Set all params")
                return
            }
            val manager = RequestManager(getEditText(R.id.editTextHost), params!!)
            manager.check(Runnable {
                val stat = manager.stat
                update = manager.update
                runOnUiThread {
                    if (stat.isEmpty()) {
                        ShowMsg("Ok")
                        UpdateView()
                    } else
                        (findViewById(R.id.textViewUpdate) as TextView).text = stat
                }
            })
        } catch (e: Exception) {
            e.printStackTrace()
            e.message?.let {
                ShowMsg(it)
            }
        }

    }

    fun onBtnFind(view: View) {
        try {
            if (manager != null && manager!!.isFinding) {
                manager!!.stop()
                (findViewById(R.id.buttonFind) as Button).text = "Find"
                return
            }

            (findViewById(R.id.textViewUpdate) as TextView).text = "Requesting..."
            params = readFromUIParams()
            if (params == null) {
                ShowMsg("Set all params")
                return
            }
            val rm = RequestManager(getEditText(R.id.editTextHost), params!!)
            manager = rm
            rm.find(Runnable {
                val stat = rm.stat
                update = rm.update
                runOnUiThread {
                    if (stat.isEmpty()) {
                        ShowMsg("Ok")
                        UpdateView()
                    } else
                        (findViewById(R.id.textViewUpdate) as TextView).text = stat
                    (findViewById(R.id.buttonFind) as Button).text = "Find"
                }
            }, Runnable {
                val timestamp = rm.timestamp
                if (timestamp > -1) {
                    setEditText(R.id.editTextTimestamp, timestamp.toString())
                }
            })

            if (manager!!.isFinding)
                (findViewById(R.id.buttonFind) as Button).text = "Stop"
            else
                (findViewById(R.id.buttonFind) as Button).text = "Find"

        } catch (e: Exception) {
            e.printStackTrace()
            e.message?.let {
                ShowMsg(it)
            }
        }

    }

    private fun UpdateView() {
        var upd = ""
        try {
            if (update != null) {
                if (Utils.has(update, "reply", "value", "new")) {
                    val jnew = update!!.getJSONObject("reply").getJSONObject("value").getJSONObject("new")
                    upd = jnew.getString("systemVersion") + " " + jnew.getString("fileSize") + " " + jnew.getString("releaseDate")
                } else if (update!!.has("code")) {
                    upd = update!!.getString("message")
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        (findViewById(R.id.textViewUpdate) as TextView).text = upd
    }

    fun onBtnView(view: View) {
        if (update == null || !Utils.has(update, "reply", "value", "new")) {
            ShowMsg("Nothing view")
            return
        }
        val intent = Intent(this, UpdateViewActivity::class.java)
        intent.putExtra("Update", update!!.toString())
        startActivity(intent)
    }

    fun onBtnEditor(view: View) {
        val intent = Intent(this, EditorActivity::class.java)
        startActivity(intent)
    }
}
