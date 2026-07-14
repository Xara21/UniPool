package com.example.unipool.student

import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.example.unipool.LoginActivity
import com.example.unipool.PassengerAvailableTripsActivity
import com.example.unipool.R
import com.example.unipool.managers.TripManager
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.navigation.NavigationView

class StudentHistoryActivity : AppCompatActivity() {

    private lateinit var drawerLayout: DrawerLayout

    private lateinit var navigationView: NavigationView

    private lateinit var btnMenu: FloatingActionButton

    private val currentStudentId = "STU001"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_student_placeholder)

        drawerLayout =
            findViewById(R.id.drawerLayout)

        navigationView =
            findViewById(R.id.navigationView)

        btnMenu =
            findViewById(R.id.btnMenu)

        setupDrawer()

        TripManager.loadFromStorage(this)

        val txtTitle =
            findViewById<TextView>(R.id.txtTitle)

        val txtSubtitle =
            findViewById<TextView>(R.id.txtSubtitle)

        txtTitle.text = "Trip History"

        val completedTrips = TripManager.tripLogsList.filter { trip ->

            trip.status == "Completed" &&

                    trip.seats.any { seat ->

                        seat.passengerId == currentStudentId
                    }
        }

        if (completedTrips.isEmpty()) {

            txtSubtitle.text =
                "You have no completed trips yet."

        } else {

            val historyText = StringBuilder()

            completedTrips.forEach { trip ->

                historyText.append(

                    """
                    🚍 Trip #${trip.tripId}
                    Driver: ${trip.driverName}
                    Destination: ${trip.destination}
                    Departure: ${trip.departureTime}
                    Arrival: ${trip.arrivalTime}
                    Status: ${trip.status}
                    
                    -------------------------
                    
                    """.trimIndent()

                )

                historyText.append("\n\n")
            }

            txtSubtitle.text =
                historyText.toString()
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

                    startActivity(
                        Intent(
                            this,
                            StudentScheduleActivity::class.java
                        )
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

                    drawerLayout.closeDrawer(
                        GravityCompat.START
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