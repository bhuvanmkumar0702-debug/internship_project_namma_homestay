package com.example.namma_homestay

import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import java.util.*

class PropertyDetailsActivity : AppCompatActivity() {

    private var checkInDate: String? = null
    private var checkOutDate: String? = null
    private var pricePerNight: Int = 0
    private var capacity: Int = 2

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_property_details)

        val name = intent.getStringExtra("NAME") ?: ""
        val location = intent.getStringExtra("LOCATION") ?: ""
        val priceStr = intent.getStringExtra("PRICE") ?: "0"
        val image = intent.getStringExtra("IMAGE") ?: ""
        capacity = intent.getIntExtra("CAPACITY", 2)
        val isAc = intent.getBooleanExtra("IS_AC", false)

        pricePerNight = priceStr.toIntOrNull() ?: 0

        val detailImage = findViewById<ImageView>(R.id.detailImage)
        val detailTitle = findViewById<TextView>(R.id.detailTitle)
        val detailLocation = findViewById<TextView>(R.id.detailLocation)
        val detailPrice = findViewById<TextView>(R.id.detailPrice)
        val capacityInfo = findViewById<TextView>(R.id.propertyCapacityInfo)
        val peopleEt = findViewById<EditText>(R.id.peopleCountEt)
        val bookNowBtn = findViewById<Button>(R.id.bookNowBtn)

        detailTitle.text = name
        detailLocation.text = "$location (${if(isAc) "A/C" else "Non A/C"})"
        detailPrice.text = "₹$priceStr"
        capacityInfo.text = "Max capacity: $capacity persons per room"

        Glide.with(this)
            .load(image)
            .placeholder(android.R.drawable.ic_menu_gallery)
            .into(detailImage)

        bookNowBtn.setOnClickListener {
            val peopleCount = peopleEt.text.toString().toIntOrNull() ?: 0
            if (peopleCount > 0) {
                showCheckInPicker(name, image, peopleCount)
            } else {
                Toast.makeText(this, "Please enter number of people", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun showCheckInPicker(name: String, image: String, people: Int) {
        val calendar = Calendar.getInstance()
        val datePickerDialog = DatePickerDialog(this, { _, year, month, dayOfMonth ->
            checkInDate = "$dayOfMonth/${month + 1}/$year"
            showCheckOutPicker(name, image, people, year, month, dayOfMonth)
        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH))
        
        datePickerDialog.datePicker.minDate = System.currentTimeMillis() - 1000
        datePickerDialog.setTitle("Select Check-in Date")
        datePickerDialog.show()
    }

    private fun showCheckOutPicker(name: String, image: String, people: Int, year: Int, month: Int, day: Int) {
        val calendar = Calendar.getInstance()
        calendar.set(year, month, day)
        
        val datePickerDialog = DatePickerDialog(this, { _, y, m, d ->
            checkOutDate = "$d/${m + 1}/$y"
            
            // Calculate days
            val checkInCal = Calendar.getInstance()
            checkInCal.set(year, month, day)
            val checkOutCal = Calendar.getInstance()
            checkOutCal.set(y, m, d)
            
            val diff = checkOutCal.timeInMillis - checkInCal.timeInMillis
            val days = (diff / (24 * 60 * 60 * 1000)).toInt().coerceAtLeast(1)

            val intent = Intent(this, PaymentActivity::class.java)
            intent.putExtra("NAME", name)
            intent.putExtra("IMAGE", image)
            intent.putExtra("CHECK_IN", checkInDate)
            intent.putExtra("CHECK_OUT", checkOutDate)
            intent.putExtra("PEOPLE", people)
            intent.putExtra("CAPACITY", capacity)
            intent.putExtra("PRICE", pricePerNight)
            intent.putExtra("DAYS", days)
            startActivity(intent)
            finish()
        }, year, month, day + 1)

        datePickerDialog.datePicker.minDate = calendar.timeInMillis + (24 * 60 * 60 * 1000)
        datePickerDialog.setTitle("Select Check-out Date")
        datePickerDialog.show()
    }
}
