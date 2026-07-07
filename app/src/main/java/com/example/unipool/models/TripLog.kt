package com.example.unipool.models

data class TripLog(

    val tripId: String,

    val driverName: String,

    val shuttleId: String,

    val departureTime: String,

    val arrivalTime: String,

    val destination: String,

    var status: String,

    var passengerCount: Int,

    val tripType: String,

    val seats: MutableList<Seat> = mutableListOf()

) {

    fun occupiedCount(): Int {
        return seats.count { it.status == SeatStatus.OCCUPIED }
    }

    fun reservedCount(): Int {
        return seats.count { it.status == SeatStatus.RESERVED }
    }

    fun availableCount(): Int {
        return seats.count { it.status == SeatStatus.AVAILABLE }
    }

}