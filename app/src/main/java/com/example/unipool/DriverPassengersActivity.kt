package com.example.unipool

import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TableLayout
import android.widget.TextView
import android.widget.Toast
import android.app.AlertDialog
import android.widget.GridLayout
import android.widget.ScrollView
import android.graphics.Color
import android.view.Gravity
import android.widget.TableRow
import androidx.appcompat.app.AppCompatActivity
import com.example.unipool.models.TripLog
import com.example.unipool.models.SeatStatus

class DriverPassengersActivity : AppCompatActivity() {

    private lateinit var panelStatusCard: LinearLayout

    private lateinit var txtCurrentStatus: TextView
    private lateinit var txtDriverName: TextView
    private lateinit var txtDepartureTime: TextView
    private lateinit var txtRouteName: TextView

    private lateinit var txtOccupied: TextView
    private lateinit var txtReserved: TextView
    private lateinit var txtAvailable: TextView

    private lateinit var btnSelectTrip: Button
    private lateinit var btnManageSeats: Button
    private lateinit var btnActionTrip: Button

    private lateinit var btnHamburger: ImageView

    private lateinit var tablePassengerManifest: TableLayout

    private var currentTrip: TripLog? = null

    private var currentStatusState = 0

    override fun onCreate(savedInstanceState: Bundle?) {

            super.onCreate(savedInstanceState)

            setContentView(R.layout.activity_driver_passengers)

            initializeViews()

            TripManager.loadFromStorage(this)

            currentTrip = TripManager.currentTrip

            if (currentTrip == null) {

                Toast.makeText(
                    this,
                    "No active trip found.",
                    Toast.LENGTH_LONG
                ).show()

                finish()
                return
            }

            loadTripInformation()
            setupButtons()
        }


    private fun refreshPassengerManifest() {

        val trip = currentTrip ?: return

        val childCount = tablePassengerManifest.childCount

        if (childCount > 1) {
            tablePassengerManifest.removeViews(
                1,
                childCount - 1
            )
        }

        var passengerNumber = 1

        for (seat in trip.seats) {

            if (seat.status != SeatStatus.OCCUPIED) {
                continue
            }

            val row = TableRow(this)

            row.layoutParams =
                TableLayout.LayoutParams(
                    TableLayout.LayoutParams.MATCH_PARENT,
                    TableLayout.LayoutParams.WRAP_CONTENT
                )

            val txtPassenger = TextView(this)

            txtPassenger.layoutParams =
                TableRow.LayoutParams(
                    0,
                    TableRow.LayoutParams.WRAP_CONTENT,
                    1.2f
                )

            txtPassenger.text = "Passenger $passengerNumber"

            txtPassenger.setPadding(8,16,8,16)

            val txtSeat = TextView(this)

            txtSeat.layoutParams =
                TableRow.LayoutParams(
                    0,
                    TableRow.LayoutParams.WRAP_CONTENT,
                    1f
                )

            txtSeat.gravity = Gravity.CENTER

            txtSeat.text = seat.id

            val btnNotify = Button(this)

            btnNotify.layoutParams =
                TableRow.LayoutParams(
                    0,
                    TableRow.LayoutParams.WRAP_CONTENT,
                    1f
                )

            btnNotify.text = "Notify"

            btnNotify.setOnClickListener {

                Toast.makeText(
                    this,
                    "Notification sent to Passenger $passengerNumber",
                    Toast.LENGTH_SHORT
                ).show()
            }

            row.addView(txtPassenger)
            row.addView(txtSeat)
            row.addView(btnNotify)

            tablePassengerManifest.addView(row)

            passengerNumber++
        }
    }

    private fun refreshSeatCounters() {

        val trip = currentTrip ?: return

        val occupied =
            trip.seats.count {
                it.status == SeatStatus.OCCUPIED
            }

        val reserved =
            trip.seats.count {
                it.status == SeatStatus.RESERVED
            }

        val available =
            trip.seats.count {
                it.status == SeatStatus.AVAILABLE
            }

        txtOccupied.text =
            "🟢 Occupied: $occupied"

        txtReserved.text =
            "🟡 Reserved: $reserved"

        txtAvailable.text =
            "🔴 Available: $available"
    }

