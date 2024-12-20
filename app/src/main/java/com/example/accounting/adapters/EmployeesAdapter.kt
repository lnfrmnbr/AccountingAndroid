package com.example.accounting.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.accounting.R
import com.example.accounting.models.Employer

class EmployeesAdapter(
    private val employees: MutableList<Employer>,
    private val onEditClick: (Employer) -> Unit,
    private val onDeleteClick: (Long) -> Unit,
    private val isAdmin: Boolean
) : RecyclerView.Adapter<EmployeesAdapter.EmployeeViewHolder>() {

    inner class EmployeeViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val firstNameTextView: TextView = itemView.findViewById(R.id.firstNameTextView)
        private val fatherNameTextView: TextView = itemView.findViewById(R.id.fatherNameTextView)
        private val lastNameTextView: TextView = itemView.findViewById(R.id.lastNameTextView)
        private val positionTextView: TextView = itemView.findViewById(R.id.positionTextView)
        private val salaryTextView: TextView = itemView.findViewById(R.id.salaryTextView)

        fun bind(employer: Employer) {
            firstNameTextView.text = "${employer.firstName} "
            lastNameTextView.text = "${employer.lastName} "
            positionTextView.text = employer.position
            salaryTextView.text = employer.salary.toString()

            if(employer.fatherName != null){
                fatherNameTextView.text = "${employer.fatherName} "
            }

            if(isAdmin){
                itemView.findViewById<Button>(R.id.editButton).setOnClickListener {
                    onEditClick(employer)
                }
                itemView.findViewById<Button>(R.id.deleteButton).setOnClickListener {
                    employer.id?.let { it1 -> onDeleteClick(it1) }
                }
            }else{
                itemView.findViewById<Button>(R.id.editButton).visibility = View.GONE
                itemView.findViewById<Button>(R.id.deleteButton).visibility = View.GONE
            }

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EmployeeViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_employees, parent, false)
        return EmployeeViewHolder(view)
    }

    override fun onBindViewHolder(holder: EmployeeViewHolder, position: Int) {
        holder.bind(employees[position])
    }

    override fun getItemCount(): Int = employees.size

    fun setEmployees(newEmployees: List<Employer>) {
        employees.clear()
        employees.addAll(newEmployees)
        notifyDataSetChanged()
    }
}
