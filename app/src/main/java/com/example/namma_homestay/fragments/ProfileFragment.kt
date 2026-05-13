package com.example.namma_homestay.fragments

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.core.content.edit
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.example.namma_homestay.*
import com.google.firebase.auth.FirebaseAuth

class ProfileFragment : Fragment() {

    private lateinit var profileImage: ImageView
    private lateinit var profileName: TextView
    private lateinit var googleId: TextView

    private val pickImageLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        uri?.let {
            saveProfileImage(it)
            loadProfileImage(it)
            Toast.makeText(requireContext(), "Profile picture updated!", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_profile, container, false)

        profileImage = view.findViewById(R.id.profileImage)
        profileName = view.findViewById(R.id.profileName)
        googleId = view.findViewById(R.id.googleId)
        val emailTv = view.findViewById<TextView>(R.id.profileEmail)
        val editNameBtn = view.findViewById<ImageButton>(R.id.editNameBtn)
        val settingsBtn = view.findViewById<Button>(R.id.profileSettingsBtn)
        val logoutBtn = view.findViewById<Button>(R.id.profileLogoutBtn)

        val currentUser = FirebaseAuth.getInstance().currentUser
        emailTv.text = currentUser?.email ?: "Not logged in"

        // Display Google ID if logged in via Google
        currentUser?.let { user ->
            if (user.providerData.any { it.providerId == "google.com" }) {
                googleId.visibility = View.VISIBLE
                googleId.text = "Google ID: ${user.uid}"
            }
        }

        // Load saved image and name unique to this user
        if (currentUser != null) {
            val sharedPrefs = requireContext().getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
            
            // Image
            val savedUri = sharedPrefs.getString("profile_image_uri_${currentUser.uid}", null)
            if (savedUri != null) {
                loadProfileImage(Uri.parse(savedUri))
            } else {
                profileImage.setImageResource(R.drawable.ic_account_box)
            }

            // Name
            val savedName = sharedPrefs.getString("profile_name_${currentUser.uid}", "Add Profile Name")
            profileName.text = savedName
        }

        profileImage.setOnClickListener {
            showImagePickDialog()
        }

        editNameBtn.setOnClickListener {
            showNameInputDialog()
        }

        settingsBtn.setOnClickListener {
            startActivity(Intent(requireContext(), SettingsActivity::class.java))
        }

        logoutBtn.setOnClickListener {
            FirebaseAuth.getInstance().signOut()
            val intent = Intent(requireContext(), LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            requireActivity().finish()
        }

        return view
    }

    private fun showNameInputDialog() {
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle("Update Profile Name")

        val input = EditText(requireContext())
        input.hint = "Enter your name"
        input.setText(if (profileName.text == "Add Profile Name") "" else profileName.text)
        builder.setView(input)

        builder.setPositiveButton("Save") { _, _ ->
            val newName = input.text.toString().trim()
            if (newName.isNotEmpty()) {
                saveProfileName(newName)
                profileName.text = newName
            }
        }
        builder.setNegativeButton("Cancel", null)
        builder.show()
    }

    private fun saveProfileName(name: String) {
        val currentUser = FirebaseAuth.getInstance().currentUser ?: return
        val sharedPrefs = requireContext().getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
        sharedPrefs.edit {
            putString("profile_name_${currentUser.uid}", name)
        }
    }

    private fun saveProfileImage(uri: Uri) {
        val currentUser = FirebaseAuth.getInstance().currentUser ?: return
        val sharedPrefs = requireContext().getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
        sharedPrefs.edit {
            putString("profile_image_uri_${currentUser.uid}", uri.toString())
        }
    }

    private fun loadProfileImage(uri: Uri) {
        Glide.with(this)
            .load(uri)
            .circleCrop()
            .into(profileImage)
    }

    private fun showImagePickDialog() {
        AlertDialog.Builder(requireContext())
            .setTitle("Profile Picture")
            .setMessage("Do you want to apply a profile picture?")
            .setPositiveButton("Yes") { _, _ ->
                pickImageLauncher.launch("image/*")
            }
            .setNegativeButton("No", null)
            .show()
    }
}
