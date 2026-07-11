package com.example.unipool

import android.content.Context
import com.example.unipool.models.Seat
import com.example.unipool.models.SeatStatus
import com.example.unipool.models.TripLog
import com.example.unipool.models.PassengerRole
import org.json.JSONArray
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object TripManager {

    var currentDriverName: String = "Jane Doe"
    var currentTrip: TripLog? = null

    val tripLogsList = mutableListOf<TripLog>()

    private const val PREFS_NAME = "unipool_prefs"
    private const val KEY_TRIPS = "trip_logs"

    private fun createDefaultSeats(): MutableList<Seat> {

        val seats = mutableListOf<Seat>()

        val layout = listOf(
            "A1", "A2", "A3", "A4", "A5",
            "B1", "B2", "B3", "B4", "B5",
            "C1", "C2", "C3", "C4",
            "D1", "D2", "D3", "D4"
        )

        layout.forEach {
            seats.add(
                Seat(
                    id = it,
                    status = SeatStatus.AVAILABLE
                )
            )
        }

        return seats
    }

    fun initDefaultTrips() {

        if (tripLogsList.isEmpty()) {

            tripLogsList.add(
                TripLog(
                    "1",
                    "Jane Doe",
                    "3",
                    "2025-07-21 08:00 AM",
                    "2025-07-21 09:00 AM",
                    "Main Campus",
                    "Completed",
                    12,
                    "Regular Noon Shuttle",
                    createDefaultSeats()
                )
            )

            tripLogsList.add(
                TripLog(
                    "2",
                    "John Smith",
                    "5",
                    "2025-07-21 09:30 AM",
                    "2025-07-21 10:30 AM",
                    "East Campus",
                    "In Progress",
                    8,
                    "Express Route",
                    createDefaultSeats()
                )
            )
        }
    }

    fun loadFromStorage(context: Context) {

        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val jsonString = prefs.getString(KEY_TRIPS, null)

        tripLogsList.clear()

        if (jsonString == null) {
            initDefaultTrips()
            saveToStorage(context)
            return
        }

        try {

            val jsonArray = JSONArray(jsonString)

            for (i in 0 until jsonArray.length()) {

                val obj = jsonArray.getJSONObject(i)

                val seats = mutableListOf<Seat>()

                val seatsArray = obj.optJSONArray("seats")

                if (seatsArray != null) {

                    for (j in 0 until seatsArray.length()) {

                        val seatObj = seatsArray.getJSONObject(j)

                        seats.add(
                            Seat(
                                id = seatObj.getString("id"),

                                status = SeatStatus.valueOf(
                                    seatObj.getString("status")
                                ),

                                passengerId =
                                    if (seatObj.isNull("passengerId"))
                                        null
                                    else
                                        seatObj.getString("passengerId"),

                                passengerName =
                                    if (seatObj.isNull("passengerName"))
                                        null
                                    else
                                        seatObj.getString("passengerName"),

                                passengerRole =
                                    if (seatObj.isNull("passengerRole"))
                                        null
                                    else
                                        PassengerRole.valueOf(
                                            seatObj.getString("passengerRole")
                                        )
                            )
                        )
                    }

                } else {

                    // Older saved trips won't contain seats
                    seats.addAll(createDefaultSeats())

                }

                tripLogsList.add(
                    TripLog(
                        obj.getString("tripId"),
                        obj.getString("driverName"),
                        obj.getString("shuttleId"),
                        obj.getString("departureTime"),
                        obj.getString("arrivalTime"),
                        obj.getString("destination"),
                        obj.getString("status"),
                        obj.getInt("passengerCount"),
                        obj.getString("tripType"),
                        seats
                    )
                )
            }

            currentTrip = tripLogsList.firstOrNull {
                it.status == "In Progress"
            }

            if (currentTrip == null && tripLogsList.isNotEmpty()) {
                currentTrip = tripLogsList.last()
            }

        } catch (e: Exception) {

            initDefaultTrips()

            currentTrip = tripLogsList.firstOrNull {
                it.status == "In Progress"
            }
        }
    }

    fun saveToStorage(context: Context) {

        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

        val jsonArray = JSONArray()

        for (trip in tripLogsList) {

            val obj = JSONObject().apply {

                put("tripId", trip.tripId)
                put("driverName", trip.driverName)
                put("shuttleId", trip.shuttleId)
                put("departureTime", trip.departureTime)
                put("arrivalTime", trip.arrivalTime)
                put("destination", trip.destination)
                put("status", trip.status)
                put("passengerCount", trip.passengerCount)
                put("tripType", trip.tripType)

                val seatsArray = JSONArray()

                for (seat in trip.seats) {

                    val seatObj = JSONObject().apply {

                        put("id", seat.id)

                        put("status", seat.status.name)

                        put("passengerId", seat.passengerId)

                        put("passengerName", seat.passengerName)

                        put(
                            "passengerRole",
                            seat.passengerRole?.name
                        )
                    }

                    seatsArray.put(seatObj)
                }

                put("seats", seatsArray)
            }

            jsonArray.put(obj)
        }

        prefs.edit()
            .putString(KEY_TRIPS, jsonArray.toString())
            .apply()
    }

    fun addTrip(context: Context, trip: TripLog) {

        loadFromStorage(context)

        tripLogsList.add(trip)

        currentTrip = trip

        saveToStorage(context)
    }

    fun updateTrip(context: Context, index: Int, trip: TripLog) {

        if (index in tripLogsList.indices) {

            tripLogsList[index] = trip

            if (currentTrip?.tripId == trip.tripId) {
                currentTrip = trip
            }

            saveToStorage(context)
        }
    }

    fun getCurrentFormattedTime(): String {

        val sdf = SimpleDateFormat(
            "yyyy-MM-dd hh:mm a",
            Locale.getDefault()
        )

        return sdf.format(Date())
    }

    fun hasExistingReservation(
        passengerId: String
    ): Boolean {

        return tripLogsList.any { trip ->

            trip.seats.any { seat ->

                seat.passengerId == passengerId &&
                        seat.status == SeatStatus.RESERVED

            }

        }

    }

    fun removeReservation(
        passengerId: String
    ) {

        tripLogsList.forEach { trip ->

            trip.seats.forEach { seat ->

                if (
                    seat.passengerId == passengerId &&
                    seat.status == SeatStatus.RESERVED
                ) {

                    seat.status = SeatStatus.AVAILABLE
                    seat.passengerId = null
                    seat.passengerName = null
                    seat.passengerRole = null
                }

            }

        }

    }
}