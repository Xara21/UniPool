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
                senderId,
                senderName,
                receiverId,
                receiverName,
                text,
                SimpleDateFormat(
                    "yyyy-MM-dd HH:mm:ss",
                    Locale.getDefault()
                ).format(Date())
            )

            MessageManager.send(message)

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

            val tv = TextView(this)

            tv.text =
                "${msg.senderName}: ${msg.message}"

            tv.textSize = 18f

            tv.setPadding(10,10,10,10)

            layoutMessages.addView(tv)
        }
    }
}