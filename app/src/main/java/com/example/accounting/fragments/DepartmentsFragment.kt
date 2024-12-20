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
import com.example.accounting.adapters.DepartmentsAdapter
import com.example.accounting.models.Department
import kotlinx.coroutines.launch

class DepartmentsFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: DepartmentsAdapter
    private val apiService by lazy { RetrofitInstance.api }
    private val departments = mutableListOf<Department>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_departments, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val sharedPreferences = this.activity?.getSharedPreferences("MyPreferences", MODE_PRIVATE)
        val isAdmin = sharedPreferences?.getBoolean("isAdmin", false)

        recyclerView = view.findViewById(R.id.recyclerViewDepartments)
        adapter = DepartmentsAdapter(departments, ::onEditDepartment, ::onDeleteDepartment, isAdmin!!)
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(context)

        fetchDepartments()

        if(isAdmin){
            view.findViewById<Button>(R.id.addDepartment).setOnClickListener {
                showAddDepartmentDialog()
            }
        }
        else{
            view.findViewById<Button>(R.id.addDepartment).visibility = View.GONE
        }
    }

    private fun fetchDepartments() {
        lifecycleScope.launch {
            try {
                val departmentsList = apiService.getDepartments()
                adapter.setDepartments(departmentsList)
            } catch (e: Exception) {
            }
        }
    }

    private fun onEditDepartment(department: Department) {
        showEditDepartmentDialog(department)
    }

    private fun onDeleteDepartment(id: Long) {
        lifecycleScope.launch {
            try {
                apiService.deleteDepartment(id)
                fetchDepartments()
            } catch (e: Exception) {
            }
        }
    }

    private fun showAddDepartmentDialog() {
        val builder = AlertDialog.Builder(requireContext())
        val inflater = layoutInflater
        val dialogLayout = inflater.inflate(R.layout.dialog_departments, null)

        val nameInput = dialogLayout.findViewById<EditText>(R.id.nameDepartmentInput)

        builder.setTitle("Добавить департамент")
            .setPositiveButton("Добавить") { _, _ ->
                val name = nameInput.text.toString()

                val newDepartment = Department(name = name)

                lifecycleScope.launch {
                    try {
                        apiService.addDepartment(newDepartment)
                        fetchDepartments()
                    } catch (e: Exception) {
                    }
                }
            }
            .setNegativeButton("Отмена") { dialog, _ -> dialog.cancel() }

        builder.setView(dialogLayout)
        builder.show()
    }


    private fun showEditDepartmentDialog(department: Department) {
        val builder = AlertDialog.Builder(requireContext())
        val inflater = layoutInflater
        val dialogLayout = inflater.inflate(R.layout.dialog_departments, null)

        val nameInput = dialogLayout.findViewById<EditText>(R.id.nameDepartmentInput)

        nameInput.setText(department.name)

        builder.setTitle("Редактировать сотрудника")
            .setPositiveButton("Сохранить") { _, _ ->
                val name = nameInput.text.toString()

                val updatedDepartment = department.copy(
                    name = name
                )

                lifecycleScope.launch {
                    try {
                        department.id?.let { apiService.updateDepartment(it, updatedDepartment) } // Предполагается, что у вас есть метод обновления
                        fetchDepartments()
                    } catch (e: Exception) {
                    }
                }
            }
            .setNegativeButton("Отмена") { dialog, _ -> dialog.cancel() }

        builder.setView(dialogLayout)
        builder.show()
    }

}
