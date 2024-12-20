package com.example.accounting.fragments

import android.app.AlertDialog
import android.content.Context.MODE_PRIVATE
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.accounting.R
import com.example.accounting.RetrofitInstance
import com.example.accounting.adapters.EmployeesAdapter
import com.example.accounting.models.Employer
import kotlinx.coroutines.launch

class EmployeesFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: EmployeesAdapter
    private val apiService by lazy { RetrofitInstance.api }
    private val employees = mutableListOf<Employer>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_employees, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val sharedPreferences = this.activity?.getSharedPreferences("MyPreferences", MODE_PRIVATE)
        val isAdmin = sharedPreferences?.getBoolean("isAdmin", false)

        recyclerView = view.findViewById(R.id.recyclerView)
        adapter = EmployeesAdapter(employees, ::onEditEmployee, ::onDeleteEmployee, isAdmin!!)
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(context)

        fetchEmployees()

        if (isAdmin){
            view.findViewById<Button>(R.id.addEmployer).setOnClickListener {
                showAddEmployeeDialog()
            }
        }else{
            view.findViewById<Button>(R.id.addEmployer).visibility = View.GONE
        }

    }

    private fun fetchEmployees() {
        lifecycleScope.launch {
            try {
                val employeeList = apiService.getEmployees()
                adapter.setEmployees(employeeList)
            } catch (e: Exception) {
            }
        }
    }

    private fun onEditEmployee(employer: Employer) {
        showEditEmployeeDialog(employer)
    }

    private fun onDeleteEmployee(id: Long) {
        lifecycleScope.launch {
            try {
                apiService.deleteEmployer(id)
                fetchEmployees()
            } catch (e: Exception) {
            }
        }
    }

    private fun showAddEmployeeDialog() {
        val builder = AlertDialog.Builder(requireContext())
        val inflater = layoutInflater
        val dialogLayout = inflater.inflate(R.layout.dialog_employee, null)

        val nameInput = dialogLayout.findViewById<EditText>(R.id.nameInput)
        val fatherNameInput = dialogLayout.findViewById<EditText>(R.id.fatherNameTextInput)
        val lastNameInput = dialogLayout.findViewById<EditText>(R.id.lastNameTextInput)
        val positionInput = dialogLayout.findViewById<EditText>(R.id.positionInput)
        val salaryInput = dialogLayout.findViewById<EditText>(R.id.salaryInput)

        builder.setTitle("Добавить сотрудника")
            .setPositiveButton("Добавить") { _, _ ->
                val name = nameInput.text.toString()
                val fatherName = fatherNameInput.text.toString()
                val lastName = lastNameInput.text.toString()
                val position = positionInput.text.toString()
                var salary = 0
                if (salaryInput.text.toString() != ""){
                    salary = salaryInput.text.toString().toInt()
                }

                val newEmployer = Employer(firstName = name, fatherName = fatherName, lastName = lastName, position =  position, salary = salary)

                lifecycleScope.launch {
                    try {
                        apiService.addEmployer(newEmployer)
                        fetchEmployees()
                    } catch (e: Exception) {
                    }
                }
            }
            .setNegativeButton("Отмена") { dialog, _ -> dialog.cancel() }

        builder.setView(dialogLayout)
        builder.show()
    }


    private fun showEditEmployeeDialog(employer: Employer) {
        val builder = AlertDialog.Builder(requireContext())
        val inflater = layoutInflater
        val dialogLayout = inflater.inflate(R.layout.dialog_employee, null)

        val nameInput = dialogLayout.findViewById<EditText>(R.id.nameInput)
        val fatherNameInput = dialogLayout.findViewById<EditText>(R.id.fatherNameTextInput)
        val lastNameInput = dialogLayout.findViewById<EditText>(R.id.lastNameTextInput)
        val positionInput = dialogLayout.findViewById<EditText>(R.id.positionInput)
        val salaryInput = dialogLayout.findViewById<EditText>(R.id.salaryInput)

        nameInput.setText(employer.firstName)
        fatherNameInput.setText(employer.fatherName)
        lastNameInput.setText(employer.lastName)
        positionInput.setText(employer.position)
        salaryInput.setText(employer.salary.toString())

        builder.setTitle("Редактировать сотрудника")
            .setPositiveButton("Сохранить") { _, _ ->
                val name = nameInput.text.toString()
                val fatherName = fatherNameInput.text.toString()
                val lastName = lastNameInput.text.toString()
                val position = positionInput.text.toString()
                val salary = salaryInput.text.toString().toInt()

                val updatedEmployer = employer.copy(
                    firstName = name,
                    fatherName = fatherName,
                    lastName = lastName,
                    position = position,
                    salary = salary
                )

                lifecycleScope.launch {
                    try {
                        employer.id?.let { apiService.updateEmployees(it, updatedEmployer) } // Предполагается, что у вас есть метод обновления
                        fetchEmployees()
                    } catch (e: Exception) {
                    }
                }
            }
            .setNegativeButton("Отмена") { dialog, _ -> dialog.cancel() }

        builder.setView(dialogLayout)
        builder.show()
    }

}
