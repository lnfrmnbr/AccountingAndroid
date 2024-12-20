package com.example.accounting.fragments

import android.app.AlertDialog
import android.content.Context.MODE_PRIVATE
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.accounting.R
import com.example.accounting.RetrofitInstance
import com.example.accounting.adapters.DepartmentEmployerAdapter
import com.example.accounting.adapters.DepartmentsArrayAdapter
import com.example.accounting.adapters.EmployeesArrayAdapter
import com.example.accounting.models.Department
import com.example.accounting.models.DepartmentEmployer
import com.example.accounting.models.Employer
import kotlinx.coroutines.launch

class DepartmentEmployerFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: DepartmentEmployerAdapter
    private val apiService by lazy { RetrofitInstance.api }
    private val departmentEmployer = mutableListOf<DepartmentEmployer>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_department_employer, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val sharedPreferences = this.activity?.getSharedPreferences("MyPreferences", MODE_PRIVATE)
        val isAdmin = sharedPreferences?.getBoolean("isAdmin", false)

        recyclerView = view.findViewById(R.id.recyclerViewDE)
        adapter = DepartmentEmployerAdapter( departmentEmployer, ::onEditDepartmentEmployer, ::onDeleteDepartmentEmployer, isAdmin!!)
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(context)

        fetchDepartmentEmployer()

        if (isAdmin){
            view.findViewById<Button>(R.id.addDepartmentEmpl).setOnClickListener {
                showAddDepartmentEmployerDialog()
            }
        } else {
            view.findViewById<Button>(R.id.addDepartmentEmpl).visibility = View.GONE
        }
    }

    private fun fetchDepartmentEmployer() {
        lifecycleScope.launch {
            try {
                val depEmployeeList = apiService.getDepartmentsEmpl()
                adapter.setDepartmentsEmployer(depEmployeeList)
            } catch (e: Exception) {
            }
        }
    }

    private fun onEditDepartmentEmployer(depEmployer: DepartmentEmployer) {
        showEditDepartmentEmployeeDialog(depEmployer)
    }

    private fun onDeleteDepartmentEmployer(id: Long) {
        lifecycleScope.launch {
            try {
                apiService.deleteDepartmentEmpl(id)
                fetchDepartmentEmployer()
            } catch (e: Exception) {
            }
        }
    }

    private fun setupDepartmentSpinner(spinner: Spinner) {
        lifecycleScope.launch {
            val departments = apiService.getDepartments()
            val adapter = DepartmentsArrayAdapter(requireContext(), departments)
            spinner.adapter = adapter
        }
    }


    private fun setupEmployeesSpinner(spinner: Spinner) {
        lifecycleScope.launch {
            val employees = apiService.getEmployees()
            val adapter = EmployeesArrayAdapter(requireContext(), employees)
            spinner.adapter = adapter
        }
    }

    private fun setupDepartmentSpinnerForEdit(spinner: Spinner, departmentName: String) {
        lifecycleScope.launch {
            val departments = apiService.getDepartments()
            val adapter = DepartmentsArrayAdapter(requireContext(), departments)
            spinner.adapter = adapter
            spinner.setSelection(departments.indexOfFirst { it.name == departmentName })
        }
    }

    private fun setupEmployerSpinnerForEdit(spinner: Spinner, employer: Employer) {
        lifecycleScope.launch {
            val employees = apiService.getEmployees()
            val adapter = EmployeesArrayAdapter(requireContext(), employees)
            spinner.adapter = adapter
            spinner.setSelection(employees.indexOfFirst { it.firstName == employer.firstName && it.fatherName == employer.fatherName && it.lastName == employer.lastName })
        }
    }

    private fun showAddDepartmentEmployerDialog() {
        val builder = AlertDialog.Builder(requireContext())
        val inflater = layoutInflater
        val dialogLayout = inflater.inflate(R.layout.dialog_department_employees, null)

        val spinnerDepartments = dialogLayout.findViewById<Spinner>(R.id.spinnerDepartments)
        setupDepartmentSpinner(spinnerDepartments)

        val spinnerEmployees = dialogLayout.findViewById<Spinner>(R.id.spinnerEmployees)
        setupEmployeesSpinner(spinnerEmployees)

        builder.setTitle("Добавить связь")
            .setPositiveButton("Добавить") { _, _ ->
                val department = spinnerDepartments.selectedItem as Department
                val employees = spinnerEmployees.selectedItem as Employer

                val departmentEmployer = DepartmentEmployer (
                    department = department,
                    employer = employees
                )

                lifecycleScope.launch {
                    try {
                        apiService.addDepartmentEmpl(departmentEmployer)
                        fetchDepartmentEmployer()
                    } catch (e: Exception) {
                    }
                }
            }
            .setNegativeButton("Отмена") { dialog, _ -> dialog.cancel() }

        builder.setView(dialogLayout)
        builder.show()
    }

    private fun showEditDepartmentEmployeeDialog(depEmployer: DepartmentEmployer) {
        val builder = AlertDialog.Builder(requireContext())
        val inflater = layoutInflater
        val dialogLayout = inflater.inflate(R.layout.dialog_department_employees, null)

        val spinnerDepartments = dialogLayout.findViewById<Spinner>(R.id.spinnerDepartments)
        setupDepartmentSpinnerForEdit(spinnerDepartments, depEmployer.department.name)

        val spinnerEmployees = dialogLayout.findViewById<Spinner>(R.id.spinnerEmployees)
        setupEmployerSpinnerForEdit(spinnerEmployees, depEmployer.employer)

        builder.setTitle("Редактировать связь")
            .setPositiveButton("Добавить") { _, _ ->
                val department = spinnerDepartments.selectedItem as Department
                val employees = spinnerEmployees.selectedItem as Employer

                val departmentEmployer = DepartmentEmployer (
                    department = department,
                    employer = employees
                )

                lifecycleScope.launch {
                    try {
                        depEmployer.id?.let { apiService.updateDepartmentEmpl(it, departmentEmployer) }
                        fetchDepartmentEmployer()
                    } catch (e: Exception) {
                    }
                }
            }
            .setNegativeButton("Отмена") { dialog, _ -> dialog.cancel() }

        builder.setView(dialogLayout)
        builder.show()
    }

}
