package com.example.namma_homestay

data class Booking(
    var bookingId: String = "",
    var propertyName: String = "",
    var propertyImage: String = "",
    var userEmail: String = "",
    var checkInDate: String = "",
    var checkOutDate: String = "",
    var numberOfPeople: Int = 1,
    var numberOfRooms: Int = 1,
    var totalPrice: Int = 0,
    var status: String = "Booked"
)
