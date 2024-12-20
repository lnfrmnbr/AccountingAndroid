package com.example.accounting.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.accounting.R
import com.example.accounting.models.Department

class DepartmentsAdapter(
    private val departments: MutableList<Department>,
    private val onEditClick: (Department) -> Unit,
    private val onDeleteClick: (Long) -> Unit,
    private val isAdmin: Boolean
) : RecyclerView.Adapter<DepartmentsAdapter.DepartmentsViewHolder>() {

    inner class DepartmentsViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val depNameTextView: TextView = itemView.findViewById(R.id.departmentNameTextView)

        fun bind(department: Department) {
            depNameTextView.text = department.name

            if (isAdmin){
                itemView.findViewById<Button>(R.id.editDepButton).setOnClickListener {
                    onEditClick(department)
                }
                itemView.findViewById<Button>(R.id.deleteDepButton).setOnClickListener {
                    department.id?.let { it1 -> onDeleteClick(it1) }
                }
            } else {
                itemView.findViewById<Button>(R.id.editDepButton).visibility = View.GONE
                itemView.findViewById<Button>(R.id.deleteDepButton).visibility = View.GONE
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DepartmentsViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_departments, parent, false)
        return DepartmentsViewHolder(view)
    }

    override fun onBindViewHolder(holder: DepartmentsViewHolder, position: Int) {
        holder.bind(departments[position])
    }

    override fun getItemCount(): Int = departments.size

    fun setDepartments(newDepartments: List<Department>) {
        departments.clear()
        departments.addAll(newDepartments)
        notifyDataSetChanged()
    }
}
