package com.example.unipool

import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.navigation.NavigationView
import java.text.SimpleDateFormat
import java.util.*

class DriverDeparturesActivity : AppCompatActivity() {

    // Drawer
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navigationView: NavigationView
    private lateinit var btnMenu: FloatingActionButton

    // Existing Views
    private lateinit var spinnerShuttle: Spinner
    private lateinit var spinnerDestination: Spinner

    private lateinit var btnMinusPassenger: Button
    private lateinit var btnPlusPassenger: Button

    private lateinit var txtPassengerCount: TextView
    private lateinit var txtCapacity: TextView

    private lateinit var progressPassengers: ProgressBar

    private lateinit var etDepartureTime: EditText
    private lateinit var etNotes: EditText

    private lateinit var btnNow: Button
    private lateinit var btnStartTrip: Button

    private var passengerCount = 0
    private val maxPassengers = 30

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_driver_departures)

        // Drawer
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

                R.id.nav_home -> {
                    startActivity(Intent(this, DriverHomeActivity::class.java))
                }

                R.id.nav_departures -> {
                    drawerLayout.closeDrawer(GravityCompat.START)
                }

                R.id.nav_passengers -> {
                    startActivity(Intent(this, DriverPassengersActivity::class.java))
                }

                R.id.nav_triplogs -> {
                    startActivity(Intent(this, DriverTripLogsActivity::class.java))
                }

                R.id.nav_messages -> {
                    startActivity(Intent(this, DriverMessagesActivity::class.java))
                }

                R.id.nav_profile -> {
                    startActivity(Intent(this, DriverProfileActivity::class.java))
                }

                R.id.nav_logout -> {

                    val intent = Intent(this, LoginActivity::class.java)
                    intent.flags =
                        Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK

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

    private fun initializeViews() {

        spinnerShuttle = findViewById(R.id.spinnerShuttle)
        spinnerDestination = findViewById(R.id.spinnerDestination)

        btnMinusPassenger = findViewById(R.id.btnMinusPassenger)
        btnPlusPassenger = findViewById(R.id.btnPlusPassenger)

        txtPassengerCount = findViewById(R.id.txtPassengerCount)
        txtCapacity = findViewById(R.id.txtCapacity)

        progressPassengers = findViewById(R.id.progressPassengers)

        etDepartureTime = findViewById(R.id.etDepartureTime)
        etNotes = findViewById(R.id.etNotes)

        btnNow = findViewById(R.id.btnNow)
        btnStartTrip = findViewById(R.id.btnStartTrip)
    }

    private fun setupSpinners() {

        val shuttles = listOf(
            "XDY-6789",
            "ABC-1234",
            "XYZ-2025"
        )

        spinnerShuttle.adapter = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_dropdown_item,
            shuttles
        )

        val destinations = listOf(
            "Paseo de Sta. Rosa",
            "Balibago",
            "Nuvali",
            "Gate 3",
            "Main Campus"
        )

        spinnerDestination.adapter = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_dropdown_item,
            destinations
        )
    }

    private fun setupPassengerCounter() {

        btnPlusPassenger.setOnClickListener {

            if (passengerCount < maxPassengers) {
                passengerCount++
                updatePassengerDisplay()
            }

        }

        btnMinusPassenger.setOnClickListener {

            if (passengerCount > 0) {
                passengerCount--
                updatePassengerDisplay()
            }

        }
    }

    private fun updatePassengerDisplay() {

        txtPassengerCount.text = passengerCount.toString()
        txtCapacity.text = "$passengerCount / $maxPassengers Seats"
        progressPassengers.progress = passengerCount
    }

    private fun setupButtons() {

        btnNow.setOnClickListener {

            val sdf = SimpleDateFormat("hh:mm a", Locale.getDefault())
            etDepartureTime.setText(sdf.format(Date()))

        }

        btnStartTrip.setOnClickListener {

            val shuttle = spinnerShuttle.selectedItem.toString()
            val destination = spinnerDestination.selectedItem.toString()
            val departure = etDepartureTime.text.toString()

            if (departure.isEmpty()) {

                Toast.makeText(
                    this,
                    "Please enter departure time.",
                    Toast.LENGTH_SHORT
                ).show()

                return@setOnClickListener
            }

            Toast.makeText(
                this,
                "Trip Started!\n$shuttle\n$destination",
                Toast.LENGTH_LONG
            ).show()
        }
    }
}