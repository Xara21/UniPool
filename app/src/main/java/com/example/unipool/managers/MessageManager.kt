package com.example.unipool.managers

import android.content.Context
import com.example.unipool.models.Message
import org.json.JSONArray
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object MessageManager {

    private val messages = mutableListOf<Message>()

    private const val PREFS_NAME = "unipool_messages"

    private const val KEY_MESSAGES = "messages"

    fun sendMessage(
        context: Context,
        message: Message
    ) {

        messages.add(message)

        android.util.Log.d(
            "MESSAGE_STORAGE",
            "Saved ${messages.size} messages"
        )

        saveToStorage(context)
    }

    fun getConversation(
        user1: String,
        user2: String
    ): List<Message> {

        return messages.filter {

            (it.senderId == user1 &&
                    it.receiverId == user2)

                    ||

                    (it.senderId == user2 &&
                            it.receiverId == user1)

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

        val last = getLastMessage(
            user1,
            user2
        )

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

        val last = getLastMessage(
            user1,
            user2
        )

        if (last == null) {

            return "--:--"
        }

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

    fun saveToStorage(
        context: Context
    ) {

        val prefs = context.getSharedPreferences(
            PREFS_NAME,
            Context.MODE_PRIVATE
        )

        val jsonArray = JSONArray()

        messages.forEach { msg ->

            val obj = JSONObject()

            obj.put(
                "senderId",
                msg.senderId
            )

            obj.put(
                "senderName",
                msg.senderName
            )

            obj.put(
                "receiverId",
                msg.receiverId
            )

            obj.put(
                "receiverName",
                msg.receiverName
            )

            obj.put(
                "message",
                msg.message
            )

            obj.put(
                "timestamp",
                msg.timestamp
            )

            obj.put(
                "isRead",
                msg.isRead
            )

            jsonArray.put(obj)
        }

        prefs.edit()
            .putString(
                KEY_MESSAGES,
                jsonArray.toString()
            )
            .apply()
    }

    fun loadFromStorage(
        context: Context
    ) {

        android.util.Log.d(
            "MESSAGE_STORAGE",
            "loadFromStorage() called"
        )

        val prefs = context.getSharedPreferences(
            PREFS_NAME,
            Context.MODE_PRIVATE
        )

        val jsonString =
            prefs.getString(
                KEY_MESSAGES,
                null
            ) ?: return

        messages.clear()

        val jsonArray =
            JSONArray(jsonString)

        for (i in 0 until jsonArray.length()) {

            val obj =
                jsonArray.getJSONObject(i)

            messages.add(

                Message(

                    senderId =
                        obj.getString(
                            "senderId"
                        ),

                    senderName =
                        obj.getString(
                            "senderName"
                        ),

                    receiverId =
                        obj.getString(
                            "receiverId"
                        ),

                    receiverName =
                        obj.getString(
                            "receiverName"
                        ),

                    message =
                        obj.getString(
                            "message"
                        ),

                    timestamp =
                        obj.getLong(
                            "timestamp"
                        ),

                    isRead =
                        obj.getBoolean(
                            "isRead"
                        )
                )
            )

        }

        android.util.Log.d(
            "MESSAGE_STORAGE",
            "Loaded ${messages.size} messages"
        )
    }

    fun getConversationPartners(
        userId: String
    ): List<Pair<String, String>> {

        val partners = mutableListOf<Pair<String, String>>()

        messages.forEach { message ->

            if (message.senderId == userId) {

                val partner = Pair(
                    message.receiverId,
                    message.receiverName
                )

                if (!partners.contains(partner)) {

                    partners.add(partner)
                }
            }

            if (message.receiverId == userId) {

                val partner = Pair(
                    message.senderId,
                    message.senderName
                )

                if (!partners.contains(partner)) {

                    partners.add(partner)
                }
            }
        }

        return partners
    }

}