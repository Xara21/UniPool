package com.example.unipool

import android.content.Intent
import android.os.Bundle
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.unipool.managers.MessageManager
import com.example.unipool.managers.TripManager
import androidx.activity.OnBackPressedCallback
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.navigation.NavigationView

class DriverMessagesActivity : AppCompatActivity() {

    private lateinit var layoutMessages: LinearLayout

    private lateinit var drawerLayout: DrawerLayout

    private lateinit var navigationView: NavigationView

    private lateinit var btnMenu: FloatingActionButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_driver_messages)

        drawerLayout = findViewById(R.id.drawerLayout)

        navigationView = findViewById(R.id.navigationView)

        btnMenu = findViewById(R.id.btnMenu)

        setupDrawer()

        layoutMessages = findViewById(R.id.layoutMessages)

        TripManager.loadFromStorage(this)

        loadPassengers()
    }

    override fun onResume() {
        super.onResume()

        TripManager.loadFromStorage(this)

        loadPassengers()
    }

    private fun loadPassengers() {

        layoutMessages.removeAllViews()

        val passengers = TripManager.tripLogsList

            .flatMap { trip -> trip.seats }

            .filter {

                it.passengerId != null &&
                        it.passengerName != null

            }

        if (passengers.isEmpty()) {

            val empty = TextView(this)

            empty.text = "No passengers yet."

            empty.textSize = 18f

            layoutMessages.addView(empty)

            return
        }

        passengers.forEach { seat ->

            val passengerId = seat.passengerId!!
            val passengerName = seat.passengerName!!

            val view = layoutInflater.inflate(
                R.layout.item_conversation,
                layoutMessages,
                false
            )

            val txtName =
                view.findViewById<TextView>(R.id.txtName)

            val txtRole =
                view.findViewById<TextView>(R.id.txtRole)

            val txtLastMessage =
                view.findViewById<TextView>(R.id.txtLastMessage)

            val txtTime =
                view.findViewById<TextView>(R.id.txtTime)

            txtName.text = passengerName

            txtRole.text =
                seat.passengerRole
                    ?.name
                    ?.lowercase()
                    ?.replaceFirstChar { it.uppercase() }
                    ?: "Passenger"

            txtLastMessage.text =
                MessageManager.getPreviewMessage(
                    "DRV001",
                    passengerId
                )

            txtTime.text =
                MessageManager.getLastTimestamp(
                    "DRV001",
                    passengerId
                )

            view.setOnClickListener {

                val intent = Intent(
                    this,
                    ChatActivity::class.java
                )

                intent.putExtra(
                    "SENDER_ID",
                    "DRV001"
                )

                intent.putExtra(
                    "SENDER_NAME",
                    "Driver"
                )

                intent.putExtra(
                    "RECEIVER_ID",
                    passengerId
                )

                intent.putExtra(
                    "RECEIVER_NAME",
                    passengerName
                )

                startActivity(intent)
            }

            layoutMessages.addView(view)
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
                            DriverHomeActivity::class.java
                        )
                    )
                }

                R.id.nav_departures -> {

                    startActivity(
                        Intent(
                            this,
                            DriverDeparturesActivity::class.java
                        )
                    )
                }

                R.id.nav_passengers -> {

                    startActivity(
                        Intent(
                            this,
                            DriverPassengersActivity::class.java
                        )
                    )
                }

                R.id.nav_triplogs -> {

                    startActivity(
                        Intent(
                            this,
                            DriverTripLogsActivity::class.java
                        )
                    )
                }

                R.id.nav_messages -> {

                    drawerLayout.closeDrawer(
                        GravityCompat.START
                    )
                }

                R.id.nav_profile -> {

                    startActivity(
                        Intent(
                            this,
                            DriverProfileActivity::class.java
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