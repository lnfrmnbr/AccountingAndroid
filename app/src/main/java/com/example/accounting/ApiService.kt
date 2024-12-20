package com.example.accounting

import com.example.accounting.models.Department
import com.example.accounting.models.DepartmentEmployer
import com.example.accounting.models.Employer
import com.example.accounting.models.Project
import com.example.accounting.models.User
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.DELETE
import retrofit2.http.Body
import retrofit2.http.Path

interface ApiService {
    @POST("/register")
    suspend fun register(@Body user: User): Boolean

    @POST("/login")
    suspend fun login(@Body user: User): Boolean

    @POST("/admin")
    suspend fun isAdmin(@Body user: User): Boolean

    @GET("/departments")
    suspend fun getDepartments(): List<Department>

    @POST("/departments")
    suspend fun addDepartment(@Body department: Department): Long?

    @PUT("/departments/{id}")
    suspend fun updateDepartment(@Path("id") id: Long, @Body department: Department)

    @DELETE("/departments/{id}")
    suspend fun deleteDepartment(@Path("id") id: Long)

    @GET("/employees")
    suspend fun getEmployees(): List<Employer>

    @POST("/employees")
    suspend fun addEmployer(@Body employer: Employer): Long?

    @PUT("/employees/{id}")
    suspend fun updateEmployees(@Path("id") id: Long, @Body employer: Employer)

    @DELETE("/employees/{id}")
    suspend fun deleteEmployer(@Path("id") id: Long)

    @GET("/department_employers")
    suspend fun getDepartmentsEmpl(): List<DepartmentEmployer>

    @POST("/department_employers")
    suspend fun addDepartmentEmpl(@Body department: DepartmentEmployer): Long?

    @PUT("/department_employers/{id}")
    suspend fun updateDepartmentEmpl(@Path("id") id: Long, @Body department: DepartmentEmployer)

    @DELETE("/department_employers/{id}")
    suspend fun deleteDepartmentEmpl(@Path("id") id: Long)

    @GET("/projects")
    suspend fun getProjects(): List<Project>

    @POST("/projects")
    suspend fun addProject(@Body project: Project): Long?

    @PUT("/projects/{id}")
    suspend fun updateProject(@Path("id") id: Long, @Body project: Project)

    @DELETE("/projects/{id}")
    suspend fun deleteProject(@Path("id") id: Long)

    @GET("/profit")
    suspend fun getTotalProfit(): Int

    @GET("/profit/{id}")
    suspend fun getProfit(@Path("id") id: Long): Int
}
