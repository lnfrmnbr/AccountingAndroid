package com.example.accounting

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.textfield.TextInputEditText
import android.widget.Button
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import com.example.accounting.models.User
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {
    private val apiService by lazy { RetrofitInstance.api }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        val authLogin: TextInputEditText = findViewById(R.id.auth_login)
        val authPassword: TextInputEditText = findViewById(R.id.auth_password)
        val authButton: Button = findViewById(R.id.auth)

        authButton.setOnClickListener {
            val login = authLogin.text.toString().trim()
            val password = authPassword.text.toString().trim()

            if (login.isNotEmpty() && password.isNotEmpty()) {
                lifecycleScope.launch {
                    try {
                        val response: Boolean = apiService.login(User(login = login, password = password, admin = false))
                        if (response) {
                            Toast.makeText(this@MainActivity, "Авторизация успешна", Toast.LENGTH_SHORT).show()
                            val intentt = Intent(this@MainActivity, AccountingActivity::class.java)
                            startActivity(intentt)

                            val isAdmin: Boolean = apiService.isAdmin(User(login = login, password = password, admin = false))
                            val sharedPreferences = getSharedPreferences("MyPreferences", MODE_PRIVATE)
                            val editor = sharedPreferences.edit()
                            editor.putBoolean("isAdmin", isAdmin)
                            editor.apply()
                        } else {
                            Toast.makeText(this@MainActivity, "Ошибка авторизации", Toast.LENGTH_SHORT).show()
                        }
                    } catch (e: Exception) {
                        Log.e("VIK", "${e.message}")
                        Toast.makeText(this@MainActivity, "Ошибка: ${e.message}", Toast.LENGTH_SHORT).show()
                    }
                }
            } else {
                Toast.makeText(this, "Пожалуйста, введите и логин, и пароль", Toast.LENGTH_SHORT).show()
            }
        }
    }
}