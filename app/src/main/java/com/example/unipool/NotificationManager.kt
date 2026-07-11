package com.example.unipool

object NotificationManager {

    private val notifications = mutableListOf<String>()

    fun add(message: String) {
        notifications.add(0, message)
    }

    fun getAll(): List<String> {
        return notifications
    }

    fun clear() {
        notifications.clear()
    }
}