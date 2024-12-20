package com.example.accounting.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.accounting.R
import com.example.accounting.models.DepartmentEmployer

class DepartmentEmployerAdapter(
    private val departmentsEmployer: MutableList<DepartmentEmployer>,
    private val onEditClick: (DepartmentEmployer) -> Unit,
    private val onDeleteClick: (Long) -> Unit,
    private val isAdmin: Boolean
) : RecyclerView.Adapter<DepartmentEmployerAdapter.DepartmentsEmployerViewHolder>() {

    inner class DepartmentsEmployerViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val depNameTextView: TextView = itemView.findViewById(R.id.departmentNameDETextView)
        private val firstNameTextView: TextView = itemView.findViewById(R.id.firstNameDETextView)
        private val fatherNameTextView: TextView = itemView.findViewById(R.id.fatherNameDETextView)
        private val lastNameTextView: TextView = itemView.findViewById(R.id.lastNameDETextView)


        fun bind(departmentEmployer: DepartmentEmployer) {
            depNameTextView.text = departmentEmployer.department.name
            firstNameTextView.text = "${departmentEmployer.employer.firstName} "
            lastNameTextView.text = "${departmentEmployer.employer.lastName} "
            if(departmentEmployer.employer.fatherName != null){
                fatherNameTextView.text = "${departmentEmployer.employer.fatherName} "
            }

            if(isAdmin){
                itemView.findViewById<Button>(R.id.editDepButton).setOnClickListener {
                    onEditClick(departmentEmployer)
                }
                itemView.findViewById<Button>(R.id.deleteDepButton).setOnClickListener {
                    departmentEmployer.id?.let { it1 -> onDeleteClick(it1) }
                }
            }
            else{
                itemView.findViewById<Button>(R.id.editDepButton).visibility = View.GONE
                itemView.findViewById<Button>(R.id.deleteDepButton).visibility = View.GONE
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DepartmentsEmployerViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_department_employer, parent, false)
        return DepartmentsEmployerViewHolder(view)
    }

    override fun onBindViewHolder(holder: DepartmentsEmployerViewHolder, position: Int) {
        holder.bind(departmentsEmployer[position])
    }

    override fun getItemCount(): Int = departmentsEmployer.size

    fun setDepartmentsEmployer(newDepartmentsEmpl: List<DepartmentEmployer>) {
        departmentsEmployer.clear()
        departmentsEmployer.addAll(newDepartmentsEmpl)
        notifyDataSetChanged()
    }
}
