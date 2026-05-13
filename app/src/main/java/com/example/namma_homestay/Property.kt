package com.example.namma_homestay

data class Property(
    var name: String = "",
    var location: String = "",
    var price: String = "",
    var image: String = "",
    var capacity: Int = 2, // Max people per room (2 or 4)
    var isAc: Boolean = false
)
