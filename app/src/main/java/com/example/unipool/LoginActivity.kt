package com.example.unipool

import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity

class LoginActivity : AppCompatActivity() {

    private lateinit var etUsername: EditText
    private lateinit var etEmail: EditText
    private lateinit var etPassword: EditText
    private lateinit var btnLogin: Button
    private lateinit var tvSignUpLink: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        etUsername   = findViewById(R.id.et_username)
        etEmail      = findViewById(R.id.et_email)
        etPassword   = findViewById(R.id.et_password)
        btnLogin     = findViewById(R.id.btn_login)
        tvSignUpLink = findViewById(R.id.tv_signup_link)

        btnLogin.setOnClickListener { attemptLogin() }

        tvSignUpLink.setOnClickListener {
            startActivity(Intent(this, SignUpAsActivity::class.java))
        }
    }

    private fun attemptLogin() {

        val username = etUsername.text.toString().trim()
        val email = etEmail.text.toString().trim()
        val password = etPassword.text.toString()

        if (username.isBlank()) {
            etUsername.error = "Username is required"
            return
        }

        if (email.isBlank() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            etEmail.error = "Enter a valid email"
            return
        }

        if (password.isBlank()) {
            etPassword.error = "Password is required"
            return
        }

        val role = detectRole(username, email)

        Toast.makeText(this, "Logged in as $role", Toast.LENGTH_SHORT).show()

        when (role) {

            "Driver" -> {
                startActivity(Intent(this, DriverHomeActivity::class.java))
                finish()
            }

            "Student" -> {
                // TODO: Student Dashboard
                Toast.makeText(this, "Student Dashboard Coming Soon", Toast.LENGTH_SHORT).show()
            }

            "Staff" -> {
                // TODO: Staff Dashboard
                Toast.makeText(this, "Staff Dashboard Coming Soon", Toast.LENGTH_SHORT).show()
            }

            "Admin" -> {
                // TODO: Admin Dashboard
                Toast.makeText(this, "Admin Dashboard Coming Soon", Toast.LENGTH_SHORT).show()
            }

            else -> {
                Toast.makeText(this, "Invalid username or email.", Toast.LENGTH_SHORT).show()
            }
        }
    }



    private fun detectRole(username: String, email: String): String {

        return when {

            username.startsWith("STU", true) &&
                    email.endsWith("@live.mcl.edu.ph", true) -> "Student"

            username.startsWith("STA", true) &&
                    email.endsWith("@mcl.edu.ph", true) -> "Staff"

            username.startsWith("DRI", true) &&
                    email.endsWith("@gmail.com", true) -> "Driver"

            username.startsWith("ADM", true) &&
                    email.endsWith("@gmail.com", true) -> "Admin"

            else -> "Unknown"
        }
    }
}