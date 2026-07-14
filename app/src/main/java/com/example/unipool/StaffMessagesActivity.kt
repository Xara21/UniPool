package com.example.unipool

import android.content.Intent
import android.os.Bundle
import android.widget.LinearLayout
import android.widget.TextView
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.example.unipool.managers.MessageManager
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.navigation.NavigationView

class StaffMessagesActivity : AppCompatActivity() {

    private lateinit var layoutConversations: LinearLayout

    private lateinit var drawerLayout: DrawerLayout

    private lateinit var navigationView: NavigationView

    private lateinit var btnMenu: FloatingActionButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_staff_messages)

        drawerLayout =
            findViewById(R.id.drawerLayout)

        navigationView =
            findViewById(R.id.navigationView)

        btnMenu =
            findViewById(R.id.btnMenu)

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
                "STA001",
                driverId
            )

        txtTime.text =
            MessageManager.getLastTimestamp(
                "STA001",
                driverId
            )

        view.setOnClickListener {

            val intent = Intent(
                this,
                ChatActivity::class.java
            )

            intent.putExtra(
                "SENDER_ID",
                "STA001"
            )

            intent.putExtra(
                "SENDER_NAME",
                "Maria Staff"
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
                            StaffHomeActivity::class.java
                        )
                    )
                }

                R.id.nav_reserve -> {

                    startActivity(
                        Intent(
                            this,
                            StaffAvailableTripsActivity::class.java
                        )
                    )
                }

                R.id.nav_schedule -> {

                    startActivity(
                        Intent(
                            this,
                            StaffScheduleActivity::class.java
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
                            StaffHistoryActivity::class.java
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