package com.example.unipool.student

import android.content.Intent
import android.os.Bundle
import android.widget.LinearLayout
import android.widget.TextView
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.example.unipool.ChatActivity
import com.example.unipool.LoginActivity
import com.example.unipool.PassengerAvailableTripsActivity
import com.example.unipool.R
import com.example.unipool.managers.MessageManager
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.navigation.NavigationView

class StudentMessagesActivity : AppCompatActivity() {

    private lateinit var layoutConversations: LinearLayout

    private lateinit var drawerLayout: DrawerLayout

    private lateinit var navigationView: NavigationView

    private lateinit var btnMenu: FloatingActionButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_student_messages)

        drawerLayout = findViewById(R.id.drawerLayout)

        navigationView = findViewById(R.id.navigationView)

        btnMenu = findViewById(R.id.btnMenu)

        layoutConversations =
            findViewById(R.id.layoutConversations)

        setupDrawer()

        loadConversations()
    }

    override fun onResume() {
        super.onResume()

        loadConversations()
    }

    private fun loadConversations() {

        layoutConversations.removeAllViews()

        val driverId = "DRV001"

        val driverName = "Driver"

        val view = layoutInflater.inflate(
            R.layout.item_conversation,
            layoutConversations,
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

        txtName.text = driverName

        txtRole.text = "Driver"

        txtLastMessage.text =
            MessageManager.getPreviewMessage(
                "STU001",
                driverId
            )

        txtTime.text =
            MessageManager.getLastTimestamp(
                "STU001",
                driverId
            )

        view.setOnClickListener {

            val intent = Intent(
                this,
                ChatActivity::class.java
            )

            intent.putExtra(
                "SENDER_ID",
                "STU001"
            )

            intent.putExtra(
                "SENDER_NAME",
                "John Student"
            )

            intent.putExtra(
                "RECEIVER_ID",
                driverId
            )

            intent.putExtra(
                "RECEIVER_NAME",
                driverName
            )

            startActivity(intent)
        }

        layoutConversations.addView(view)
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

                    drawerLayout.closeDrawer(
                        GravityCompat.START
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