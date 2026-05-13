package com.example.namma_homestay.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.namma_homestay.*
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration

import android.widget.TextView
import androidx.lifecycle.lifecycleScope
import com.google.ai.client.generativeai.GenerativeModel
import kotlinx.coroutines.launch

class HomeFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var propertyList: ArrayList<Property>
    private lateinit var adapter: PropertyAdapter
    private lateinit var aiSuggestionTv: TextView
    private var firestoreListener: ListenerRegistration? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_home, container, false)

        aiSuggestionTv = view.findViewById(R.id.aiSuggestionTv)

        view.findViewById<ImageButton>(R.id.settingsBtn).setOnClickListener {
            startActivity(Intent(requireContext(), SettingsActivity::class.java))
        }

        view.findViewById<FloatingActionButton>(R.id.assistantFab).setOnClickListener {
            startActivity(Intent(requireContext(), AssistantActivity::class.java))
        }

        view.findViewById<FloatingActionButton>(R.id.addPropertyFab).setOnClickListener {
            startActivity(Intent(requireContext(), AddPropertyActivity::class.java))
        }

        recyclerView = view.findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        propertyList = arrayListOf()
        adapter = PropertyAdapter(propertyList)
        recyclerView.adapter = adapter

        setupRealtimeListener()
        fetchAiSuggestion()
        return view
    }

    private fun fetchAiSuggestion() {
        val generativeModel = GenerativeModel(
            modelName = "gemini-1.5-flash",
            apiKey = Config.GEMINI_API_KEY
        )

        lifecycleScope.launch {
            try {
                val prompt = "You are a travel assistant for 'Namma HomeStay'. Give a short, 1-sentence friendly suggestion for a traveler looking for budget-friendly and cozy stays in Karnataka."
                val response = generativeModel.generateContent(prompt)
                aiSuggestionTv.text = "AI Smart Suggestion: ${response.text}"
            } catch (e: Exception) {
                aiSuggestionTv.text = "AI Suggestion: Discover the best of Karnataka's hospitality!"
            }
        }
    }

    private fun setupRealtimeListener() {
        val db = FirebaseFirestore.getInstance()
        firestoreListener = db.collection("properties")
            .addSnapshotListener { result, error ->
                if (error != null) return@addSnapshotListener
                if (result != null) {
                    propertyList.clear()
                    for (doc in result) {
                        val property = doc.toObject(Property::class.java)
                        if (property != null) propertyList.add(property)
                    }
                    adapter.notifyDataSetChanged()
                }
            }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        firestoreListener?.remove()
    }
}
