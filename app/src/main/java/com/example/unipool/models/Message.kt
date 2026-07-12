package com.example.unipool.models

data class Message(

    val senderId: String,

    val senderName: String,

    val receiverId: String,

    val receiverName: String,

    val message: String,

    val timestamp: Long = System.currentTimeMillis(),

    var isRead: Boolean = false

)