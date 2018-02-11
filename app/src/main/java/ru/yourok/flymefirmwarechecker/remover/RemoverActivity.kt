package ru.yourok.flymefirmwarechecker.remover

import android.content.DialogInterface
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.AdapterView
import android.widget.ListView
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_remover.*
import ru.yourok.flymefirmwarechecker.R
import ru.yourok.packagemanager.PackageRemover


class RemoverActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_remover)

        val packageRemover = PackageRemover(this)
        packageRemover.updateList()
        val removerAdaptor = RemoverAdaptor(this, packageRemover)

        val listView = findViewById<ListView>(R.id.listViewApps)
        listView.setAdapter(removerAdaptor)

        editTextFilterRemove.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable) {}
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                removerAdaptor.filter.filter(s)
            }
        })

        listView.onItemClickListener = AdapterView.OnItemClickListener { adapterView, view, i, l ->
            val itm = removerAdaptor.getItem(i) as PackageRemover.AppInfo
            itm.remove = !itm.remove
            removerAdaptor.notifyDataSetChanged()
            packageRemover.saveRemoveList()
        }

        ButtonDelete.setOnClickListener {
            if (packageRemover.getList().filter { it.remove }.isEmpty()) {
                Toast.makeText(this, "Nothing delete", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val warn = "Everything you do, you do at your own peril and risk.\n\n" +
                    "The author is not responsible for any damages that might happen during this.\n\n" +
                    "Все, что вы делаете, вы делаете на свой страх и риск.\n\n" +
                    "Автор не несет ответственности за любые повреждения, которые могут возникнуть во время этого."
            var output = ""
            textViewWarn.visibility = View.VISIBLE
            AlertDialog.Builder(this)
                    .setMessage(warn)
                    .setPositiveButton("I agree/Согласен", object : DialogInterface.OnClickListener {
                        override fun onClick(dialog: DialogInterface, which: Int) {
                            enableButton(false)
                            packageRemover.removeApps(this@RemoverActivity, {
                                runOnUiThread {
                                    output += it + "\n"
                                    textViewWarn.setText(output)
                                }
                            }, {
                                runOnUiThread {
                                    packageRemover.updateList()
                                    removerAdaptor.notifyDataSetChanged()
                                    Toast.makeText(this@RemoverActivity, "Delete finish", Toast.LENGTH_SHORT).show()
                                    enableButton(true)
                                }
                            })
                        }
                    })
                    .setNegativeButton(android.R.string.cancel, null)
                    .show()
        }

        ButtonDefault.setOnClickListener {
            editTextFilterRemove.setText("")
            packageRemover.defaultRemoveList()
            removerAdaptor.notifyDataSetChanged()
        }
    }

    override fun onBackPressed() {
        if (textViewWarn.visibility == View.VISIBLE) {
            textViewWarn.visibility = View.GONE
            return
        }
        super.onBackPressed()
    }

    private fun enableButton(en: Boolean) {
        runOnUiThread { findViewById<View>(R.id.ButtonDelete).isEnabled = en }
    }

}
