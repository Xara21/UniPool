package com.example.unipool.managers

import com.example.unipool.models.Message

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

    fun getConversationPartners(
        currentUserId: String
    ): List<String> {

        return messages.flatMap {

            listOf(it.senderId, it.receiverId)

        }.filter {

            it != currentUserId

        }.distinct()
    }

    fun clearAll() {

        messages.clear()

    }

}