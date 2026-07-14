package com.example.unipool

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.unipool.managers.TripManager

class DriverHistoryActivity : AppCompatActivity() {

    private val currentDriverName = "Jane Doe"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_student_placeholder)

        TripManager.loadFromStorage(this)

        val txtTitle =
            findViewById<TextView>(R.id.txtTitle)

        val txtSubtitle =
            findViewById<TextView>(R.id.txtSubtitle)

        txtTitle.text = "Driver History"

        val completedTrips = TripManager.tripLogsList.filter {

            it.status == "Completed" &&
                    it.driverName == currentDriverName
        }

        if (completedTrips.isEmpty()) {

            txtSubtitle.text =
                "No completed trips yet."

        } else {

            val history = StringBuilder()

            completedTrips.forEach { trip ->

                history.append(

                    """
                    🚍 Trip #${trip.tripId}
                    Shuttle ID: ${trip.shuttleId}
                    Destination: ${trip.destination}
                    Departure: ${trip.departureTime}
                    Arrival: ${trip.arrivalTime}
                    Passengers: ${trip.passengerCount}
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