package com.example.namma_homestay.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.SearchView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.namma_homestay.*
import com.google.firebase.firestore.FirebaseFirestore

class SearchFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var propertyList: ArrayList<Property>
    private lateinit var adapter: PropertyAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_search, container, false)

        val searchView = view.findViewById<SearchView>(R.id.searchView)
        recyclerView = view.findViewById(R.id.searchRecyclerView)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        propertyList = arrayListOf()
        adapter = PropertyAdapter(propertyList)
        recyclerView.adapter = adapter

        view.findViewById<Button>(R.id.filterAllBtn).setOnClickListener { fetchAllProperties() }
        view.findViewById<Button>(R.id.filterBudgetBtn).setOnClickListener { filterProperties(0, 2000) }
        view.findViewById<Button>(R.id.filterMidBtn).setOnClickListener { filterProperties(2000, 5000) }
        view.findViewById<Button>(R.id.filterLuxuryBtn).setOnClickListener { filterProperties(5000, Int.MAX_VALUE) }

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                if (query != null) searchProperties(query)
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                if (newText != null) searchProperties(newText)
                return true
            }
        })

        fetchAllProperties()
        return view
    }

    private fun fetchAllProperties() {
        val db = FirebaseFirestore.getInstance()
        db.collection("properties")
            .get()
            .addOnSuccessListener { result ->
                propertyList.clear()
                for (doc in result) {
                    val property = doc.toObject(Property::class.java)
                    if (property != null) propertyList.add(property)
                }
                adapter.notifyDataSetChanged()
            }
    }

    private fun filterProperties(min: Int, max: Int) {
        val db = FirebaseFirestore.getInstance()
        db.collection("properties")
            .get()
            .addOnSuccessListener { result ->
                propertyList.clear()
                for (doc in result) {
                    val property = doc.toObject(Property::class.java)
                    if (property != null) {
                        val price = property.price.toIntOrNull() ?: 0
                        if (price in min..max) {
                            propertyList.add(property)
                        }
                    }
                }
                adapter.notifyDataSetChanged()
            }
    }

    private fun searchProperties(query: String) {
        val db = FirebaseFirestore.getInstance()
        db.collection("properties")
            .whereGreaterThanOrEqualTo("location", query)
            .whereLessThanOrEqualTo("location", query + "\uf8ff")
            .get()
            .addOnSuccessListener { result ->
                propertyList.clear()
                if (result.isEmpty) {
                    Toast.makeText(requireContext(), "No listing found in this place", Toast.LENGTH_SHORT).show()
                } else {
                    for (doc in result) {
                        val property = doc.toObject(Property::class.java)
                        propertyList.add(property)
                    }
                }
                adapter.notifyDataSetChanged()
            }
    }
}
