package com.example.unipool

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.unipool.managers.TripManager

class PassengerAvailableTripsActivity : AppCompatActivity() {

    private lateinit var layoutTrips: LinearLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_passenger_available_trips)

        layoutTrips = findViewById(R.id.layoutTrips)

        TripManager.loadFromStorage(this)

        loadTrips()
    }

    private fun loadTrips() {

        layoutTrips.removeAllViews()

        for (trip in TripManager.tripLogsList) {

            if (
                trip.status != "Scheduled" &&
                trip.status != "In Progress"
            ) continue

            val card = LinearLayout(this)

            card.orientation = LinearLayout.VERTICAL

            card.setPadding(40,40,40,40)

            val title = TextView(this)

            title.text =
                "Trip #${trip.tripId}"

            title.textSize = 20f

            val destination = TextView(this)

            destination.text =
                "Destination: ${trip.destination}"

            val departure = TextView(this)

            departure.text =
                "Departure: ${trip.departureTime}"

            val seats = TextView(this)

            seats.text =
                "Available Seats: ${trip.availableCount()}"

            val button = Button(this)

            button.text = "Reserve"

            button.setOnClickListener {

                TripManager.currentTrip = trip

                val intent = Intent(
                    this,
                    PassengerSeatReservationActivity::class.java
                )

                intent.putExtra("IS_STAFF", false)

                startActivity(intent)
            }

            card.addView(title)
            card.addView(destination)
            card.addView(departure)
            card.addView(seats)
            card.addView(button)

            layoutTrips.addView(card)
        }
    }
}