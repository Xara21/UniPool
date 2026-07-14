package com.example.unipool.student

import android.content.Intent
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.ListView
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.example.unipool.LoginActivity
import com.example.unipool.NotificationManager
import com.example.unipool.PassengerAvailableTripsActivity
import com.example.unipool.R
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.navigation.NavigationView

class StudentNotificationsActivity : AppCompatActivity() {

    private lateinit var drawerLayout: DrawerLayout

    private lateinit var navigationView: NavigationView

    private lateinit var btnMenu: FloatingActionButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_student_notification)

        drawerLayout =
            findViewById(R.id.drawerLayout)

        navigationView =
            findViewById(R.id.navigationView)

        btnMenu =
            findViewById(R.id.btnMenu)

        setupDrawer()

        val listView =
            findViewById<ListView>(
                R.id.listNotifications
            )

        val adapter = ArrayAdapter(
            this,
            android.R.layout.simple_list_item_1,
            NotificationManager.getAll()
        )

        listView.adapter = adapter
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