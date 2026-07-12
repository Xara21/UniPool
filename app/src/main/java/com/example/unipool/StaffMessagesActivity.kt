package com.example.unipool

import android.content.Intent
import android.os.Bundle
import android.widget.LinearLayout
import android.widget.TextView
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

        val conversations = ConversationManager.getAll()

        if (conversations.isEmpty()) {

            val empty = TextView(this)

            empty.text = "No conversations yet."

            empty.textSize = 18f

            layoutConversations.addView(empty)

            return
        }

        conversations.forEach { conversation ->

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

            txtName.text = conversation.receiverName

            txtRole.text = "Driver"

            txtLastMessage.text = conversation.lastMessage

            if (conversation.lastMessageTimestamp == 0L) {

                txtTime.text = "--:--"

            } else {

                txtTime.text =
                    java.text.SimpleDateFormat(
                        "hh:mm a",
                        java.util.Locale.getDefault()
                    ).format(
                        java.util.Date(
                            conversation.lastMessageTimestamp
                        )
                    )
            }

            view.setOnClickListener {

                val intent = Intent(
                    this,
                    ChatActivity::class.java
                )

                intent.putExtra("SENDER_ID", "STF001")
                intent.putExtra("SENDER_NAME", "Maria Staff")

                intent.putExtra(
                    "RECEIVER_ID",
                    conversation.receiverId
                )

                intent.putExtra(
                    "RECEIVER_NAME",
                    conversation.receiverName
                )

                startActivity(intent)
            }

            layoutConversations.addView(view)
        }
    }
}