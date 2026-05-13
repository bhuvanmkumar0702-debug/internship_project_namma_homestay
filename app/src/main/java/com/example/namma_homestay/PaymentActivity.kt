package com.example.namma_homestay

import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

import kotlin.math.ceil

class PaymentActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_payment)

        val propertyName = intent.getStringExtra("NAME") ?: ""
        val propertyImage = intent.getStringExtra("IMAGE") ?: ""
        val checkIn = intent.getStringExtra("CHECK_IN") ?: ""
        val checkOut = intent.getStringExtra("CHECK_OUT") ?: ""
        val people = intent.getIntExtra("PEOPLE", 1)
        val capacity = intent.getIntExtra("CAPACITY", 2)
        val pricePerNight = intent.getIntExtra("PRICE", 0)
        val days = intent.getIntExtra("DAYS", 1)

        // Booking Logic: If people exceeds limit, book multiple rooms
        val roomsNeeded = ceil(people.toDouble() / capacity.toDouble()).toInt()
        val grandTotal = pricePerNight * roomsNeeded * days

        val summaryTv = findViewById<TextView>(R.id.paymentSummaryTv)
        summaryTv.text = """
            Property: $propertyName
            Stay: $checkIn to $checkOut ($days days)
            People: $people
            Rooms Required: $roomsNeeded (Capacity: $capacity/room)
            Price: ₹$pricePerNight x $roomsNeeded rooms x $days days
            ----------------------------
            GRAND TOTAL: ₹$grandTotal
        """.trimIndent()

        findViewById<Button>(R.id.payUpiBtn).setOnClickListener { 
            processBooking(propertyName, propertyImage, checkIn, checkOut, people, roomsNeeded, grandTotal, "UPI") 
        }
        findViewById<Button>(R.id.payNetBtn).setOnClickListener { 
            processBooking(propertyName, propertyImage, checkIn, checkOut, people, roomsNeeded, grandTotal, "Net Banking") 
        }
        findViewById<Button>(R.id.payCardBtn).setOnClickListener { 
            processBooking(propertyName, propertyImage, checkIn, checkOut, people, roomsNeeded, grandTotal, "Credit Card") 
        }
    }

    private fun processBooking(name: String, image: String, checkIn: String, checkOut: String, people: Int, rooms: Int, total: Int, method: String) {
        val user = FirebaseAuth.getInstance().currentUser
        if (user == null) {
            Toast.makeText(this, "Session expired. Please login again.", Toast.LENGTH_SHORT).show()
            return
        }

        val db = FirebaseFirestore.getInstance()
        val booking = Booking(
            propertyName = name,
            propertyImage = image,
            userEmail = user.email ?: "",
            checkInDate = checkIn,
            checkOutDate = checkOut,
            numberOfPeople = people,
            numberOfRooms = rooms,
            totalPrice = total,
            status = "Booked"
        )

        db.collection("bookings")
            .add(booking)
            .addOnSuccessListener {
                Toast.makeText(this, "Payment Successful via $method! Status: Booked", Toast.LENGTH_LONG).show()
                finish()
            }
            .addOnFailureListener {
                Toast.makeText(this, "Failed to confirm booking", Toast.LENGTH_SHORT).show()
            }
    }
}
