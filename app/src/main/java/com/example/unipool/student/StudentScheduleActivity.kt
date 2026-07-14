package com.example.unipool.student

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.unipool.R
import com.example.unipool.managers.TripManager
import com.example.unipool.models.SeatStatus
import com.example.unipool.NotificationManager

class StudentScheduleActivity : AppCompatActivity() {

    private lateinit var txtTitle: TextView
    private lateinit var txtSubtitle: TextView
    private lateinit var btnCancelReservation: Button

    private val studentId = "STU001"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_student_placeholder)

        txtTitle = findViewById(R.id.txtTitle)
        txtSubtitle = findViewById(R.id.txtSubtitle)
        btnCancelReservation =
            findViewById(R.id.btnCancelReservation)

        loadReservation()
    }

    override fun onResume() {
        super.onResume()
        loadReservation()
    }

    private fun loadReservation() {

        TripManager.loadFromStorage(this)

        val trip = TripManager.tripLogsList.firstOrNull { trip ->

            trip.seats.any { seat ->

                seat.passengerId == studentId &&
                        seat.status == SeatStatus.RESERVED
            }
        }

        if (trip == null) {

            txtTitle.text = "My Reservation"

            txtSubtitle.text =
                "No reservation found."

            btnCancelReservation.visibility = View.GONE

            return
        }

        val seat = trip.seats.first {

            it.passengerId == studentId &&
                    it.status == SeatStatus.RESERVED
        }

        txtTitle.text = "Trip #${trip.tripId}"

        txtSubtitle.text =
            "Destination: ${trip.destination}\n" +
                    "Departure: ${trip.departureTime}\n" +
                    "Seat: ${seat.id}"

        btnCancelReservation.visibility = View.VISIBLE

        btnCancelReservation.setOnClickListener {

            AlertDialog.Builder(this)

                .setTitle("Cancel reservation")

                .setMessage(
                    "Are you sure you want to cancel your reservation?"
                )

                .setNegativeButton("No", null)

                .setPositiveButton("Yes") { _, _ ->

                    TripManager.removeReservation(studentId)

                    NotificationManager.add(
                        "Your reservation has been cancelled."
                    )

                    TripManager.saveToStorage(this)

                    Toast.makeText(
                        this,
                        "Reservation cancelled.",
                        Toast.LENGTH_SHORT
                    ).show()

                    loadReservation()
                }

                .show()
        }
    }
}