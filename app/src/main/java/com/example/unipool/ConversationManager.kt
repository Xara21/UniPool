package com.example.unipool

import com.example.unipool.models.Conversation
import com.example.unipool.models.PassengerRole

object ConversationManager {

    private val conversations = mutableListOf<Conversation>()

    fun clear() {
        conversations.clear()
    }

    fun getAll(): List<Conversation> {
        return conversations.sortedByDescending {
            it.lastMessageTimestamp
        }
    }

    fun getConversation(receiverId: String): Conversation? {
        return conversations.find {
            it.receiverId == receiverId
        }
    }

    fun updateConversation(

        receiverId: String,
        receiverName: String,
        receiverRole: PassengerRole?,
        lastMessage: String,
        timestamp: Long

    ) {

        val conversation =
            conversations.find {
                it.receiverId == receiverId
            }

        if (conversation == null) {

            conversations.add(

                Conversation(

                    receiverId = receiverId,
                    receiverName = receiverName,
                    receiverRole = receiverRole,
                    lastMessage = lastMessage,
                    lastMessageTimestamp = timestamp

                )
            )

        } else {

            conversation.lastMessage = lastMessage
            conversation.lastMessageTimestamp = timestamp
        }
    }

}