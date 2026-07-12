package com.example.unipool

import android.content.Intent
import android.os.Bundle
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.unipool.managers.TripManager

class DriverConversationsActivity : AppCompatActivity() {

    private lateinit var layoutConversations: LinearLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_driver_conversations)

        layoutConversations = findViewById(R.id.layoutConversations)

        loadPassengers()
    }

    private fun loadPassengers() {

        layoutConversations.removeAllViews()

        val trip = TripManager.currentTrip

        if (trip == null) {

            val empty = TextView(this)
            empty.text = "No active trip."
            empty.textSize = 18f

            layoutConversations.addView(empty)

            return
        }

        var hasPassengers = false

        trip.seats.forEach { seat ->

            if (
                seat.passengerId != null &&
                seat.passengerName != null
            ) {

                hasPassengers = true

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

                txtName.text = seat.passengerName

                txtRole.text =
                    seat.passengerRole
                        ?.name
                        ?.lowercase()
                        ?.replaceFirstChar {
                            it.uppercase()
                        }
                        ?: "Passenger"

                val conversation =
                    ConversationManager.getConversation(
                        seat.passengerId!!
                    )

                if (conversation != null) {

                    txtLastMessage.text =
                        conversation.lastMessage

                    txtTime.text =
                        java.text.SimpleDateFormat(
                            "hh:mm a",
                            java.util.Locale.getDefault()
                        ).format(
                            java.util.Date(
                                conversation.lastMessageTimestamp
                            )
                        )

                } else {

                    txtLastMessage.text = "Tap to chat"

                    txtTime.text = "--:--"

                }

                view.setOnClickListener {

                    val intent =
                        Intent(
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
                        seat.passengerId
                    )

                    intent.putExtra(
                        "RECEIVER_NAME",
                        seat.passengerName
                    )

                    startActivity(intent)
                }

                layoutConversations.addView(view)
            }
        }

        if (!hasPassengers) {

            val empty = TextView(this)

            empty.text = "No passengers reserved."

            empty.textSize = 18f

            layoutConversations.addView(empty)
        }
    }
}