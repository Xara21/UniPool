package com.example.unipool.student

import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.ListView
import androidx.appcompat.app.AppCompatActivity
import com.example.unipool.NotificationManager
import com.example.unipool.R

class StudentNotificationsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_student_notification)

        val listView =
            findViewById<ListView>(R.id.listNotifications)

        val adapter = ArrayAdapter(
            this,
            android.R.layout.simple_list_item_1,
            NotificationManager.getAll()
        )

        listView.adapter = adapter
    }
}