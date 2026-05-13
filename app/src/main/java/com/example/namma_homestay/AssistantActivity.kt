package com.example.namma_homestay

import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.ai.client.generativeai.GenerativeModel
import kotlinx.coroutines.launch

import com.google.ai.client.generativeai.type.generationConfig

class AssistantActivity : AppCompatActivity() {

    private lateinit var adapter: ChatAdapter
    private val messages = ArrayList<ChatMessage>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_assistant)

        val queryEt = findViewById<EditText>(R.id.queryEt)
        val sendBtn = findViewById<Button>(R.id.sendBtn)
        val recyclerView = findViewById<RecyclerView>(R.id.chatRecyclerView)

        adapter = ChatAdapter(messages)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter

        // Using gemini-1.5-flash which is the current standard
        val generativeModel = GenerativeModel(
            modelName = "gemini-1.5-flash",
            apiKey = Config.GEMINI_API_KEY
        )

        // Initial welcome message
        messages.add(ChatMessage("Hello! I am your Namma HomeStay assistant. How can I help you find the perfect stay today?", false))
        adapter.notifyItemInserted(messages.size - 1)

        sendBtn.setOnClickListener {
            val query = queryEt.text.toString().trim()
            if (query.isNotEmpty()) {
                // Add user message
                messages.add(ChatMessage(query, true))
                adapter.notifyItemInserted(messages.size - 1)
                recyclerView.scrollToPosition(messages.size - 1)
                
                queryEt.setText("")

                // Add "Thinking..." message
                val thinkingIndex = messages.size
                messages.add(ChatMessage("VERSION 1.4: Checking Connection...", false))
                adapter.notifyItemInserted(thinkingIndex)
                recyclerView.scrollToPosition(messages.size - 1)
                
                lifecycleScope.launch {
                    try {
                        val prompt = "Respond with 'The AI is working!'"
                        val response = generativeModel.generateContent(prompt)
                        
                        val responseText = response.text
                        if (responseText.isNullOrEmpty()) {
                             messages[thinkingIndex] = ChatMessage("Empty response from AI.", false)
                        } else {
                            messages[thinkingIndex] = ChatMessage(responseText, false)
                        }
                        adapter.notifyItemChanged(thinkingIndex)
                    } catch (e: Exception) {
                        val errorDetail = e.toString()
                        messages[thinkingIndex] = ChatMessage("DEBUG ERROR: $errorDetail", false)
                        adapter.notifyItemChanged(thinkingIndex)
                    }
                }
            } else {
                Toast.makeText(this, "Please enter a message", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
