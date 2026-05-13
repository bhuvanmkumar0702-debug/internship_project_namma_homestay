package com.example.namma_homestay

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth

class ForgotPasswordActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_forgot_password)

        val emailEt = findViewById<EditText>(R.id.resetEmailEt)
        val sendBtn = findViewById<Button>(R.id.sendOtpBtn)

        sendBtn.setOnClickListener {
            val email = emailEt.text.toString().trim()
            if (email.isNotEmpty()) {
                checkUserAndSendEmail(email)
            } else {
                Toast.makeText(this, "Please enter your email", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun checkUserAndSendEmail(email: String) {
        val auth = FirebaseAuth.getInstance()
        
        // Check if user exists first
        auth.fetchSignInMethodsForEmail(email)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val methods = task.result?.signInMethods ?: emptyList<String>()
                    if (methods.isNotEmpty()) {
                        // User exists, send reset email and proceed to OTP
                        auth.sendPasswordResetEmail(email)
                        Toast.makeText(this, "OTP Sent (Simulated) & Reset Email Dispatched", Toast.LENGTH_LONG).show()
                        val intent = Intent(this, OtpActivity::class.java)
                        intent.putExtra("EMAIL", email)
                        startActivity(intent)
                    } else {
                        // User does not exist
                        showUserNotFoundDialog()
                    }
                } else {
                    // Other error (e.g. network)
                    Toast.makeText(this, "Error: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                }
            }
    }

    private fun showUserNotFoundDialog() {
        AlertDialog.Builder(this)
            .setTitle("User Not Found")
            .setMessage("User not found, please create an account to enter.")
            .setPositiveButton("Create Account") { _, _ ->
                startActivity(Intent(this, SignupActivity::class.java))
                finish()
            }
            .setNegativeButton("Cancel", null)
            .show()
    }
}
