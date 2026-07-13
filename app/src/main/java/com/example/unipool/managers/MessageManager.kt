package com.example.unipool.managers

import com.example.unipool.models.Message
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object MessageManager {

    private val messages = mutableListOf<Message>()

    fun sendMessage(message: Message) {
        messages.add(message)
    }

    fun getConversation(
        user1: String,
        user2: String
    ): List<Message> {

        return messages.filter {

            (it.senderId == user1 && it.receiverId == user2) ||

                    (it.senderId == user2 && it.receiverId == user1)

        }.sortedBy {

            it.timestamp

        }
    }

    fun getLastMessage(
        user1: String,
        user2: String
    ): Message? {

        return getConversation(
            user1,
            user2
        ).lastOrNull()

    }

    fun getPreviewMessage(
        user1: String,
        user2: String
    ): String {

        val last = getLastMessage(user1, user2)

        return if (last == null) {

            "Tap to chat"

        } else {

            "${last.senderName}: ${last.message}"

        }

    }

    fun getLastTimestamp(
        user1: String,
        user2: String
    ): String {

        val last = getLastMessage(user1, user2)

        if (last == null)
            return "--:--"

        val formatter = SimpleDateFormat(
            "h:mm a",
            Locale.getDefault()
        )

        return formatter.format(
            Date(last.timestamp)
        )
    }

    fun getMessageCount(): Int {
        return messages.size
    }
}