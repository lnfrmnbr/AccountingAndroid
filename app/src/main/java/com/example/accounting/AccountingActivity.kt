package com.example.accounting

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.WindowManager
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import com.example.accounting.fragments.DepartmentEmployerFragment
import com.example.accounting.fragments.DepartmentsFragment
import com.example.accounting.fragments.EmployeesFragment
import com.example.accounting.fragments.ProjectsFragment
import com.qamar.curvedbottomnaviagtion.CurvedBottomNavigation

class AccountingActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_accounting)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val bottomNavigation = findViewById<CurvedBottomNavigation>(R.id.bottomNavigation)
        bottomNavigation.add(
            CurvedBottomNavigation.Model(1, "Сотрудники", R.drawable.employees)
        )
        bottomNavigation.add(
            CurvedBottomNavigation.Model(2, "Департаменты", R.drawable.departments)
        )
        bottomNavigation.add(
            CurvedBottomNavigation.Model(3, "Проекты", R.drawable.projects)
        )
        bottomNavigation.add(
            CurvedBottomNavigation.Model(4, "Связи", R.drawable.dep_empl)
        )

        bottomNavigation.setOnClickMenuListener {
            when(it.id){
                1 -> {
                    replaceFragment(EmployeesFragment())
                }
                2 -> {
                    replaceFragment(DepartmentsFragment())
                }
                3 -> {
                    replaceFragment(ProjectsFragment())
                }
                4 -> {
                    replaceFragment(DepartmentEmployerFragment())
                }
            }
        }
        replaceFragment(EmployeesFragment())
        bottomNavigation.show(1)

        window.setFlags(
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS)

    }

    private fun replaceFragment(fragment: Fragment) {
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.fragmentContainer, fragment)
            .commit()
    }
}