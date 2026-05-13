package com.example.namma_homestay

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class ResetPasswordActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reset_password)

        val newPassEt = findViewById<EditText>(R.id.newPassEt)
        val rePassEt = findViewById<EditText>(R.id.reNewPassEt)
        val updateBtn = findViewById<Button>(R.id.updatePassBtn)

        updateBtn.setOnClickListener {
            val pass1 = newPassEt.text.toString()
            val pass2 = rePassEt.text.toString()

            if (pass1.isNotEmpty() && pass1 == pass2) {
                // NOTE: Real password reset happens via the link sent to email.
                // This UI flow is for demonstration as requested.
                Toast.makeText(this, "Password reset successful! Please use the link in your email to finalize.", Toast.LENGTH_LONG).show()
                finish()
            } else {
                Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
