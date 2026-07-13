package com.example.unipool

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.unipool.models.Message
import java.text.SimpleDateFormat
import java.util.*
import com.example.unipool.managers.MessageManager

class ChatActivity : AppCompatActivity() {

    private lateinit var txtTitle: TextView
    private lateinit var layoutMessages: LinearLayout
    private lateinit var etMessage: EditText
    private lateinit var btnSend: Button

    private lateinit var senderId: String
    private lateinit var senderName: String
    private lateinit var receiverId: String
    private lateinit var receiverName: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_chat)

        txtTitle = findViewById(R.id.txtChatTitle)
        layoutMessages = findViewById(R.id.layoutMessages)
        etMessage = findViewById(R.id.etMessage)
        btnSend = findViewById(R.id.btnSend)

        senderId = intent.getStringExtra("SENDER_ID") ?: ""
        senderName = intent.getStringExtra("SENDER_NAME") ?: ""

        receiverId = intent.getStringExtra("RECEIVER_ID") ?: ""
        receiverName = intent.getStringExtra("RECEIVER_NAME") ?: ""

        txtTitle.text = receiverName

        loadConversation()

        btnSend.setOnClickListener {

            val text = etMessage.text.toString().trim()

            if (text.isEmpty()) return@setOnClickListener

            val message = Message(
                senderId = senderId,
                senderName = senderName,
                receiverId = receiverId,
                receiverName = receiverName,
                message = text
            )

            MessageManager.sendMessage(message)

            android.widget.Toast.makeText(
                this,
                "Saved: ${message.senderId} -> ${message.receiverId}",
                android.widget.Toast.LENGTH_LONG
            ).show()

            ConversationManager.updateConversation(

                receiverId = receiverId,

                receiverName = receiverName,

                receiverRole = null,

                lastMessage = text,

                timestamp = message.timestamp

            )

            etMessage.text.clear()

            loadConversation()
        }
    }

    private fun loadConversation() {

        layoutMessages.removeAllViews()

        val conversation =
            MessageManager.getConversation(
                senderId,
                receiverId
            )

        for (msg in conversation) {

            val isMine = msg.senderId == senderId

            val layoutId =
                if (isMine)
                    R.layout.item_chat_right
                else
                    R.layout.item_chat_left

            val bubble = layoutInflater.inflate(
                layoutId,
                layoutMessages,
                false
            )

            val txtMessage =
                bubble.findViewById<TextView>(R.id.txtMessage)

            val txtTime =
                bubble.findViewById<TextView>(R.id.txtTime)

            txtMessage.text = msg.message

            txtTime.text =
                formatTimestamp(msg.timestamp)

            layoutMessages.addView(bubble)
        }

        layoutMessages.post {

            if (layoutMessages.parent is android.widget.ScrollView) {

                (layoutMessages.parent as android.widget.ScrollView)
                    .fullScroll(android.view.View.FOCUS_DOWN)
            }

        }
    }

    private fun formatTimestamp(timestamp: Long): String {

        val sdf = SimpleDateFormat(
            "h:mm a",
            Locale.getDefault()
        )

        return sdf.format(Date(timestamp))
    }
}