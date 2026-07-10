package com.example.unipool.student

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.example.unipool.LoginActivity
import com.example.unipool.R
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.navigation.NavigationView

class StudentHomeActivity : AppCompatActivity() {

    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navigationView: NavigationView

    private lateinit var btnMenu: FloatingActionButton

    private lateinit var txtStudentName: TextView

    private lateinit var btnReserveSeat: Button
    private lateinit var btnViewSchedule: Button
    private lateinit var btnMessages: Button
    private lateinit var btnHistory: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_student_home)

        initializeViews()

        setupDrawer()

        setupButtons()

        // Temporary demo user
        txtStudentName.text = "John Student"
    }

    private fun initializeViews() {

        drawerLayout = findViewById(R.id.drawerLayout)
        navigationView = findViewById(R.id.navigationView)

        btnMenu = findViewById(R.id.btnMenu)

        txtStudentName = findViewById(R.id.txtStudentName)

        btnReserveSeat = findViewById(R.id.btnReserveSeat)
        btnViewSchedule = findViewById(R.id.btnViewSchedule)
        btnMessages = findViewById(R.id.btnMessages)
        btnHistory = findViewById(R.id.btnHistory)
    }

    private fun setupButtons() {

        btnReserveSeat.setOnClickListener {

            startActivity(
                Intent(
                    this,
                    StudentReserveSeatActivity::class.java
                )
            )
        }

        btnViewSchedule.setOnClickListener {

            startActivity(
                Intent(
                    this,
                    StudentScheduleActivity::class.java
                )
            )
        }

        btnMessages.setOnClickListener {

            startActivity(
                Intent(
                    this,
                    StudentMessagesActivity::class.java
                )
            )
        }

        btnHistory.setOnClickListener {

            startActivity(
                Intent(
                    this,
                    StudentHistoryActivity::class.java
                )
            )
        }
    }

    private fun setupDrawer() {

        btnMenu.setOnClickListener {
            drawerLayout.openDrawer(GravityCompat.START)
        }

        navigationView.setNavigationItemSelectedListener { item ->

            when (item.itemId) {

                R.id.nav_home -> {

                    drawerLayout.closeDrawer(GravityCompat.START)
                }

                R.id.nav_reserve -> {

                    startActivity(
                        Intent(
                            this,
                            StudentReserveSeatActivity::class.java
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
}