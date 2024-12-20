package com.example.accounting.adapters

import android.text.format.Time
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.accounting.R
import com.example.accounting.models.Project
import java.sql.Date

class ProjectsAdapter(
    private val projects: MutableList<Project>,
    private val onEditClick: (Project) -> Unit,
    private val onDeleteClick: (Long) -> Unit,
    private val onCalcClick: (Long, View) -> Unit,
    private val isAdmin: Boolean
) : RecyclerView.Adapter<ProjectsAdapter.ProjectsViewHolder>() {

    inner class ProjectsViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val pNameTextView: TextView = itemView.findViewById(R.id.projectNameTextView)
        private val pCostTextView: TextView = itemView.findViewById(R.id.projectCostTextView)
        private val pDepTextView: TextView = itemView.findViewById(R.id.projectDepartmentTextView)
        private val pDateBegView: TextView = itemView.findViewById(R.id.projectDateBegTextView)
        private val pDateEndView: TextView = itemView.findViewById(R.id.projectDateEndTextView)
        private val pDateEndRealView: TextView = itemView.findViewById(R.id.projectDateEndRealTextView)
        private val buttCalc: Button = itemView.findViewById(R.id.profitButton)

        fun bind(project: Project) {
            pNameTextView.text = project.name
            pCostTextView.text = project.cost.toString()
            pDepTextView.text = project.department.name
            pDateBegView.text = project.dateBeg ?: ""
            pDateEndView.text = project.dateEnd ?: ""
            pDateEndRealView.text = project.dateEndReal ?: ""

            if (project.dateEndReal == null || project.dateEndReal == ""){
                if (project.dateEnd != "" && project.dateEnd != null){
                    if ((Date.valueOf(project.dateEnd)>Date(System.currentTimeMillis()))){
                        buttCalc.visibility = View.VISIBLE
                    }
                    else{
                        buttCalc.visibility = View.GONE
                    }
                }
                    else{
                    buttCalc.visibility = View.VISIBLE
                    }
                }
            else {
                buttCalc.visibility = View.GONE
            }

            buttCalc.setOnClickListener{
                project.id?.let { it1 -> onCalcClick(it1, itemView) }
            }

            if (isAdmin){
                itemView.findViewById<Button>(R.id.editButtonProject).setOnClickListener {
                    onEditClick(project)
                }
                itemView.findViewById<Button>(R.id.deleteButtonProject).setOnClickListener {
                    project.id?.let { it1 -> onDeleteClick(it1) }
                }
            }
            else{
                itemView.findViewById<Button>(R.id.editButtonProject).visibility = View.GONE
                itemView.findViewById<Button>(R.id.deleteButtonProject).visibility = View.GONE
            }
        }
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProjectsViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_projects, parent, false)
        return ProjectsViewHolder(view)
    }

    override fun onBindViewHolder(holder: ProjectsViewHolder, position: Int) {
        holder.bind(projects[position])
    }

    override fun getItemCount(): Int = projects.size

    fun setProjects(project: List<Project>) {
        projects.clear()
        projects.addAll(project)
        notifyDataSetChanged()
    }
}
