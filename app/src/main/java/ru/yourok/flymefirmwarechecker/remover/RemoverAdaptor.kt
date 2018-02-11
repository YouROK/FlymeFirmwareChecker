package ru.yourok.flymefirmwarechecker.remover

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import ru.yourok.flymefirmwarechecker.R
import ru.yourok.packagemanager.PackageRemover


/**
 * Created by yourok on 08.02.18.
 */

class RemoverAdaptor(val context: Context, val appsList: PackageRemover) : BaseAdapter(), Filterable {

    var listApps = appsList.getList()

    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(constraint: CharSequence?): FilterResults {
                val result = FilterResults()
                if (constraint != null && constraint.isNotEmpty()) {
                    val allApps = appsList.getList().filter {
                        it.appInfo.packageName.contains(constraint, true) || it.remove
                    }
                    result.values = allApps
                    result.count = allApps.size
                } else {
                    result.values = appsList.getList()
                    result.count = appsList.size()
                }
                return result
            }

            override fun publishResults(constraint: CharSequence, results: FilterResults) {
                if (results.count == 0) {
                    notifyDataSetChanged()
                } else {
                    listApps = results.values as ArrayList<PackageRemover.AppInfo>
                    notifyDataSetChanged()
                }
            }
        }
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view: View = convertView
                ?: (context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater).inflate(R.layout.adaptor_remover_item, null)

        val appInfo = listApps[position]
        val pkg = appInfo.appInfo.packageName
        val name = appInfo.nameApp
        val dir = appInfo.appInfo.sourceDir
        val icon = appInfo.icon

        view.findViewById<ImageView>(R.id.imageViewAppIcon).setImageDrawable(icon)
        view.findViewById<TextView>(R.id.textViewTitle).text = name
        view.findViewById<TextView>(R.id.textViewStat).text = pkg + "\n" + dir
        view.findViewById<CheckBox>(R.id.checkBoxRemove).isChecked = appInfo.remove
        view.findViewById<CheckBox>(R.id.checkBoxRemove).setOnClickListener {
            val checked = view.findViewById<CheckBox>(R.id.checkBoxRemove).isChecked
            appInfo.remove = checked
            appsList.saveRemoveList()
        }

        return view
    }

    override fun getItem(pos: Int): Any? {
        return listApps[pos]
    }

    override fun getItemId(pos: Int): Long {
        return pos.toLong()
    }

    override fun getCount(): Int {
        return listApps.size
    }

}