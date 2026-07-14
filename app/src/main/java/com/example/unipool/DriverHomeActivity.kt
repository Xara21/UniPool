package com.example.unipool

import android.content.Intent
import android.os.Bundle
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.navigation.NavigationView

class DriverHomeActivity : AppCompatActivity() {

    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navigationView: NavigationView
    private lateinit var btnMenu: FloatingActionButton
    private lateinit var btnStartTrip: AppCompatButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_driver_home)

        drawerLayout = findViewById(R.id.drawerLayout)
        navigationView = findViewById(R.id.navigationView)
        btnMenu = findViewById(R.id.btnMenu)
        btnStartTrip = findViewById(R.id.btnStartTrip)

        // Open drawer
        btnMenu.setOnClickListener {
            drawerLayout.openDrawer(GravityCompat.START)
        }

        // Start New Trip
        btnStartTrip.setOnClickListener {
            startActivity(
                Intent(
                    this,
                    DriverDeparturesActivity::class.java
                )
            )
        }

        // Drawer menu
        navigationView.setNavigationItemSelectedListener { item ->

            when (item.itemId) {

                R.id.nav_home -> {
                    drawerLayout.closeDrawer(GravityCompat.START)
                    true
                }

                R.id.nav_departures -> {
                    startActivity(
                        Intent(
                            this,
                            DriverDeparturesActivity::class.java
                        )
                    )
                    drawerLayout.closeDrawer(GravityCompat.START)
                    true
                }

                R.id.nav_passengers -> {
                    startActivity(
                        Intent(
                            this,
                            DriverPassengersActivity::class.java
                        )
                    )
                    drawerLayout.closeDrawer(GravityCompat.START)
                    true
                }

                R.id.nav_triplogs -> {
                    startActivity(
                        Intent(
                            this,
                            DriverTripLogsActivity::class.java
                        )
                    )
                    drawerLayout.closeDrawer(GravityCompat.START)
                    true
                }

                // UPDATED
                R.id.nav_messages -> {

                    android.widget.Toast.makeText(
                        this,
                        "Driver messages clicked",
                        android.widget.Toast.LENGTH_SHORT
                    ).show()

                    startActivity(
                        Intent(
                            this,
                            DriverMessagesActivity::class.java
                        )
                    )

                    drawerLayout.closeDrawer(GravityCompat.START)

                    true
                }

                R.id.nav_profile -> {
                    startActivity(
                        Intent(
                            this,
                            DriverProfileActivity::class.java
                        )
                    )
                    drawerLayout.closeDrawer(GravityCompat.START)
                    true
                }

                R.id.nav_logout -> {

                    val intent =
                        Intent(
                            this,
                            LoginActivity::class.java
                        )

                    intent.flags =
                        Intent.FLAG_ACTIVITY_NEW_TASK or
                                Intent.FLAG_ACTIVITY_CLEAR_TASK

                    startActivity(intent)
                    finish()

                    true
                }

                else -> false
            }
        }

        // Android Back button
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
}