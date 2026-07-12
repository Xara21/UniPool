package com.example.unipool

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import com.example.unipool.managers.TripManager

class ConversationListActivity : AppCompatActivity() {

    private lateinit var layoutConversations: LinearLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_conversations)

        layoutConversations = findViewById(R.id.layoutConversations)

        val isDriver = intent.getBooleanExtra("IS_DRIVER", false)
        val isStaff = intent.getBooleanExtra("IS_STAFF", false)

        if (isDriver) {

            val trip = TripManager.currentTrip

            trip?.seats?.forEach { seat ->

                if (
                    seat.passengerId != null &&
                    seat.passengerName != null
                ) {

                    addConversation(
                        receiverName = seat.passengerName!!,
                        receiverId = seat.passengerId!!,
                        senderName = "Driver",
                        senderId = "DRV001"
                    )
                }
            }

        } else {

            if (isStaff) {

                addConversation(
                    receiverName = "Driver",
                    receiverId = "DRV001",
                    senderName = "Maria Staff",
                    senderId = "STA001"
                )

            } else {

                addConversation(
                    receiverName = "Driver",
                    receiverId = "DRV001",
                    senderName = "John Student",
                    senderId = "STU001"
                )
            }
        }
    }

    private fun addConversation(
        receiverName: String,
        receiverId: String,
        senderName: String,
        senderId: String
    ) {

        val button = Button(this)

        button.text = receiverName

        button.setOnClickListener {

            val intent = Intent(this, ChatActivity::class.java)

            intent.putExtra("SENDER_ID", senderId)
            intent.putExtra("SENDER_NAME", senderName)

            intent.putExtra("RECEIVER_ID", receiverId)
            intent.putExtra("RECEIVER_NAME", receiverName)

            startActivity(intent)
        }

        layoutConversations.addView(button)
    }
}