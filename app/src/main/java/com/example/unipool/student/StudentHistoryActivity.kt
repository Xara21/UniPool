package com.example.unipool.student

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.unipool.R
import com.example.unipool.managers.TripManager

class StudentHistoryActivity : AppCompatActivity() {

    private val currentStudentId = "STU001"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_student_placeholder)

        TripManager.loadFromStorage(this)

        val txtTitle =
            findViewById<TextView>(R.id.txtTitle)

        val txtSubtitle =
            findViewById<TextView>(R.id.txtSubtitle)

        txtTitle.text = "Trip History"

        val completedTrips = TripManager.tripLogsList.filter { trip ->

            trip.status == "Completed" &&

                    trip.seats.any { seat ->

                        seat.passengerId == currentStudentId
                    }
        }

        if (completedTrips.isEmpty()) {

            txtSubtitle.text =
                "You have no completed trips yet."

        } else {

            val historyText = StringBuilder()

            completedTrips.forEach { trip ->

                historyText.append(

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

                historyText.append("\n\n")
            }


            txtSubtitle.text = historyText.toString()

        }
    }
}