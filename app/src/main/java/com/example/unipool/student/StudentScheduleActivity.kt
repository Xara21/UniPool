package com.example.unipool.student

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.example.unipool.LoginActivity
import com.example.unipool.NotificationManager
import com.example.unipool.PassengerAvailableTripsActivity
import com.example.unipool.R
import com.example.unipool.managers.TripManager
import com.example.unipool.models.SeatStatus
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.navigation.NavigationView

class StudentScheduleActivity : AppCompatActivity() {

    private lateinit var txtTitle: TextView
    private lateinit var txtSubtitle: TextView
    private lateinit var btnCancelReservation: Button

    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navigationView: NavigationView
    private lateinit var btnMenu: FloatingActionButton

    private val studentId = "STU001"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_student_placeholder)

        drawerLayout = findViewById(R.id.drawerLayout)
        navigationView = findViewById(R.id.navigationView)
        btnMenu = findViewById(R.id.btnMenu)

        txtTitle = findViewById(R.id.txtTitle)
        txtSubtitle = findViewById(R.id.txtSubtitle)

        btnCancelReservation =
            findViewById(R.id.btnCancelReservation)

        setupDrawer()

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

    private fun setupDrawer() {

        btnMenu.setOnClickListener {

            drawerLayout.openDrawer(
                GravityCompat.START
            )
        }

        navigationView.setNavigationItemSelectedListener { item ->

            when (item.itemId) {

                R.id.nav_home -> {

                    startActivity(
                        Intent(
                            this,
                            StudentHomeActivity::class.java
                        )
                    )
                }

                R.id.nav_reserve -> {

                    startActivity(
                        Intent(
                            this,
                            PassengerAvailableTripsActivity::class.java
                        )
                    )
                }

                R.id.nav_schedule -> {

                    drawerLayout.closeDrawer(
                        GravityCompat.START
                    )
                }

                R.id.nav_messages -> {

                    startActivity(
                        Intent(
                            this,
                            StudentMessagesActivity::class.java
                        )
                    )
                }

                R.id.nav_history -> {

                    startActivity(
                        Intent(
                            this,
                            StudentHistoryActivity::class.java
                        )
                    )
                }

                R.id.nav_logout -> {

                    val intent = Intent(
                        this,
                        LoginActivity::class.java
                    )

                    intent.flags =
                        Intent.FLAG_ACTIVITY_NEW_TASK or
                                Intent.FLAG_ACTIVITY_CLEAR_TASK

                    startActivity(intent)

                    finish()
                }
            }

            drawerLayout.closeDrawer(
                GravityCompat.START
            )

            true
        }

        onBackPressedDispatcher.addCallback(
            this,
            object : OnBackPressedCallback(true) {

                override fun handleOnBackPressed() {

                    if (
                        drawerLayout.isDrawerOpen(
                            GravityCompat.START
                        )
                    ) {

                        drawerLayout.closeDrawer(
                            GravityCompat.START
                        )

                    } else {

                        finish()
                    }
                }
            }
        )
    }
}