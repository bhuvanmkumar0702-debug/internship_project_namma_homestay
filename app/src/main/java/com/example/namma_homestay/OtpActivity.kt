package com.example.namma_homestay

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class OtpActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_otp)

        val email = intent.getStringExtra("EMAIL") ?: ""
        val otpEt = findViewById<EditText>(R.id.otpEt)
        val verifyBtn = findViewById<Button>(R.id.verifyOtpBtn)
        val msgTv = findViewById<TextView>(R.id.otpMsgTv)

        msgTv.text = "An OTP has been sent to $email"

        verifyBtn.setOnClickListener {
            val enteredOtp = otpEt.text.toString().trim()
            
            // For this demo, we use a fixed OTP "123456" 
            // In real app, this would be verified against a backend
            if (enteredOtp == "123456") {
                val intent = Intent(this, ResetPasswordActivity::class.java)
                intent.putExtra("EMAIL", email)
                startActivity(intent)
                finish()
            } else {
                Toast.makeText(this, "Incorrect OTP. Try 123456", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
