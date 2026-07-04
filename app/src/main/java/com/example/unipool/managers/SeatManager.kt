package com.example.unipool.managers

import com.example.unipool.models.Seat
import com.example.unipool.models.SeatStatus

class SeatManager {

    val seats = mutableListOf<Seat>()

    fun generateSeats(passengerCount: Int) {

        val rows = ('A'..'F').toList()

        val newSeats = mutableListOf<Seat>()

        var generated = 0

        outer@ for (row in rows) {

            for (col in 1..5) {

                if (generated >= passengerCount)
                    break@outer

                val seatId = "$row$col"

                val existingSeat = seats.find { it.id == seatId }

                if (existingSeat != null) {
                    newSeats.add(existingSeat)
                } else {
                    newSeats.add(Seat(seatId))
                }

                generated++
            }
        }

        seats.clear()
        seats.addAll(newSeats)
    }

    fun toggleSeat(seat: Seat) {

        seat.status =
            when (seat.status) {

                SeatStatus.AVAILABLE ->
                    SeatStatus.RESERVED

                SeatStatus.RESERVED ->
                    SeatStatus.OCCUPIED

                SeatStatus.OCCUPIED ->
                    SeatStatus.AVAILABLE
            }
    }

    fun occupiedCount() =
        seats.count { it.status == SeatStatus.OCCUPIED }

    fun reservedCount() =
        seats.count { it.status == SeatStatus.RESERVED }

    fun availableCount() =
        seats.count { it.status == SeatStatus.AVAILABLE }
}