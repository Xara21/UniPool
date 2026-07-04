package com.example.unipool

import android.content.Context
import org.json.JSONArray
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import com.example.unipool.models.TripLog



object TripManager {
    var currentDriverName: String = "Jane Doe"
    var currentTrip: TripLog? = null

    val tripLogsList = mutableListOf<TripLog>()

    private const val PREFS_NAME = "unipool_prefs"
    private const val KEY_TRIPS = "trip_logs"

    fun initDefaultTrips() {
        if (tripLogsList.isEmpty()) {
            tripLogsList.add(TripLog("1", "Jane Doe", "3", "2025-07-21 08:00 AM", "2025-07-21 09:00 AM", "Main Campus", "Completed", 12, "Regular Noon Shuttle"))
            tripLogsList.add(TripLog("2", "John Smith", "5", "2025-07-21 09:30 AM", "2025-07-21 10:30 AM", "East Campus", "In Progress", 8, "Express Route"))
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
                        obj.getString("tripType")
                    )
                )
            }
        } catch (e: Exception) {
            initDefaultTrips()
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
                put("putType", trip.tripType)
                put("tripType", trip.tripType)
            }
            jsonArray.put(obj)
        }

        prefs.edit().putString(KEY_TRIPS, jsonArray.toString()).apply()
    }

    fun addTrip(context: Context, trip: TripLog) {
        loadFromStorage(context)
        tripLogsList.add(trip)
        saveToStorage(context)
    }

    fun updateTrip(context: Context, index: Int, trip: TripLog) {
        if (index in 0 until tripLogsList.size) {
            tripLogsList[index] = trip
            saveToStorage(context)
        }
    }

    fun getCurrentFormattedTime(): String {
        val sdf = SimpleDateFormat("yyyy-MM-dd hh:mm a", Locale.getDefault())
        return sdf.format(Date())
    }
}