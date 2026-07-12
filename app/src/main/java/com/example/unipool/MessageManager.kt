package com.example.unipool

import com.example.unipool.models.Message

object MessageManager {

    val messages = mutableListOf<Message>()

    fun send(message: Message) {
        messages.add(message)
    }

    fun getConversation(user1: String, user2: String): List<Message> {

        return messages.filter {

            (it.senderId == user1 && it.receiverId == user2) ||
                    (it.senderId == user2 && it.receiverId == user1)

        }.sortedBy { it.timestamp }

    }

    fun getMessagesFor(receiverId: String): List<Message> {

        return messages.filter {

            it.receiverId == receiverId

        }.sortedBy { it.timestamp }

    }

    fun unreadCount(receiverId: String): Int {

        return messages.count {

            it.receiverId == receiverId &&
                    !it.isRead

        }

    }

    fun markConversationAsRead(user1: String, user2: String) {

        messages.forEach {

            if (it.receiverId == user1 &&
                it.senderId == user2) {

                it.isRead = true
            }
        }
    }
}