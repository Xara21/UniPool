package com.example.unipool.student

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.unipool.R

class StudentMessagesActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_student_placeholder)

        findViewById<TextView>(R.id.txtTitle).text = "Messages"

        findViewById<TextView>(R.id.txtSubtitle).text =
            "Messages will appear here."
    }
}