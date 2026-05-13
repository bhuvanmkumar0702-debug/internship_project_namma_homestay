package com.example.namma_homestay

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SwitchCompat
import androidx.biometric.BiometricManager
import androidx.core.content.edit
import com.google.firebase.auth.FirebaseAuth

class SettingsActivity : AppCompatActivity() {

    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        sharedPreferences = getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
        val fingerprintSwitch = findViewById<SwitchCompat>(R.id.fingerprintSwitch)
        val logoutBtn = findViewById<Button>(R.id.logoutBtn)
        val callUsBtn = findViewById<Button>(R.id.callUsBtn)
        val emailUsBtn = findViewById<Button>(R.id.emailUsBtn)

        // Set initial state
        fingerprintSwitch.isChecked = sharedPreferences.getBoolean("fingerprint_enabled", false)

        fingerprintSwitch.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                if (checkBiometricSupport()) {
                    sharedPreferences.edit {
                        putBoolean("fingerprint_enabled", true)
                    }
                } else {
                    fingerprintSwitch.isChecked = false
                    Toast.makeText(this, "Biometric authentication not supported on this device", Toast.LENGTH_SHORT).show()
                }
            } else {
                sharedPreferences.edit {
                    putBoolean("fingerprint_enabled", false)
                }
            }
        }

        callUsBtn.setOnClickListener {
            val intent = Intent(Intent.ACTION_DIAL)
            intent.data = Uri.parse("tel:+919019477732")
            startActivity(intent)
        }

        emailUsBtn.setOnClickListener {
            val intent = Intent(Intent.ACTION_SENDTO)
            intent.data = Uri.parse("mailto:bhuvanmkumar0702@gmail.com")
            intent.putExtra(Intent.EXTRA_SUBJECT, "Query regarding Namma HomeStay")
            try {
                startActivity(intent)
            } catch (e: Exception) {
                Toast.makeText(this, "No email app found", Toast.LENGTH_SHORT).show()
            }
        }

        logoutBtn.setOnClickListener {
            FirebaseAuth.getInstance().signOut()
            val intent = Intent(this, LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish()
        }
    }

    private fun checkBiometricSupport(): Boolean {
        val biometricManager = BiometricManager.from(this)
        return when (biometricManager.canAuthenticate(BiometricManager.Authenticators.BIOMETRIC_STRONG)) {
            BiometricManager.BIOMETRIC_SUCCESS -> true
            else -> false
        }
    }
}
