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
import android.widget.LinearLayout
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.accounting.R
import com.example.accounting.RetrofitInstance
import com.example.accounting.adapters.DepartmentsArrayAdapter
import com.example.accounting.adapters.ProjectsAdapter
import com.example.accounting.models.Department
import com.example.accounting.models.Project
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Locale

class ProjectsFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: ProjectsAdapter
    private val apiService by lazy { RetrofitInstance.api }
    private val projects = mutableListOf<Project>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_projects, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val sharedPreferences = this.activity?.getSharedPreferences("MyPreferences", MODE_PRIVATE)
        val isAdmin = sharedPreferences?.getBoolean("isAdmin", false)

        recyclerView = view.findViewById(R.id.recyclerViewProjects)
        adapter = ProjectsAdapter( projects, ::onEditProject, ::onDeleteProject, ::onCalcProject, isAdmin!!)
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(context)

        fetchProjects()

        if (isAdmin){
            view.findViewById<Button>(R.id.addProject).setOnClickListener {
                showAddProjectDialog()
            }
        }
        else {
            view.findViewById<Button>(R.id.addProject).visibility = View.GONE
        }


        view.findViewById<Button>(R.id.totalProfitButton).setOnClickListener {
            calculateTotalProfit(view)
        }
    }

    private fun fetchProjects() {
        lifecycleScope.launch {
            try {
                val projects = apiService.getProjects()
                adapter.setProjects(projects)
            } catch (e: Exception) {
            }
        }
    }

    private fun calculateTotalProfit(view: View) {
        lifecycleScope.launch {
            try {
                val profit = apiService.getTotalProfit()
                val totalProfitText = view.findViewById<TextView>(R.id.totalProfitText)
                totalProfitText.text = "${totalProfitText.text} ${profit}руб."
            } catch (e: Exception) {
            }
        }
    }

    private fun onEditProject(project: Project) {
        showEditProjectDialog(project)
    }

    private fun onCalcProject(id: Long, view: View) {
        val profitLayout = view.findViewById<LinearLayout>(R.id.profitLayout)
        profitLayout!!.visibility = View.VISIBLE
        val profit = view.findViewById<TextView>(R.id.projectProfit)
        lifecycleScope.launch {
            try {
                profit!!.text = apiService.getProfit(id).toString()
            } catch (e: Exception) {
            }
        }
    }

    private fun onDeleteProject(id: Long) {
        lifecycleScope.launch {
            try {
                apiService.deleteProject(id)
                fetchProjects()
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

    private fun setupDepartmentSpinnerForEdit(spinner: Spinner, departmentName: String) {
        lifecycleScope.launch {
            val departments = apiService.getDepartments()
            val adapter = DepartmentsArrayAdapter(requireContext(), departments)
            spinner.adapter = adapter
            spinner.setSelection(departments.indexOfFirst { it.name == departmentName })
        }
    }

    private fun isValidDateFormat(date: String): Boolean {
        val format = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        if (date == ""){
            return true
        }
        return try {
            format.parse(date)
            true
        } catch (e: Exception) {
            false
        }
    }

    private fun showAddProjectDialog() {
        val builder = AlertDialog.Builder(requireContext())
        val inflater = layoutInflater
        val dialogLayout = inflater.inflate(R.layout.dialog_projects, null)

        val spinnerDepartments = dialogLayout.findViewById<Spinner>(R.id.spinnerDepartments)
        setupDepartmentSpinner(spinnerDepartments)

        val nameInput = dialogLayout.findViewById<EditText>(R.id.nameProjectInput)
        val costInput = dialogLayout.findViewById<EditText>(R.id.costProjectInput)
        val dateBegInput = dialogLayout.findViewById<EditText>(R.id.dateBegProjectInput)
        val dateEndInput = dialogLayout.findViewById<EditText>(R.id.dateEndProjectInput)
        val dateEndRealInput = dialogLayout.findViewById<EditText>(R.id.dateEndRealProjectInput)

        builder.setTitle("Добавить проект")
            .setPositiveButton("Добавить") { _, _ ->
                val department = spinnerDepartments.selectedItem as Department
                val name = nameInput.text.toString()
                val cost_ = costInput.text.toString()
                val dateBeg = dateBegInput.text.toString()
                val dateEnd = dateEndInput.text.toString()
                val dateEndReal = dateEndRealInput.text.toString()

                var cost = 0
                if (cost_ != ""){
                    cost = cost_.toInt()
                }

                if (isValidDateFormat(dateBeg) && isValidDateFormat(dateEnd) && isValidDateFormat(dateEndReal)){
                    val project = Project (
                        name = name,
                        cost = cost,
                        department = department,
                        dateBeg = dateBeg,
                        dateEnd = dateEnd,
                        dateEndReal = dateEndReal
                    )
                    lifecycleScope.launch {
                        try {
                            apiService.addProject(project)
                            fetchProjects()
                        } catch (e: Exception) {
                        }
                    }
                }
                else{
                    Toast.makeText(context, "Дата и время должны быть в формате yyyy-MM-dd HH:mm:ss", Toast.LENGTH_LONG).show()
                }
            }
            .setNegativeButton("Отмена") { dialog, _ -> dialog.cancel() }

        builder.setView(dialogLayout)
        builder.show()
    }

    private fun showEditProjectDialog(project: Project) {
        val builder = AlertDialog.Builder(requireContext())
        val inflater = layoutInflater
        val dialogLayout = inflater.inflate(R.layout.dialog_projects, null)

        val spinnerDepartments = dialogLayout.findViewById<Spinner>(R.id.spinnerDepartments)
        setupDepartmentSpinnerForEdit(spinnerDepartments, project.name)

        val nameInput = dialogLayout.findViewById<EditText>(R.id.nameProjectInput)
        val costInput = dialogLayout.findViewById<EditText>(R.id.costProjectInput)
        val dateBegInput = dialogLayout.findViewById<EditText>(R.id.dateBegProjectInput)
        val dateEndInput = dialogLayout.findViewById<EditText>(R.id.dateEndProjectInput)
        val dateEndRealInput = dialogLayout.findViewById<EditText>(R.id.dateEndRealProjectInput)

        nameInput.setText(project.name)
        costInput.setText(project.cost.toString())
        dateBegInput.setText(project.dateBeg)
        dateEndInput.setText(project.dateEnd)
        dateEndRealInput.setText(project.dateEndReal)

        builder.setTitle("Изменить проект")
            .setPositiveButton("Изменить") { _, _ ->
                val department = spinnerDepartments.selectedItem as Department
                val name = nameInput.text.toString()
                val cost = costInput.text.toString().toInt()
                val dateBeg = dateBegInput.text.toString()
                val dateEnd = dateEndInput.text.toString()
                val dateEndReal = dateEndRealInput.text.toString()

                val updatedProject = Project (
                    name = name,
                    cost = cost,
                    department = department,
                    dateBeg = dateBeg,
                    dateEnd = dateEnd,
                    dateEndReal = dateEndReal
                )
                lifecycleScope.launch {
                    try {
                        project.id?.let { apiService.updateProject(it, updatedProject) }
                        fetchProjects()
                    } catch (e: Exception) {
                    }
                }
            }
            .setNegativeButton("Отмена") { dialog, _ -> dialog.cancel() }

        builder.setView(dialogLayout)
        builder.show()
    }

}
