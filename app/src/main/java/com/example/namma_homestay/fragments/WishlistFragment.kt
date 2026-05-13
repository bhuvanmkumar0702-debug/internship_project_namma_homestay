package com.example.namma_homestay.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.namma_homestay.Property
import com.example.namma_homestay.PropertyAdapter
import com.example.namma_homestay.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class WishlistFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var emptyTv: TextView
    private lateinit var wishlist: ArrayList<Property>
    private lateinit var adapter: PropertyAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_wishlist, container, false)

        recyclerView = view.findViewById(R.id.wishlistRecyclerView)
        emptyTv = view.findViewById(R.id.emptyWishlistTv)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        wishlist = arrayListOf()
        adapter = PropertyAdapter(wishlist)
        recyclerView.adapter = adapter

        fetchWishlist()

        return view
    }

    private fun fetchWishlist() {
        val user = FirebaseAuth.getInstance().currentUser ?: return
        val db = FirebaseFirestore.getInstance()

        db.collection("wishlist")
            .document(user.uid)
            .collection("my_favorites")
            .addSnapshotListener { result, error ->
                if (error != null) return@addSnapshotListener

                if (result != null) {
                    wishlist.clear()
                    for (doc in result) {
                        val property = doc.toObject(Property::class.java)
                        wishlist.add(property)
                    }
                    adapter.notifyDataSetChanged()

                    if (wishlist.isEmpty()) {
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
