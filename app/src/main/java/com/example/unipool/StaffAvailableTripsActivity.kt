package com.example.unipool

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.unipool.managers.TripManager
import android.view.LayoutInflater
import androidx.appcompat.widget.AppCompatButton

class StaffAvailableTripsActivity : AppCompatActivity() {

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

            val card = LayoutInflater.from(this)
                .inflate(
                    R.layout.item_trip_card,
                    layoutTrips,
                    false
                )

            val title =
                card.findViewById<TextView>(R.id.txtTripTitle)

            val destination =
                card.findViewById<TextView>(R.id.txtDestination)

            val departure =
                card.findViewById<TextView>(R.id.txtDeparture)

            val seats =
                card.findViewById<TextView>(R.id.txtSeats)

            val button =
                card.findViewById<AppCompatButton>(R.id.btnReserve)

            title.text =
                "Trip #${trip.tripId}"

            destination.text =
                trip.destination

            departure.text =
                trip.departureTime

            seats.text =
                trip.availableCount().toString()

            button.setOnClickListener {

                TripManager.currentTrip = trip

                val intent = Intent(
                    this,
                    PassengerSeatReservationActivity::class.java
                )

                intent.putExtra("IS_STAFF", true)

                startActivity(intent)
            }


            layoutTrips.addView(card)
        }
    }
}