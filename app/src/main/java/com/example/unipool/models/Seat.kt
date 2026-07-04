package com.example.unipool.models

data class Seat(
    val id: String,
    var status: SeatStatus = SeatStatus.AVAILABLE
)