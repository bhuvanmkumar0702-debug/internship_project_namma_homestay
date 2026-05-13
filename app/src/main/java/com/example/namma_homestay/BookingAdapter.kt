package com.example.namma_homestay

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.firestore.FirebaseFirestore

class BookingAdapter(
    private val list: ArrayList<Booking>
) : RecyclerView.Adapter<BookingAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val image: ImageView = view.findViewById(R.id.bookingPropertyImage)
        val name: TextView = view.findViewById(R.id.bookingPropertyName)
        val date: TextView = view.findViewById(R.id.bookingDateTv)
        val status: TextView = view.findViewById(R.id.bookingStatusTv)
        val cancelBtn: Button = view.findViewById(R.id.cancelBookingBtn)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_booking, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int = list.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val booking = list[position]
        holder.name.text = booking.propertyName
        holder.date.text = "Stay: ${booking.checkInDate} to ${booking.checkOutDate}"
        holder.status.text = "Status: ${booking.status}"

        Glide.with(holder.itemView.context)
            .load(booking.propertyImage)
            .placeholder(android.R.drawable.ic_menu_gallery)
            .into(holder.image)

        holder.cancelBtn.setOnClickListener {
            showCancelConfirmation(booking, holder.itemView)
        }
    }

    private fun showCancelConfirmation(booking: Booking, view: View) {
        AlertDialog.Builder(view.context)
            .setTitle("Cancel Booking")
            .setMessage("Are you sure you want to cancel your stay at ${booking.propertyName}?")
            .setPositiveButton("Yes, Cancel") { _, _ ->
                cancelBooking(booking, view)
            }
            .setNegativeButton("No", null)
            .show()
    }

    private fun cancelBooking(booking: Booking, view: View) {
        val db = FirebaseFirestore.getInstance()
        
        db.collection("bookings")
            .whereEqualTo("propertyName", booking.propertyName)
            .whereEqualTo("checkInDate", booking.checkInDate)
            .whereEqualTo("userEmail", booking.userEmail)
            .get()
            .addOnSuccessListener { result ->
                for (doc in result) {
                    doc.reference.delete().addOnSuccessListener {
                        Toast.makeText(view.context, "Booking Cancelled", Toast.LENGTH_SHORT).show()
                    }
                }
            }
    }
}
