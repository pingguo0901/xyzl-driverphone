package com.xyzl.driverphone.model

data class Trip(
    val id: String,
    val origin: String,
    val destination: String,
    val passenger: String,
    val fare: Double,
    val distanceKm: Double,
    val time: String,
    val status: TripStatus,
)

enum class TripStatus { PENDING, ONGOING, COMPLETED, CANCELLED }