    private fun setupButtons() {

        btnManageSeats.setOnClickListener {
            showSeatDialog()
        }

        btnSelectTrip.setOnClickListener {

            Toast.makeText(
                this,
                "Trip selection will be added next.",
                Toast.LENGTH_SHORT
            ).show()
        }

        btnActionTrip.setOnClickListener {

            val trip = currentTrip ?: return@setOnClickListener

            when (trip.status) {

                "Scheduled" -> {

                    trip.status = "In Progress"

                    btnActionTrip.text = "End Trip"
                }

                "In Progress" -> {

                    trip.status = "Completed"

                    btnActionTrip.text = "Trip Completed"

                    btnActionTrip.isEnabled = false
                }
            }

            txtCurrentStatus.text = trip.status

            TripManager.saveToStorage(this)
        }
    }

    private fun showSeatDialog() {

        val trip = currentTrip ?: return

        val scrollView = ScrollView(this)

        val grid = GridLayout(this).apply {
            columnCount = 5
            useDefaultMargins = true
        }

        scrollView.addView(grid)

        for (seat in trip.seats) {

            val button = Button(this)

            button.layoutParams =
                GridLayout.LayoutParams().apply {
                    width = 150
                    height = 150
                    setMargins(10,10,10,10)
                }

            fun refreshSeatButton() {

                when (seat.status) {

                    SeatStatus.AVAILABLE -> {
                        button.text = "🔴\n${seat.id}"
                        button.setBackgroundColor(Color.parseColor("#F44336"))
                    }

                    SeatStatus.RESERVED -> {
                        button.text = "🟡\n${seat.id}"
                        button.setBackgroundColor(Color.parseColor("#FFC107"))
                    }

                    SeatStatus.OCCUPIED -> {
                        button.text = "🟢\n${seat.id}"
                        button.setBackgroundColor(Color.parseColor("#4CAF50"))
                    }
                }
            }

            refreshSeatButton()

            button.setOnClickListener {

                seat.status =
                    when (seat.status) {

                        SeatStatus.AVAILABLE ->
                            SeatStatus.RESERVED

                        SeatStatus.RESERVED ->
                            SeatStatus.OCCUPIED

                        SeatStatus.OCCUPIED ->
                            SeatStatus.AVAILABLE
                    }

                refreshSeatButton()

                refreshPassengerManifest()

                refreshSeatCounters()
            }

            grid.addView(button)
        }

        AlertDialog.Builder(this)
            .setTitle("Seat Occupation")
            .setView(scrollView)
            .setPositiveButton("Done") { _, _ ->

                TripManager.saveToStorage(this)

            }
            .show()
    }

    private fun initializeViews() {

        panelStatusCard =
            findViewById(R.id.panelStatusCard)

        txtCurrentStatus =
            findViewById(R.id.txtCurrentStatus)

        txtDriverName =
            findViewById(R.id.txtDriverName)

        txtDepartureTime =
            findViewById(R.id.txtDepartureTime)

        txtRouteName =
            findViewById(R.id.txtRouteName)

        txtOccupied =
            findViewById(R.id.txtOccupied)

        txtReserved =
            findViewById(R.id.txtReserved)

        txtAvailable =
            findViewById(R.id.txtAvailable)

        btnSelectTrip =
            findViewById(R.id.btnSelectTrip)

        btnManageSeats =
            findViewById(R.id.btnManageSeats)

        btnActionTrip =
            findViewById(R.id.btnActionTrip)

        btnHamburger =
            findViewById(R.id.btnHamburger)

        tablePassengerManifest =
            findViewById(R.id.tablePassengerManifest)
    }

    private fun loadTripInformation() {

        val trip = currentTrip ?: return

        txtDriverName.text = trip.driverName
        txtDepartureTime.text = trip.departureTime
        txtRouteName.text = trip.destination
        txtCurrentStatus.text = trip.status

        refreshPassengerManifest()

        refreshSeatCounters()
    }

}

