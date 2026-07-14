package com.example.unipool

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class StaffScheduleActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(
            R.layout.activity_student_placeholder
        )

        findViewById<TextView>(
            R.id.txtTitle
        ).text = "Staff Schedule"

        findViewById<TextView>(
            R.id.txtSubtitle
        ).text =
            "Staff trip schedules will appear here."
    }
}