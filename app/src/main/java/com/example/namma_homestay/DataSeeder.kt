package com.example.namma_homestay

import com.google.firebase.firestore.FirebaseFirestore

object DataSeeder {
    fun seedData() {
        val db = FirebaseFirestore.getInstance()
        val properties = listOf(
            Property("Heritage Coorg Home", "Kodagu (Coorg)", "3500", "https://images.unsplash.com/photo-1566073771259-6a8506099945", 2, true),
            Property("Misty Woods Estate", "Kodagu (Coorg)", "4200", "https://images.unsplash.com/photo-1449156001437-3a1f9d8ad7ba", 4, true),
            Property("Coffee Bean Villa", "Chikmagalur", "2800", "https://images.unsplash.com/photo-1580587771525-78b9dba3b914", 2, false),
            Property("Hill View Paradise", "Chikmagalur", "3200", "https://images.unsplash.com/photo-1542314831-068cd1dbfeeb", 2, true),
            Property("Royal Mysore Stay", "Mysuru", "2500", "https://images.unsplash.com/photo-1590073242678-70ee3fc28e8e", 4, false),
            Property("Garden City Retreat", "Bengaluru Urban", "5500", "https://images.unsplash.com/photo-1564013799919-ab600027ffc6", 2, true),
            Property("Coastal Breeze Udupi", "Udupi", "3800", "https://images.unsplash.com/photo-1499793983690-e29da59ef1c2", 2, true),
            Property("Dandeli River Side", "Uttara Kannada", "3000", "https://images.unsplash.com/photo-1520250497591-112f2f40a3f4", 4, false),
            Property("Gokarna Beach Shack", "Uttara Kannada", "1800", "https://images.unsplash.com/photo-1540541338287-41700207dee6", 2, false),
            Property("Jog Falls View Lodge", "Shivamogga", "2200", "https://images.unsplash.com/photo-1571019613454-1cb2f99b2d8b", 2, false)
        )

        val collection = db.collection("properties")
        properties.forEach { property ->
            collection.add(property)
        }
    }
}
