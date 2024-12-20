package com.example.accounting.adapters

import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import com.example.accounting.models.Department

class DepartmentsArrayAdapter(context: Context, private val departments: List<Department>) : ArrayAdapter<Department>(context, android.R.layout.simple_spinner_item, departments) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view = super.getView(position, convertView, parent)
        view.findViewById<TextView>(android.R.id.text1).text = departments[position].name
        return view
    }

    override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view = super.getDropDownView(position, convertView, parent)
        view.findViewById<TextView>(android.R.id.text1).text = departments[position].name
        return view
    }
}
