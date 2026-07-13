package com.example.unipool

import android.content.Intent
import android.os.Bundle
import android.widget.LinearLayout
import android.widget.TextView
import com.example.unipool.managers.MessageManager
import androidx.appcompat.app.AppCompatActivity

class StaffMessagesActivity : AppCompatActivity() {

    private lateinit var layoutConversations: LinearLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_staff_messages)

        layoutConversations = findViewById(R.id.layoutConversations)

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

        MessageManager.getPreviewMessage(
            "STA001",
            driverId
        )

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
}