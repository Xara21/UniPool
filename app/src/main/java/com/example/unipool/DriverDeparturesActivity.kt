package com.example.unipool

import android.content.Intent
import android.os.Bundle
import android.widget.*
import android.graphics.Color
import android.widget.GridLayout
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.appcompat.app.AlertDialog
import com.example.unipool.managers.SeatManager
import com.example.unipool.managers.TripManager
import com.example.unipool.models.SeatStatus
import com.example.unipool.models.TripLog
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.navigation.NavigationView
import java.text.SimpleDateFormat
import java.util.*

class DriverDeparturesActivity : AppCompatActivity() {

    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navigationView: NavigationView
    private lateinit var btnMenu: FloatingActionButton

    private lateinit var spinnerShuttle: Spinner
    private lateinit var spinnerDestination: Spinner
    private lateinit var spinnerTripType: Spinner

    private lateinit var spinnerHour: Spinner
    private lateinit var spinnerMinute: Spinner
    private lateinit var spinnerAmPm: Spinner

    private lateinit var btnMinusPassenger: Button
    private lateinit var btnPlusPassenger: Button
    private lateinit var txtPassengerCount: TextView
    private lateinit var txtCapacity: TextView

    private lateinit var txtOccupied: TextView

    private lateinit var txtReserved: TextView

    private lateinit var txtAvailable: TextView

    private lateinit var btnManageSeats: Button
    private lateinit var progressPassengers: ProgressBar
    private lateinit var etNotes: EditText

    private lateinit var btnNow: Button
    private lateinit var btnStartTrip: Button

    private var passengerCount = 0
    private val maxPassengers = 30

