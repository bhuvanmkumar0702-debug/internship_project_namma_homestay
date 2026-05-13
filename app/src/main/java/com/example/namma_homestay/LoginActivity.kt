package com.example.namma_homestay

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AlertDialog
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidUserException

class LoginActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        auth = FirebaseAuth.getInstance()

        val email = findViewById<EditText>(R.id.emailEt)
        val password = findViewById<EditText>(R.id.passwordEt)
        val loginBtn = findViewById<Button>(R.id.loginBtn)
        val signupText = findViewById<TextView>(R.id.signupText)
        val forgotPasswordTv = findViewById<TextView>(R.id.forgotPasswordTv)

        loginBtn.setOnClickListener {
            val emailStr = email.text.toString()
            val passStr = password.text.toString()

            if (emailStr.isNotEmpty() && passStr.isNotEmpty()) {
                auth.signInWithEmailAndPassword(emailStr, passStr)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            startActivity(Intent(this, HomeActivity::class.java))
                            finish()
                        } else {
                            if (task.exception is FirebaseAuthInvalidUserException) {
                                showUserNotFoundDialog()
                            } else {
                                Toast.makeText(this, "Login Failed: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                            }
                        }
                    }
            } else {
                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show()
            }
        }

        signupText.setOnClickListener {
            startActivity(Intent(this, SignupActivity::class.java))
        }

        forgotPasswordTv.setOnClickListener {
            startActivity(Intent(this, ForgotPasswordActivity::class.java))
        }
    }

    private fun showUserNotFoundDialog() {
        AlertDialog.Builder(this)
            .setTitle("User Not Found")
            .setMessage("User not found, please create an account to enter.")
            .setPositiveButton("Create Account") { _, _ ->
                startActivity(Intent(this, SignupActivity::class.java))
            }
            .setNegativeButton("Cancel", null)
            .show()
    }
}
