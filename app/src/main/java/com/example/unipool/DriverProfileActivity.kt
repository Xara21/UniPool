package com.example.unipool

import android.content.Intent
import android.os.Bundle
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import android.widget.ImageView
import com.google.android.material.navigation.NavigationView

class DriverProfileActivity : AppCompatActivity() {

    private lateinit var drawerLayout: DrawerLayout

    private lateinit var navigationView: NavigationView

    private lateinit var btnMenu: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(
            R.layout.activity_driver_profile
        )

        drawerLayout =
            findViewById(
                R.id.drawerLayout
            )

        navigationView =
            findViewById(
                R.id.navigationView
            )

        btnMenu =
            findViewById(
                R.id.btnMenu
            )

        setupDrawer()
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

                    startActivity(

                        Intent(
                            this,
                            DriverMessagesActivity::class.java
                        )
                    )
                }

                R.id.nav_profile -> {

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

                    startActivity(
                        intent
                    )

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