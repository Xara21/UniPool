package com.example.unipool

import android.app.AlertDialog
import android.graphics.Color
import android.os.Bundle
import android.widget.Button
import android.widget.GridLayout
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.unipool.managers.TripManager
import com.example.unipool.models.PassengerRole
import com.example.unipool.models.SeatStatus


class PassengerSeatReservationActivity : AppCompatActivity() {

    private var isStaff = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        isStaff = intent.getBooleanExtra("IS_STAFF", false)

        showSeatLayout()
    }

    private fun showSeatLayout() {

        val trip = TripManager.currentTrip ?: run {
            finish()
            return
        }

        val scroll = ScrollView(this)

        val container = LinearLayout(this)

        container.orientation = LinearLayout.VERTICAL
        container.setPadding(30,30,30,30)

        scroll.addView(container)

        val title = TextView(this)

        title.text =
            "Trip #${trip.tripId}\n${trip.destination}\n${trip.departureTime}"

        title.textSize = 20f

        container.addView(title)

        val grid = GridLayout(this)

        grid.columnCount = 5

        grid.setPadding(0,30,0,0)

        container.addView(grid)

        trip.seats.forEach { seat ->

            val btn = Button(this)

            val params = GridLayout.LayoutParams()

            params.width = 170
            params.height = 170

            params.setMargins(10,10,10,10)

            btn.layoutParams = params

            fun refresh(){

                when(seat.status){

                    SeatStatus.AVAILABLE->{

                        btn.text="🔴\n${seat.id}"

                        btn.setBackgroundColor(Color.RED)
                    }

                    SeatStatus.RESERVED->{

                        btn.text="🟡\n${seat.id}"

                        btn.setBackgroundColor(Color.YELLOW)
                    }

                    SeatStatus.OCCUPIED->{

                        btn.text="🟢\n${seat.id}"

                        btn.setBackgroundColor(Color.GREEN)
                    }
                }

            }

            refresh()

            btn.setOnClickListener {

                if (seat.status != SeatStatus.AVAILABLE && !isStaff) {

                    Toast.makeText(
                        this,
                        "Seat unavailable.",
                        Toast.LENGTH_SHORT
                    ).show()

                    return@setOnClickListener
                }

                AlertDialog.Builder(this)

                    .setTitle("Reserve ${seat.id}?")

                    .setMessage(
                        "Reserve this seat?"
                    )

                    .setNegativeButton("Cancel",null)

                    .setPositiveButton("Reserve") { _, _ ->

                        val passengerId =
                            if (isStaff) "STA001"
                            else "STU001"

                        // Students can only reserve one seat
                        android.util.Log.d(
                            "RESERVATION_CHECK",
                            "Checking $passengerId"
                        )

                        android.util.Log.d(
                            "RESERVATION_CHECK",
                            "Already reserved = ${TripManager.hasExistingReservation(passengerId)}"
                        )

                        if (TripManager.hasExistingReservation(passengerId)) {

                            Toast.makeText(
                                this,
                                "You already reserved a seat.",
                                Toast.LENGTH_LONG
                            ).show()

                            return@setPositiveButton
                        }

                        if (!isStaff) {

                            // Student reservation

                            seat.status = SeatStatus.RESERVED

                            seat.passengerName = "John Student"

                            seat.passengerId = "STU001"

                            seat.passengerRole = PassengerRole.STUDENT

                        } else {

                            // Staff reservation

                            if (seat.status == SeatStatus.AVAILABLE) {

                                seat.status = SeatStatus.RESERVED

                                seat.passengerName = "Jane Staff"

                                seat.passengerId = "STA001"

                                seat.passengerRole = PassengerRole.STAFF

                            } else {

                                val studentSeat = trip.seats.lastOrNull {

                                    it.status == SeatStatus.RESERVED &&
                                            it.passengerRole == PassengerRole.STUDENT

                                }

                                if (studentSeat != null) {

                                    // Remove student reservation

                                    studentSeat.status = SeatStatus.AVAILABLE

                                    studentSeat.passengerId = null

                                    studentSeat.passengerName = null

                                    studentSeat.passengerRole = null

                                    NotificationManager.add(
                                        "Your reservation for Trip #${trip.tripId} was replaced because a staff member has priority."
                                    )

                                    // Reserve seat for staff

                                    seat.status = SeatStatus.RESERVED

                                    seat.passengerName = "Jane Staff"

                                    seat.passengerId = "STA001"

                                    seat.passengerRole = PassengerRole.STAFF

                                    Toast.makeText(
                                        this,
                                        "Student reservation replaced.",
                                        Toast.LENGTH_LONG
                                    ).show()

                                } else {

                                    Toast.makeText(
                                        this,
                                        "No student reservation available to replace.",
                                        Toast.LENGTH_LONG
                                    ).show()

                                    return@setPositiveButton
                                }
                            }
                        }

                        trip.seats.forEach {

                            android.util.Log.d(
                                "STAFF_DEBUG",
                                "Seat=${it.id}, passengerId=${it.passengerId}, role=${it.passengerRole}, status=${it.status}"
                            )
                        }

                        TripManager.saveToStorage(this)

                        Toast.makeText(
                            this,
                            "Seat Reserved!",
                            Toast.LENGTH_SHORT
                        ).show()

                        finish()
                    }

                    .show()

            }

            grid.addView(btn)

        }

        setContentView(scroll)

    }

}