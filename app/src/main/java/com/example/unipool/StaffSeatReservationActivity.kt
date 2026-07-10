package com.example.unipool

import android.app.AlertDialog
import android.graphics.Color
import android.os.Bundle
import android.view.Gravity
import android.widget.Button
import android.widget.GridLayout
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.unipool.R
import com.example.unipool.TripManager
import com.example.unipool.models.PassengerRole
import com.example.unipool.models.SeatStatus

class StaffSeatReservationActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

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

                if(seat.status!=SeatStatus.AVAILABLE){

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

                    .setPositiveButton("Reserve"){_,_->

                        seat.status=SeatStatus.RESERVED

                        seat.passengerName = "Maria Staff"

                        seat.passengerId = "STA001"

                        seat.passengerRole = PassengerRole.STAFF

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