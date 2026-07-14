package com.example.unipool

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.unipool.managers.TripManager

class StaffHistoryActivity : AppCompatActivity() {

    private val currentStaffId = "STA001"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_student_placeholder)

        android.util.Log.d(
            "STAFF_HISTORY",
            "StaffHistory opened"
        )

        TripManager.loadFromStorage(this)

        val txtTitle =
            findViewById<TextView>(R.id.txtTitle)

        val txtSubtitle =
            findViewById<TextView>(R.id.txtSubtitle)

        txtTitle.text = "Staff History"

        val completedTrips = TripManager.tripLogsList.filter { trip ->

            trip.status == "Completed" &&

                    trip.seats.any { seat ->

                        seat.passengerId == currentStaffId
                    }
        }

        android.util.Log.d(
            "STAFF_HISTORY",
            "Found ${completedTrips.size} completed trips"
        )

        if (completedTrips.isEmpty()) {

            txtSubtitle.text =
                "You have no completed trips yet."

        } else {

            val history = StringBuilder()

            completedTrips.forEach { trip ->

                history.append(

                    """
                    🚍 Trip #${trip.tripId}
                    Driver: ${trip.driverName}
                    Destination: ${trip.destination}
                    Departure: ${trip.departureTime}
                    Arrival: ${trip.arrivalTime}
                    Status: ${trip.status}
                    
                    -------------------------
                    
                    """.trimIndent()

                )

                history.append("\n\n")
            }

            txtSubtitle.text = history.toString()
        }
    }
}