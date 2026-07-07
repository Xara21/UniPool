package com.example.unipool.models

data class Seat(

    val id: String,

    var status: SeatStatus = SeatStatus.AVAILABLE,

    var passengerId: String? = null,

    var passengerName: String? = null,

    var passengerRole: PassengerRole? = null

)