package com.example.unipool.models

data class Conversation(

    val receiverId: String,

    val receiverName: String,

    val receiverRole: PassengerRole? = null,

    var lastMessage: String = "Tap to chat",

    var lastMessageTimestamp: Long = 0L

)