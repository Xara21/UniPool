package com.example.unipool.student

import android.graphics.Color
import android.os.Bundle
import android.widget.Button
import android.widget.GridLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.unipool.R
import com.example.unipool.managers.TripManager
import com.example.unipool.models.TripLog
import com.example.unipool.models.SeatStatus
import android.view.LayoutInflater
import androidx.appcompat.widget.AppCompatButton

class StudentReserveSeatActivity : AppCompatActivity() {

    private lateinit var txtDriverName: TextView
    private lateinit var txtDepartureTime: TextView
    private lateinit var txtRouteName: TextView
    private lateinit var txtCurrentStatus: TextView

    private lateinit var txtOccupied: TextView
    private lateinit var txtReserved: TextView
    private lateinit var txtAvailable: TextView

    private lateinit var btnSelectTrip: Button

    private lateinit var gridSeats: GridLayout

    private lateinit var panelInformationCard: android.widget.LinearLayout

    private var currentTrip: TripLog? = null

    private var selectedSeatIndex = -1

    private val currentStudentId = "STU001"

    private val currentStudentName = "John Student"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_student_reserve_seat)

        initializeViews()

        TripManager.loadFromStorage(this)

        currentTrip = TripManager.currentTrip

        loadTripInformation()

        btnSelectTrip.setOnClickListener {
            reserveSelectedSeat()
        }
    }

    private fun initializeViews() {

        txtDriverName = findViewById(R.id.txtDriverName)

        txtDepartureTime = findViewById(R.id.txtDepartureTime)

        txtRouteName = findViewById(R.id.txtRouteName)

        txtCurrentStatus = findViewById(R.id.txtCurrentStatus)

        txtOccupied = findViewById(R.id.txtOccupied)

        txtReserved = findViewById(R.id.txtReserved)

        txtAvailable = findViewById(R.id.txtAvailable)

        btnSelectTrip = findViewById(R.id.btnSelectTrip)

        gridSeats = findViewById(R.id.gridSeats)

        panelInformationCard = findViewById(R.id.panelInformationCard)

    }

    private fun loadTripInformation() {

        val trip = currentTrip ?: return

        txtDriverName.text = trip.driverName

        txtDepartureTime.text = trip.departureTime

        txtRouteName.text = trip.destination

        txtCurrentStatus.text = trip.status

        refreshSeatCounters()

        refreshStatusCard()

        loadSeatGrid()
    }

    private fun refreshSeatCounters() {

        val trip = currentTrip ?: return

        val occupied = trip.seats.count {
            it.status == SeatStatus.OCCUPIED
        }

        val reserved = trip.seats.count {
            it.status == SeatStatus.RESERVED
        }

        val available = trip.seats.count {
            it.status == SeatStatus.AVAILABLE
        }

        txtOccupied.text = "🟢 Occupied: $occupied"

        txtReserved.text = "🟡 Reserved: $reserved"

        txtAvailable.text = "🔴 Available: $available"
    }

    private fun refreshStatusCard() {

        val trip = currentTrip ?: return

        when (trip.status) {

            "Scheduled" -> {
                panelInformationCard.setBackgroundColor(
                    Color.parseColor("#FFEB3B")
                )
            }

            "In Progress" -> {
                panelInformationCard.setBackgroundColor(
                    Color.parseColor("#4CAF50")
                )
            }

            "Completed" -> {
                panelInformationCard.setBackgroundColor(
                    Color.parseColor("#2196F3")
                )
            }
        }
    }


    private fun reserveSelectedSeat() {

        val trip = currentTrip ?: return

        if (selectedSeatIndex == -1) {

            android.widget.Toast.makeText(
                this,
                "Please select a seat first.",
                android.widget.Toast.LENGTH_SHORT
            ).show()

            return
        }

        // Check if this student already has a reservation
        val existingSeat = trip.seats.find {

            it.passengerId == currentStudentId &&
                    it.status == SeatStatus.RESERVED
        }

        if (existingSeat != null) {

            android.widget.Toast.makeText(
                this,
                "You already reserved seat ${existingSeat.id}",
                android.widget.Toast.LENGTH_LONG
            ).show()

            return
        }

        val seat = trip.seats[selectedSeatIndex]

        if (seat.status != SeatStatus.AVAILABLE) {

            android.widget.Toast.makeText(
                this,
                "Seat is no longer available.",
                android.widget.Toast.LENGTH_SHORT
            ).show()

            return
        }

        seat.status = SeatStatus.RESERVED

        seat.passengerId = currentStudentId

        seat.passengerName = currentStudentName

        seat.passengerRole = com.example.unipool.models.PassengerRole.STUDENT

        TripManager.saveToStorage(this)

        refreshSeatCounters()

        loadSeatGrid()

        android.widget.Toast.makeText(
            this,
            "Seat ${seat.id} reserved!",
            android.widget.Toast.LENGTH_SHORT
        ).show()
    }

    private fun loadSeatGrid() {

        val trip = currentTrip ?: return

        gridSeats.removeAllViews()

        for (seat in trip.seats) {

            val button = LayoutInflater.from(this)
                .inflate(
                    R.layout.item_seat,
                    gridSeats,
                    false
                ) as AppCompatButton

            button.text = seat.id

            val params = GridLayout.LayoutParams()

            params.width = 140
            params.height = 140

            params.setMargins(8,8,8,8)

            button.layoutParams = params

            when (seat.status) {

                SeatStatus.AVAILABLE ->
                    button.setBackgroundColor(Color.RED)

                SeatStatus.RESERVED ->
                    button.setBackgroundColor(Color.YELLOW)

                SeatStatus.OCCUPIED ->
                    button.setBackgroundColor(Color.GREEN)
            }

            if (trip.seats.indexOf(seat) == selectedSeatIndex) {

                button.setBackgroundColor(Color.BLUE)

                button.setTextColor(Color.WHITE)
            }

            button.setOnClickListener {

                selectedSeatIndex = trip.seats.indexOf(seat)

                android.widget.Toast.makeText(
                    this,
                    "Selected ${seat.id}",
                    android.widget.Toast.LENGTH_SHORT
                ).show()

                loadSeatGrid()
            }

            gridSeats.addView(button)
        }
    }
}