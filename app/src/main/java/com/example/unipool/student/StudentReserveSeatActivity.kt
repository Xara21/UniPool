package com.example.unipool.student

import android.graphics.Color
import android.os.Bundle
import android.widget.Button
import android.widget.GridLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.unipool.R
import com.example.unipool.TripManager
import com.example.unipool.models.TripLog
import com.example.unipool.models.SeatStatus

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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_student_reserve_seat)

        initializeViews()

        TripManager.loadFromStorage(this)

        currentTrip = TripManager.currentTrip

        loadTripInformation()
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
}