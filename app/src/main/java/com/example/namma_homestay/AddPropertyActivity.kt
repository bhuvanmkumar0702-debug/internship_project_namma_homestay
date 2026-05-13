package com.example.namma_homestay

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.firestore.FirebaseFirestore

class AddPropertyActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_property)

        val nameEt = findViewById<EditText>(R.id.nameEt)
        val locationEt = findViewById<EditText>(R.id.locationEt)
        val priceEt = findViewById<EditText>(R.id.priceEt)
        val imageEt = findViewById<EditText>(R.id.imageEt)
        val saveBtn = findViewById<Button>(R.id.saveBtn)
        val importBtn = findViewById<Button>(R.id.importStaysBtn)

        saveBtn.setOnClickListener {
            val name = nameEt.text.toString()
            val location = locationEt.text.toString()
            val price = priceEt.text.toString()
            val image = imageEt.text.toString()

            if (name.isNotEmpty() && location.isNotEmpty() && price.isNotEmpty() && image.isNotEmpty()) {
                saveProperty(name, location, price, image)
            } else {
                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show()
            }
        }

        importBtn.setOnClickListener {
            DataSeeder.seedData()
            Toast.makeText(this, "Importing Homestays from all Districts...", Toast.LENGTH_LONG).show()
            finish()
        }
    }

    private fun saveProperty(name: String, location: String, price: String, image: String) {
        val db = FirebaseFirestore.getInstance()
        val property = Property(name, location, price, image)

        db.collection("properties")
            .add(property)
            .addOnSuccessListener {
                Toast.makeText(this, "Property Posted Successfully!", Toast.LENGTH_SHORT).show()
                finish()
            }
            .addOnFailureListener {
                Toast.makeText(this, "Failed to post property", Toast.LENGTH_SHORT).show()
            }
    }
}
