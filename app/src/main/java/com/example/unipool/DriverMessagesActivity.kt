package com.example.unipool

import android.content.Intent
import android.os.Bundle
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.unipool.managers.TripManager
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

        val trip = TripManager.currentTrip ?: return

        trip.seats.forEach { seat ->

            if (
                seat.passengerId != null &&
                seat.passengerName != null
            ) {

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

                txtName.text = seat.passengerName

                txtRole.text =
                    seat.passengerRole?.name
                        ?.lowercase()
                        ?.replaceFirstChar { it.uppercase() }

                txtLastMessage.text =
                    MessageManager.getPreviewMessage(
                        "DRV001",
                        seat.passengerId!!
                    )

                txtTime.text =
                    MessageManager.getLastTimestamp(
                        "DRV001",
                        seat.passengerId!!
                    )

                view.setOnClickListener {

                    val intent = Intent(
                        this,
                        ChatActivity::class.java
                    )

                    intent.putExtra("SENDER_ID", "DRV001")
                    intent.putExtra("SENDER_NAME", "Driver")

                    intent.putExtra("RECEIVER_ID", seat.passengerId)
                    intent.putExtra("RECEIVER_NAME", seat.passengerName)

                    startActivity(intent)
                }

                layoutMessages.addView(view)
            }
        }
    }
}