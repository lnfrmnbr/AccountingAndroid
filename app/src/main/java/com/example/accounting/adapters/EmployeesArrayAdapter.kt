package com.example.accounting.adapters

import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import com.example.accounting.models.Employer

class EmployeesArrayAdapter(context: Context, private val employees: List<Employer>) : ArrayAdapter<Employer>(context, android.R.layout.simple_spinner_item, employees) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view = super.getView(position, convertView, parent)
        view.findViewById<TextView>(android.R.id.text1).text = "${employees[position].firstName}  ${employees[position].fatherName ?: ""} ${employees[position].lastName}"
        return view
    }

    override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view = super.getDropDownView(position, convertView, parent)
        view.findViewById<TextView>(android.R.id.text1).text = "${employees[position].firstName}  ${employees[position].fatherName ?: ""} ${employees[position].lastName}"
        return view
    }
}
