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
import android.content.Intent
import androidx.activity.OnBackPressedCallback
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.navigation.NavigationView
import androidx.appcompat.app.AppCompatActivity
import com.example.unipool.models.TripLog
import com.example.unipool.models.SeatStatus

class DriverPassengersActivity : AppCompatActivity() {

    private lateinit var panelStatusCard: LinearLayout

    private lateinit var drawerLayout: DrawerLayout

    private lateinit var navigationView: NavigationView
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

    override fun onCreate(savedInstanceState: Bundle?) {

            super.onCreate(savedInstanceState)

            setContentView(R.layout.activity_driver_passengers)

            initializeViews()

            setupDrawer()

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

    private fun setupDrawer() {

        btnHamburger.setOnClickListener {
            drawerLayout.openDrawer(GravityCompat.START)
        }

        navigationView.setCheckedItem(R.id.nav_passengers)

        navigationView.setNavigationItemSelectedListener { item ->

            when (item.itemId) {

                R.id.nav_home ->
                    startActivity(Intent(this, DriverHomeActivity::class.java))

                R.id.nav_departures ->
                    startActivity(Intent(this, DriverDeparturesActivity::class.java))

                R.id.nav_passengers ->
                    drawerLayout.closeDrawer(GravityCompat.START)

                R.id.nav_triplogs ->
                    startActivity(Intent(this, DriverTripLogsActivity::class.java))

                R.id.nav_messages ->
                    startActivity(Intent(this, DriverMessagesActivity::class.java))

                R.id.nav_profile ->
                    startActivity(Intent(this, DriverProfileActivity::class.java))

                R.id.nav_logout -> {

                    val intent = Intent(this, LoginActivity::class.java).apply {
                        flags =
                            Intent.FLAG_ACTIVITY_NEW_TASK or
                                    Intent.FLAG_ACTIVITY_CLEAR_TASK
                    }

                    startActivity(intent)
                    finish()
                }
            }

            drawerLayout.closeDrawer(GravityCompat.START)

            true
        }

        onBackPressedDispatcher.addCallback(
            this,
            object : OnBackPressedCallback(true) {

                override fun handleOnBackPressed() {

                    if (drawerLayout.isDrawerOpen(GravityCompat.START)) {

                        drawerLayout.closeDrawer(GravityCompat.START)

                    } else {

                        finish()
                    }
                }
            }
        )
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

        val seatMap = trip.seats.associateBy { it.id }

        for (seat in trip.seats) {

            if (
                seat.status != SeatStatus.OCCUPIED &&
                seat.status != SeatStatus.RESERVED
            ) {
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

            txtPassenger.text =
                seat.passengerName ?: "Unknown Passenger"

            txtPassenger.setPadding(8,16,8,16)

            val txtSeat = TextView(this)
            val txtStatus = TextView(this)

            txtStatus.layoutParams =
                TableRow.LayoutParams(
                    0,
                    TableRow.LayoutParams.WRAP_CONTENT,
                    1f
                )

            txtStatus.gravity = Gravity.CENTER

            txtStatus.text =
                when (seat.status) {
                    SeatStatus.RESERVED -> "Reserved"
                    SeatStatus.OCCUPIED -> "On Board"
                    else -> "-"
                }

            txtSeat.layoutParams =
                TableRow.LayoutParams(
                    0,
                    TableRow.LayoutParams.WRAP_CONTENT,
                    1f
                )

            txtSeat.gravity = Gravity.CENTER

            txtSeat.text = seat.id

            val btnAction = Button(this)

            btnAction.layoutParams =
                TableRow.LayoutParams(
                    0,
                    TableRow.LayoutParams.WRAP_CONTENT,
                    1f
                )

            if (seat.status == SeatStatus.RESERVED) {

                btnAction.text = "Approve"

                btnAction.setOnClickListener {

                    AlertDialog.Builder(this)
                        .setTitle("Passenger Boarding")
                        .setMessage(
                            "Confirm that ${seat.passengerName} has boarded the shuttle?"
                        )
                        .setPositiveButton("Confirm") { _, _ ->

                            seat.status = SeatStatus.OCCUPIED

                            TripManager.saveToStorage(this)

                            refreshPassengerManifest()
                            refreshSeatCounters()

                            Toast.makeText(
                                this,
                                "${seat.passengerName} is now On Board.",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                        .setNegativeButton("Cancel", null)
                        .show()
                }

            } else {

                btnAction.text = "Notify"

                btnAction.setOnClickListener {

                    AlertDialog.Builder(this)
                        .setTitle("Notify Passenger")
                        .setMessage(
                            "Send boarding reminder to ${seat.passengerName}?"
                        )
                        .setPositiveButton("Send") { _, _ ->

                            Toast.makeText(
                                this,
                                "Boarding reminder sent.",
                                Toast.LENGTH_SHORT
                            ).show()

                        }
                        .setNegativeButton("Cancel", null)
                        .show()
                }
            }

            row.addView(txtPassenger)
            row.addView(txtSeat)
            row.addView(txtStatus)
            row.addView(btnAction)

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

            if (TripManager.tripLogsList.isEmpty()) {

                Toast.makeText(
                    this,
                    "No trips available.",
                    Toast.LENGTH_SHORT
                ).show()

                return@setOnClickListener
            }

            val tripDescriptions =
                TripManager.tripLogsList.map {

                    "Trip #${it.tripId}\n" +
                            "${it.destination}\n" +
                            "${it.departureTime}\n" +
                            "Status: ${it.status}"

                }.toTypedArray()

            AlertDialog.Builder(this)
                .setTitle("Select Trip")
                .setItems(tripDescriptions) { _, which ->

                    currentTrip =
                        TripManager.tripLogsList[which]

                    TripManager.currentTrip =
                        currentTrip

                    loadTripInformation()

                    refreshSeatCounters()

                    Toast.makeText(
                        this,
                        "Trip #${currentTrip!!.tripId} selected.",
                        Toast.LENGTH_SHORT
                    ).show()
                }
                .show()
        }

        btnActionTrip.setOnClickListener {

            val trip = currentTrip ?: return@setOnClickListener

            when (trip.status) {

                "Scheduled" -> {
                    trip.status = "In Progress"
                }

                "In Progress" -> {

                    val occupied =
                        trip.seats.count { it.status == SeatStatus.OCCUPIED }

                    val reserved =
                        trip.seats.count { it.status == SeatStatus.RESERVED }

                    val available =
                        trip.seats.count { it.status == SeatStatus.AVAILABLE }

                    AlertDialog.Builder(this)
                        .setTitle("Complete Trip?")
                        .setMessage(
                            """
Driver: ${trip.driverName}

Destination: ${trip.destination}

Occupied: $occupied
Reserved: $reserved
Available: $available

This action cannot be undone.
            """.trimIndent()
                        )
                        .setNegativeButton("Cancel", null)

                        .setPositiveButton("Complete Trip") { _, _ ->

                            trip.status = "Completed"

                            txtCurrentStatus.text = trip.status

                            refreshStatusCard()

                            TripManager.saveToStorage(this)

                            Toast.makeText(
                                this,
                                "Trip completed successfully.",
                                Toast.LENGTH_SHORT
                            ).show()
                        }

                        .show()

                    return@setOnClickListener
                }

                else -> return@setOnClickListener
            }

            refreshStatusCard()

            TripManager.saveToStorage(this)
        }
    }

    private fun showSeatDialog() {

        val trip = currentTrip ?: return

        val scrollView = ScrollView(this)

        val container = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(24,24,24,24)
        }

        scrollView.addView(container)

        val seatRows = mutableListOf<LinearLayout>()

        repeat(4) {

            seatRows.add(
                LinearLayout(this).apply {

                    orientation = LinearLayout.HORIZONTAL

                    gravity = Gravity.CENTER

                    setPadding(0,16,0,16)

                }
            )
        }

        for (seat in trip.seats) {

            val button = Button(this)

            button.layoutParams =
                GridLayout.LayoutParams().apply {
                    width = 150
                    height = 150
                    setMargins(10,10,10,10)
                }

            fun refreshSeatButton() {

                val subtitle =
                    when (seat.status) {

                        SeatStatus.AVAILABLE ->
                            "Available"

                        SeatStatus.RESERVED ->
                            seat.passengerName ?: "Reserved"

                        SeatStatus.OCCUPIED ->
                            seat.passengerName ?: "Occupied"
                    }

                button.text =
                    when (seat.status) {

                        SeatStatus.AVAILABLE ->
                            "🔴\n${seat.id}\n$subtitle"

                        SeatStatus.RESERVED ->
                            "🟡\n${seat.id}\n$subtitle"

                        SeatStatus.OCCUPIED ->
                            "🟢\n${seat.id}\n$subtitle"
                    }

                button.textSize = 10f

                when (seat.status) {

                    SeatStatus.AVAILABLE ->
                        button.setBackgroundColor(Color.parseColor("#F44336"))

                    SeatStatus.RESERVED ->
                        button.setBackgroundColor(Color.parseColor("#FFC107"))

                    SeatStatus.OCCUPIED ->
                        button.setBackgroundColor(Color.parseColor("#4CAF50"))
                }

                trip.passengerCount =
                    trip.seats.count {
                        it.status == SeatStatus.OCCUPIED
                    }
            }

            refreshSeatButton()

            button.setOnClickListener {

                when (seat.status) {

                    SeatStatus.AVAILABLE -> {

                        Toast.makeText(
                            this,
                            "Students reserve seats themselves.",
                            Toast.LENGTH_SHORT
                        ).show()
                    }

                    SeatStatus.RESERVED,
                    SeatStatus.OCCUPIED -> {

                        AlertDialog.Builder(this)
                            .setTitle("Clear Seat?")
                            .setMessage(
                                "Remove ${seat.passengerName ?: "this passenger"} from ${seat.id}?"
                            )
                            .setPositiveButton("Yes") { _, _ ->

                                seat.status = SeatStatus.AVAILABLE
                                seat.passengerId = null
                                seat.passengerName = null
                                seat.passengerRole = null

                                TripManager.saveToStorage(this)

                                refreshSeatButton()
                                refreshPassengerManifest()
                                refreshSeatCounters()
                            }
                            .setNegativeButton("Cancel", null)
                            .show()
                    }
                }

                refreshSeatButton()

                refreshPassengerManifest()

                refreshSeatCounters()

                TripManager.saveToStorage(this)


            }

            val rowIndex =
                when (seat.id.first()) {

                    'A' -> 0
                    'B' -> 1
                    'C' -> 2
                    else -> 3
                }

            seatRows[rowIndex].addView(button)

            if (seat.id.endsWith("2")) {

                val aisle = TextView(this).apply {

                    text = "     "

                    width = 90
                }

                seatRows[rowIndex].addView(aisle)
            }
        }

        val title = TextView(this).apply {

            text = "🚌 Shuttle Seat Layout"

            textSize = 18f

            gravity = Gravity.CENTER

            setPadding(0,0,0,24)
        }

        container.addView(title)

        for (row in seatRows) {
            container.addView(row)
        }

        AlertDialog.Builder(this)
            .setTitle("Seat Occupation")
            .setView(scrollView)
            .setPositiveButton("Done") { _, _ ->

                TripManager.saveToStorage(this)

                loadTripInformation()

            }
            .show()
    }

    private fun initializeViews() {

        drawerLayout =
            findViewById(R.id.drawerLayout)

        navigationView =
            findViewById(R.id.navigationView)

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
        refreshStatusCard()
    }

    private fun refreshStatusCard() {

        val trip = currentTrip ?: return

        txtCurrentStatus.text = trip.status

        when (trip.status) {

            "Scheduled" -> {

                panelStatusCard.setBackgroundColor(
                    Color.parseColor("#FFEB3B")
                )

                btnActionTrip.text = "Start Trip"
                btnActionTrip.isEnabled = true
            }

            "In Progress" -> {

                panelStatusCard.setBackgroundColor(
                    Color.parseColor("#4CAF50")
                )

                btnActionTrip.text = "End Trip"
                btnActionTrip.isEnabled = true
            }

            "Completed" -> {

                panelStatusCard.setBackgroundColor(
                    Color.parseColor("#2196F3")
                )

                btnActionTrip.text = "Trip Completed"
                btnActionTrip.isEnabled = false
            }
        }
    }

}

