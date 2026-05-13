package com.example.namma_homestay.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.namma_homestay.Booking
import com.example.namma_homestay.BookingAdapter
import com.example.namma_homestay.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class BookingsFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var emptyTv: TextView
    private lateinit var bookingList: ArrayList<Booking>
    private lateinit var adapter: BookingAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_bookings, container, false)

        recyclerView = view.findViewById(R.id.bookingsRecyclerView)
        emptyTv = view.findViewById(R.id.emptyBookingsTv)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        bookingList = arrayListOf()
        adapter = BookingAdapter(bookingList)
        recyclerView.adapter = adapter

        fetchBookings()

        return view
    }

    private fun fetchBookings() {
        val user = FirebaseAuth.getInstance().currentUser ?: return
        val db = FirebaseFirestore.getInstance()

        db.collection("bookings")
            .whereEqualTo("userEmail", user.email)
            .addSnapshotListener { result, error ->
                if (error != null) return@addSnapshotListener

                if (result != null) {
                    bookingList.clear()
                    for (doc in result) {
                        val booking = doc.toObject(Booking::class.java)
                        if (booking != null) {
                            bookingList.add(booking)
                        }
                    }
                    adapter.notifyDataSetChanged()
                    
                    if (bookingList.isEmpty()) {
                        emptyTv.visibility = View.VISIBLE
                        recyclerView.visibility = View.GONE
                    } else {
                        emptyTv.visibility = View.GONE
                        recyclerView.visibility = View.VISIBLE
                    }
                }
            }
    }
}
