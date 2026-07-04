package com.example.unipool

import android.content.Intent
import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.view.Gravity
import android.widget.TableLayout
import android.widget.TableRow
import android.widget.TextView
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.navigation.NavigationView
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class DriverTripLogsActivity : AppCompatActivity() {

    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navigationView: NavigationView
    private lateinit var btnMenu: FloatingActionButton
    private lateinit var tableTripLogs: TableLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_driver_trip_logs)

        drawerLayout = findViewById(R.id.drawerLayout)
        navigationView = findViewById(R.id.navigationView)
        btnMenu = findViewById(R.id.btnMenu)
        tableTripLogs = findViewById(R.id.tableTripLogs)

        setupDrawer()
        TripManager.loadFromStorage(this)
        populateTripLogsTable()
    }

    private fun setupDrawer() {
        btnMenu.setOnClickListener {
            drawerLayout.openDrawer(GravityCompat.START)
        }

        navigationView.setCheckedItem(R.id.nav_triplogs)

        navigationView.setNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> startActivity(Intent(this, DriverHomeActivity::class.java))
                R.id.nav_departures -> startActivity(Intent(this, DriverDeparturesActivity::class.java))
                R.id.nav_passengers -> startActivity(Intent(this, DriverPassengersActivity::class.java))
                R.id.nav_triplogs -> drawerLayout.closeDrawer(GravityCompat.START)
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

    private fun populateTripLogsTable() {
        if (tableTripLogs.childCount > 1) {
            tableTripLogs.removeViews(1, tableTripLogs.childCount - 1)
        }

        val tripsList = TripManager.tripLogsList

        for (trip in tripsList) {
            val row = TableRow(this).apply {
                setPadding(8, 8, 8, 8)
                layoutParams = TableLayout.LayoutParams(
                    TableLayout.LayoutParams.MATCH_PARENT,
                    TableLayout.LayoutParams.WRAP_CONTENT
                )

                isClickable = true
                focusable = TableRow.FOCUSABLE
                val outValue = android.util.TypedValue()
                theme.resolveAttribute(android.R.attr.selectableItemBackground, outValue, true)
                setBackgroundResource(outValue.resourceId)

                setOnClickListener {
                    showTripDetailsDialog(trip)
                }
            }

            row.addView(createTableCell(trip.tripId))
            row.addView(createTableCell(trip.driverName))
            row.addView(createTableCell(trip.shuttleId))
            row.addView(createTableCell(trip.departureTime))
            row.addView(createTableCell(trip.arrivalTime))
            row.addView(createTableCell(trip.destination))

            val statusCell = createTableCell(trip.status)
            if (trip.status.equals("In Progress", ignoreCase = true)) {
                statusCell.setTextColor(Color.parseColor("#E65100"))
                statusCell.setTypeface(null, Typeface.BOLD)
            } else if (trip.status.equals("Completed", ignoreCase = true)) {
                statusCell.setTextColor(Color.parseColor("#2E7D32"))
            }
            row.addView(statusCell)

            row.addView(createTableCell(trip.passengerCount.toString(), isNumeric = true))

            tableTripLogs.addView(row)
        }
    }

    private fun createTableCell(text: String, isNumeric: Boolean = false): TextView {
        return TextView(this).apply {
            this.text = text
            setTextColor(Color.BLACK)
            setPadding(24, 16, 24, 16)
            textSize = 14f
            gravity = if (isNumeric) Gravity.CENTER else Gravity.START
        }
    }

    private fun showTripDetailsDialog(trip: TripLog) {
        val detailsMessage = """
            📍 Destination: ${trip.destination}
            📋 Trip Type: ${trip.tripType}
            👤 Driver Name: ${trip.driverName}
            🚌 Shuttle Number: ${trip.shuttleId}
            ⏱️ Departure: ${trip.departureTime}
            🏁 Arrival Time: ${trip.arrivalTime}
            🚦 Current Status: ${trip.status}
            👥 Total Passengers: ${trip.passengerCount} / 30
        """.trimIndent()

        val dialogBuilder = androidx.appcompat.app.AlertDialog.Builder(this)
            .setTitle("Trip Summary — ID #${trip.tripId}")
            .setMessage(detailsMessage)
            .setNegativeButton("Close") { dialog, _ ->
                dialog.dismiss()
            }

        if (trip.status.equals("In Progress", ignoreCase = true)) {
            dialogBuilder.setPositiveButton("Complete Trip") { dialog, _ ->
                val currentTime = SimpleDateFormat("hh:mm a", Locale.getDefault()).format(Date())
                val index = TripManager.tripLogsList.indexOfFirst { it.tripId == trip.tripId }

                if (index != -1) {
                    val updatedTrip = TripManager.tripLogsList[index].copy(
                        status = "Completed",
                        arrivalTime = currentTime
                    )
                    TripManager.updateTrip(this, index, updatedTrip)
                    populateTripLogsTable()
                    Toast.makeText(this, "Trip #${trip.tripId} marked as Completed!", Toast.LENGTH_SHORT).show()
                }
                dialog.dismiss()
            }
        }

        dialogBuilder.create().show()
    }
}