    private val seatManager = SeatManager()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_driver_departures)

        drawerLayout = findViewById(R.id.drawerLayout)
        navigationView = findViewById(R.id.navigationView)
        btnMenu = findViewById(R.id.btnMenu)

        setupDrawer()
        initializeViews()
        setupSpinners()
        setupPassengerCounter()
        setupButtons()

        updatePassengerDisplay()
    }

    private fun setupDrawer() {
        btnMenu.setOnClickListener {
            drawerLayout.openDrawer(GravityCompat.START)
        }
        navigationView.setCheckedItem(R.id.nav_departures)
        navigationView.setNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> startActivity(Intent(this, DriverHomeActivity::class.java))
                R.id.nav_departures -> drawerLayout.closeDrawer(GravityCompat.START)
                R.id.nav_passengers -> startActivity(Intent(this, DriverPassengersActivity::class.java))
                R.id.nav_triplogs -> startActivity(Intent(this, DriverTripLogsActivity::class.java))
                R.id.nav_messages -> startActivity(Intent(this, DriverMessagesActivity::class.java))
                R.id.nav_profile -> startActivity(Intent(this, DriverProfileActivity::class.java))
                R.id.nav_logout -> {
                    val intent = Intent(this, LoginActivity::class.java).apply {
                        flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    }
                    startActivity(intent)
                    finish()
                }
            }
            drawerLayout.closeDrawer(GravityCompat.START)
            true
        }

        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
                    drawerLayout.closeDrawer(GravityCompat.START)
                } else {
                    finish()
                }
            }
        })
    }

    private fun initializeViews() {

        spinnerShuttle = findViewById(R.id.spinnerShuttle)
        spinnerDestination = findViewById(R.id.spinnerDestination)
        spinnerTripType = findViewById(R.id.spinnerTripType)

        spinnerHour = findViewById(R.id.spinnerHour)
        spinnerMinute = findViewById(R.id.spinnerMinute)
        spinnerAmPm = findViewById(R.id.spinnerAmPm)

        btnMinusPassenger = findViewById(R.id.btnMinusPassenger)
        btnPlusPassenger = findViewById(R.id.btnPlusPassenger)
        txtPassengerCount = findViewById(R.id.txtPassengerCount)
        txtCapacity = findViewById(R.id.txtCapacity)

        println(findViewById<TextView>(R.id.txtOccupied))
        println(findViewById<TextView>(R.id.txtReserved))
        println(findViewById<TextView>(R.id.txtAvailable))
        println(findViewById<Button>(R.id.btnManageSeats))

        txtOccupied = findViewById(R.id.txtOccupied)
        txtReserved = findViewById(R.id.txtReserved)
        txtAvailable = findViewById(R.id.txtAvailable)
        btnManageSeats = findViewById(R.id.btnManageSeats)

        progressPassengers = findViewById(R.id.progressPassengers)
        etNotes = findViewById(R.id.etNotes)
        btnNow = findViewById(R.id.btnNow)
        btnStartTrip = findViewById(R.id.btnStartTrip)
    }

    private fun setupSpinners() {
        spinnerShuttle.adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, listOf("XDY-6789", "ABC-1234", "XYZ-2025"))
        spinnerDestination.adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, listOf("Paseo de Sta. Rosa", "Balibago", "Nuvali", "Gate 3", "Main Campus"))
        spinnerTripType.adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, listOf("Regular Shuttle", "Express Route", "Inter-Campus", "Special Request"))

        val hoursList = (1..12).map { String.format("%02d", it) }
        spinnerHour.adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, hoursList)

        val minutesList = (0..55 step 5).map { String.format("%02d", it) }
        spinnerMinute.adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, minutesList)

        spinnerAmPm.adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, listOf("AM", "PM"))
    }

    private fun setupPassengerCounter() {
        btnPlusPassenger.setOnClickListener {
            if (passengerCount < maxPassengers) { passengerCount++; updatePassengerDisplay() }
        }
        btnMinusPassenger.setOnClickListener {
            if (passengerCount > 0) { passengerCount--; updatePassengerDisplay() }
        }
    }

    private fun updateSeatCounters() {

        txtOccupied.text =
            "🟢 Occupied: ${seatManager.occupiedCount()}"

        txtReserved.text =
            "🟡 Reserved: ${seatManager.reservedCount()}"

        txtAvailable.text =
            "🔴 Available: ${seatManager.availableCount()}"
    }

    private fun updatePassengerDisplay() {

        txtPassengerCount.text = passengerCount.toString()

        txtCapacity.text = "$passengerCount / $maxPassengers Seats"

        progressPassengers.progress = passengerCount

        seatManager.generateSeats(passengerCount)

        updateSeatCounters()
    }

    private fun showSeatDialog() {

        val view = layoutInflater.inflate(
            R.layout.dialog_manage_seats,
            null
        )

        val grid = view.findViewById<GridLayout>(R.id.gridSeats)

        seatManager.generateSeats(passengerCount)

        grid.removeAllViews()

        for (seat in seatManager.seats) {

            val button = Button(this)

            button.text = seat.id

            val params = GridLayout.LayoutParams()

            params.width = 0
            params.height = GridLayout.LayoutParams.WRAP_CONTENT
            params.columnSpec = GridLayout.spec(GridLayout.UNDEFINED, 1f)

            button.layoutParams = params

            params.setMargins(8, 8, 8, 8)

            button.minimumHeight = 120

            fun refreshSeat() {

                when (seat.status) {

                    SeatStatus.AVAILABLE -> {

                        button.text = "🔴 ${seat.id}"

                        button.backgroundTintList =
                            android.content.res.ColorStateList.valueOf(
                                Color.parseColor("#F44336")
                            )
                    }

                    SeatStatus.RESERVED -> {

                        button.text = "🟡 ${seat.id}"

                        button.backgroundTintList =
                            android.content.res.ColorStateList.valueOf(
                                Color.parseColor("#FFC107")
                            )
                    }

                    SeatStatus.OCCUPIED -> {

                        button.text = "🟢 ${seat.id}"

                        button.backgroundTintList =
                            android.content.res.ColorStateList.valueOf(
                                Color.parseColor("#4CAF50")
                            )
                    }
                }
            }

            refreshSeat()

            button.setOnClickListener {

                seatManager.toggleSeat(seat)

                refreshSeat()

                updateSeatCounters()
            }

            grid.addView(button)
        }

        AlertDialog.Builder(this)
            .setTitle("Manage Seat Occupation")
            .setView(view)
            .setPositiveButton("Done", null)
            .show()
    }

    private fun setupButtons() {
        btnManageSeats.setOnClickListener {

            showSeatDialog()

        }

        btnNow.setOnClickListener {

            val calendar = Calendar.getInstance()

            val isPm = calendar.get(Calendar.AM_PM) == Calendar.PM

            var hour = calendar.get(Calendar.HOUR)

            if (hour == 0) hour = 12

            val minute = calendar.get(Calendar.MINUTE)

            val roundedMinute = ((minute + 2) / 5) * 5

            var finalHour = hour
            var finalMinute = roundedMinute
            var finalIsPm = isPm

            if (roundedMinute == 60) {

                finalMinute = 0
                finalHour++

                if (finalHour == 12) {
                    finalIsPm = !finalIsPm
                }

                if (finalHour == 13)
                    finalHour = 1
            }

            spinnerHour.setSelection(finalHour - 1)

            spinnerMinute.setSelection(finalMinute / 5)

            spinnerAmPm.setSelection(if (finalIsPm) 1 else 0)
        }

        btnStartTrip.setOnClickListener {
            val shuttle = spinnerShuttle.selectedItem.toString()
            val destination = spinnerDestination.selectedItem.toString()
            val tripType = spinnerTripType.selectedItem.toString()

            val selectedTime = "${spinnerHour.selectedItem}:${spinnerMinute.selectedItem} ${spinnerAmPm.selectedItem}"
            val currentDateStr = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
            val fullDepartureTimestamp = "$currentDateStr $selectedTime"

            val dynamicTrip = TripLog(
                tripId = (TripManager.tripLogsList.size + 1).toString(),
                driverName = TripManager.currentDriverName,
                shuttleId = shuttle,
                departureTime = fullDepartureTimestamp,
                arrivalTime = "--",
                destination = destination,
                status = "In Progress",
                passengerCount = passengerCount,
                tripType = tripType,

                seats = seatManager.seats.toMutableList()
            )

            TripManager.addTrip(this, dynamicTrip)
            TripManager.currentTrip = dynamicTrip

            Toast.makeText(this, "Trip #${dynamicTrip.tripId} Successfully Logged!", Toast.LENGTH_SHORT).show()

            startActivity(Intent(this, DriverTripLogsActivity::class.java))
            finish()
        }
    }
}