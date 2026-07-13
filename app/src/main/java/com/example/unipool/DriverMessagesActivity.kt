package com.example.unipool

import android.content.Intent
import android.os.Bundle
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.unipool.managers.MessageManager
class DriverMessagesActivity : AppCompatActivity() {

    private lateinit var layoutMessages: LinearLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_driver_messages)

        layoutMessages = findViewById(R.id.layoutMessages)

        loadPassengers()
    }

    override fun onResume() {
        super.onResume()

        loadPassengers()
    }

    private fun loadPassengers() {

        layoutMessages.removeAllViews()

        val conversations =
            MessageManager.getConversationPartners(
                "DRV001"
            )

        if (conversations.isEmpty()) {

            val empty = TextView(this)

            empty.text = "No conversations yet."

            empty.textSize = 18f

            layoutMessages.addView(empty)

            return
        }

        conversations.forEach { conversation ->

            val passengerId = conversation.first

            val passengerName = conversation.second

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
                when (passengerId) {

                    "STU001" -> "Student"

                    "STA001" -> "Staff"

                    else -> "Passenger"
                }

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
}