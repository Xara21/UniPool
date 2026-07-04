package com.example.unipool

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.Gravity
import android.view.ViewGroup
import android.widget.Button
import android.widget.FrameLayout
import android.widget.GridLayout
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.PopupMenu
import android.widget.ScrollView
import android.widget.TableLayout
import android.widget.TableRow
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.graphics.toColorInt

class DriverPassengersActivity : AppCompatActivity() {

    private lateinit var panelStatusCard: LinearLayout
    private lateinit var txtCurrentStatus: TextView
    private lateinit var txtDriverName: TextView
    private lateinit var txtDepartureTime: TextView
    private lateinit var txtRouteName: TextView
    private lateinit var btnSelectTrip: Button
    private lateinit var btnOpenSeats: Button
    private lateinit var btnActionTrip: Button
    private lateinit var btnHamburger: ImageView
    private lateinit var tablePassengerManifest: TableLayout

    private var currentStatusState = 0
    private var selectedTripIndex = -1

    private val totalConfiguredSeatsList = mutableListOf<String>()
    private val unoccupiedSeatsList = mutableListOf<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_driver_passengers)

        initializeViews()
        setupControlActions()

        TripManager.loadFromStorage(this)
        promptTripSelectionWorkflow()
    }

    private fun initializeViews() {
        panelStatusCard = findViewById(R.id.panelStatusCard)
        txtCurrentStatus = findViewById(R.id.txtCurrentStatus)
        txtDriverName = findViewById(R.id.txtDriverName)
        txtDepartureTime = findViewById(R.id.txtDepartureTime)
        txtRouteName = findViewById(R.id.txtRouteName)
        btnSelectTrip = findViewById(R.id.btnSelectTrip)
        btnOpenSeats = findViewById(R.id.btnOpenSeats)
        btnActionTrip = findViewById(R.id.btnActionTrip)
        btnHamburger = findViewById(R.id.btnHamburger)
        tablePassengerManifest = findViewById(R.id.tablePassengerManifest)
    }

    private fun promptTripSelectionWorkflow() {
        val tripsList = TripManager.tripLogsList
        if (tripsList.isEmpty()) {
            Toast.makeText(this, "No trips found in history log records.", Toast.LENGTH_LONG).show()
            txtDriverName.text = TripManager.currentDriverName
            return
        }

        val tripSelectionItems = tripsList.map { trip ->
            val time = getReflectiveProperty(trip, arrayOf("departureTime", "time", "date"))
            val destination = getReflectiveProperty(trip, arrayOf("destination", "route", "routeName"))
            val status = getReflectiveProperty(trip, arrayOf("status", "tripStatus"))
            "$time - $destination ($status)"
        }.toTypedArray()

        AlertDialog.Builder(this)
            .setTitle("Select Active Trip Assignment")
            .setCancelable(false)
            .setItems(tripSelectionItems) { _, index ->
                selectedTripIndex = index
                bindSelectedTripData(tripsList[index])
            }
            .create()
            .show()
    }

    @SuppressLint("SetTextI18n")
    private fun bindSelectedTripData(trip: Any) {
        txtDriverName.text = TripManager.currentDriverName
        txtDepartureTime.text = getReflectiveProperty(trip, arrayOf("departureTime", "time", "date"))
        txtRouteName.text = getReflectiveProperty(trip, arrayOf("destination", "route", "routeName"))

        val rawSeats = getReflectiveProperty(trip, arrayOf("seats", "capacity", "totalSeats"))
        val totalSeatsCount = rawSeats.toIntOrNull() ?: 10

        totalConfiguredSeatsList.clear()
        unoccupiedSeatsList.clear()

        val rowsArray = arrayOf("A", "B", "C", "D")
        var generatedCount = 0
        for (r in rowsArray) {
            val totalColumnsCount = if (r == "C" || r == "D") 4 else 5
            for (c in 1..totalColumnsCount) {
                if (generatedCount < totalSeatsCount) {
                    val seatLabel = "$r$c"
                    totalConfiguredSeatsList.add(seatLabel)
                    unoccupiedSeatsList.add(seatLabel)
                    generatedCount++
                }
            }
        }

        refreshPassengerTable()

        val status = getReflectiveProperty(trip, arrayOf("status", "tripStatus"))
        when (status) {
            "Scheduled", "Pending" -> {
                currentStatusState = 0
                txtCurrentStatus.text = "Scheduled"
                panelStatusCard.setBackgroundColor("#FFEB3B".toColorInt())
                btnActionTrip.text = "Start Trip"
                btnActionTrip.setBackgroundColor("#4CAF45".toColorInt())
                btnActionTrip.isEnabled = true
            }
            "In Progress", "On Going", "Ongoing" -> {
                currentStatusState = 1
                txtCurrentStatus.text = "On Going"
                panelStatusCard.setBackgroundColor("#4CAF50".toColorInt())
                btnActionTrip.text = "End Trip"
                btnActionTrip.setBackgroundColor("#F44336".toColorInt())
                btnActionTrip.isEnabled = true
            }
            "Arrived", "Completed", "Finished" -> {
                currentStatusState = 2
                txtCurrentStatus.text = "Arrived"
                panelStatusCard.setBackgroundColor("#1976D2".toColorInt())
                btnActionTrip.text = "Trip Completed"
                btnActionTrip.setBackgroundColor(Color.GRAY)
                btnActionTrip.isEnabled = false
            }
        }
    }

    @SuppressLint("SetTextI18n")
    private fun refreshPassengerTable() {
        val childCount = tablePassengerManifest.childCount
        if (childCount > 1) {
            tablePassengerManifest.removeViews(1, childCount - 1)
        }

        val occupiedSeatsList = totalConfiguredSeatsList.filter { !unoccupiedSeatsList.contains(it) }

        for (seatId in occupiedSeatsList) {
            val row = TableRow(this).apply {
                layoutParams = TableLayout.LayoutParams(TableLayout.LayoutParams.MATCH_PARENT, TableLayout.LayoutParams.WRAP_CONTENT)
                setPadding(0, 4, 0, 4)
                gravity = Gravity.CENTER_VERTICAL
            }

            val txtName = TextView(this).apply {
                layoutParams = TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 1.2f)
                text = "Student"
                setTextColor(Color.BLACK)
                textSize = 13f
            }

            val txtSeat = TextView(this).apply {
                layoutParams = TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 1f)
                text = "#$seatId"
                setTextColor(Color.BLACK)
                textSize = 13f
            }

            val btnNotifyPassenger = Button(this).apply {
                layoutParams = TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT)
                text = "Notify"
                textSize = 11f
                setOnClickListener {
                    Toast.makeText(this@DriverPassengersActivity, "Notification sent to passenger in seat $seatId!", Toast.LENGTH_SHORT).show()
                }
            }

            row.addView(txtName)
            row.addView(txtSeat)
            row.addView(btnNotifyPassenger)
            tablePassengerManifest.addView(row)
        }
    }

    @SuppressLint("SetTextI18n")
    private fun setupControlActions() {
        btnHamburger.setOnClickListener {
            val popup = PopupMenu(this, btnHamburger)
            popup.menu.add("Statistics")
            popup.menu.add("Departures")
            popup.menu.add("Passengers")
            popup.menu.add("Trip Logs")
            popup.menu.add("Messages")

            popup.setOnMenuItemClickListener { item ->
                when (item.title) {
                    "Passengers" -> {
                        Toast.makeText(this, "Already viewing Passengers Manifest", Toast.LENGTH_SHORT).show()
                    }
                    "Trip Logs" -> {
                        try {
                            val intent = Intent(this, Class.forName("com.example.unipool.TripLogsActivity"))
                            startActivity(intent)
                        } catch (_: Exception) {
                            Toast.makeText(this, "Navigating to Trip Logs...", Toast.LENGTH_SHORT).show()
                        }
                    }
                    else -> {
                        Toast.makeText(this, "Navigating to ${item.title}...", Toast.LENGTH_SHORT).show()
                    }
                }
                true
            }
            popup.show()
        }

        btnSelectTrip.setOnClickListener {
            promptTripSelectionWorkflow()
        }

        btnOpenSeats.setOnClickListener {
            if (selectedTripIndex == -1) {
                Toast.makeText(this, "Please select a trip assignment first.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            showSeatOccupationDialog()
        }

        btnActionTrip.setOnClickListener {
            if (selectedTripIndex == -1) return@setOnClickListener

            val trip = TripManager.tripLogsList[selectedTripIndex]
            when (currentStatusState) {
                0 -> {
                    currentStatusState = 1
                    txtCurrentStatus.text = "On Going"
                    panelStatusCard.setBackgroundColor("#4CAF50".toColorInt())
                    btnActionTrip.text = "End Trip"
                    btnActionTrip.setBackgroundColor("#F44336".toColorInt())
                    setReflectiveProperty(trip, "status", "In Progress")
                    TripManager.updateTrip(this, selectedTripIndex, trip)
                }
                1 -> {
                    currentStatusState = 2
                    txtCurrentStatus.text = "Arrived"
                    panelStatusCard.setBackgroundColor("#1976D2".toColorInt())
                    btnActionTrip.text = "Trip Completed"
                    btnActionTrip.isEnabled = false
                    btnActionTrip.setBackgroundColor(Color.GRAY)
                    setReflectiveProperty(trip, "status", "Completed")
                    TripManager.updateTrip(this, selectedTripIndex, trip)
                }
            }
        }
    }

    private fun showSeatOccupationDialog() {
        val context = this
        val dialogScrollView = ScrollView(context).apply {
            layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
            setPadding(24, 24, 24, 24)
        }

        val gridSeats = GridLayout(context).apply {
            columnCount = 5
            rowCount = 4
            alignmentMode = GridLayout.ALIGN_BOUNDS
            useDefaultMargins = true
            val params = FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT)
            params.gravity = Gravity.CENTER
            layoutParams = params
        }

        val rowsArray = arrayOf("A", "B", "C", "D")
        for (r in rowsArray.indices) {
            val totalColumnsCount = if (rowsArray[r] == "C" || rowsArray[r] == "D") 4 else 5
            for (c in 1..totalColumnsCount) {
                val seatIdentifierLabel = "${rowsArray[r]}$c"

                if (!totalConfiguredSeatsList.contains(seatIdentifierLabel)) {
                    continue
                }

                val seatButton = Button(context).apply {
                    text = seatIdentifierLabel
                    textSize = 12f
                    setTextColor(Color.BLACK)

                    layoutParams = GridLayout.LayoutParams().apply {
                        width = 110
                        height = 110
                        setMargins(6, 6, 6, 6)
                    }

                    if (unoccupiedSeatsList.contains(seatIdentifierLabel)) {
                        setBackgroundColor("#FFCDD2".toColorInt())
                    } else {
                        setBackgroundColor("#C8E6C9".toColorInt())
                    }

                    setOnClickListener {
                        if (unoccupiedSeatsList.contains(seatIdentifierLabel)) {
                            unoccupiedSeatsList.remove(seatIdentifierLabel)
                            setBackgroundColor("#C8E6C9".toColorInt())
                            Toast.makeText(context, "Seat $seatIdentifierLabel Occupied", Toast.LENGTH_SHORT).show()
                        } else {
                            unoccupiedSeatsList.add(seatIdentifierLabel)
                            setBackgroundColor("#FFCDD2".toColorInt())
                            Toast.makeText(context, "Seat $seatIdentifierLabel Unoccupied", Toast.LENGTH_SHORT).show()
                        }
                        refreshPassengerTable()
                    }
                }
                gridSeats.addView(seatButton)
            }
        }

        dialogScrollView.addView(gridSeats)

        AlertDialog.Builder(context)
            .setTitle("Seat Map (Red = Unoccupied)")
            .setView(dialogScrollView)
            .setPositiveButton("Done") { dialog, _ -> dialog.dismiss() }
            .create()
            .show()
    }

    private fun getReflectiveProperty(obj: Any, fields: Array<String>): String {
        for (fieldName in fields) {
            try {
                val field = obj::class.java.declaredFields.firstOrNull { it.name == fieldName }
                if (field != null) {
                    field.isAccessible = true
                    return field.get(obj)?.toString() ?: ""
                }
            } catch (_: Exception) { }
        }
        return ""
    }

    private fun setReflectiveProperty(obj: Any, fieldName: String, value: String) {
        try {
            val field = obj::class.java.declaredFields.firstOrNull { it.name == fieldName || it.name == "tripStatus" }
            if (field != null) {
                field.isAccessible = true
                field.set(obj, value)
            }
        } catch (_: Exception) { }
    }
}