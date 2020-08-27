package com.example.weatherwake.Activities.CreateAlarm

/* Calendar View Activity. Activity is in charge of getting the Calendar Date the user wants the alarm to ring on.
    Sends back to Alarm Maker the date user chooses
 */


import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.CalendarView
import android.widget.Toast
import com.example.weatherwake.Activities.AlarmMaker
import com.example.weatherwake.R
import java.time.LocalDateTime
import java.util.*
import kotlin.collections.ArrayList

lateinit var saveDateButton: Button
lateinit var alarmCalendarView: CalendarView
lateinit var calendarDate: Calendar
val currentDate: Calendar = Calendar.getInstance()

class AlarmCalendarView : AppCompatActivity(), CalendarView.OnDateChangeListener {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_alarm_calendar_view)

        saveDateButton = findViewById(R.id.saveDateButton)
        alarmCalendarView = findViewById(R.id.alarmCalendarView)
        calendarDate = Calendar.getInstance()

    }

    override fun onStart() {
        super.onStart()

        saveDateButton.setOnClickListener { view ->
            if (calendarDate.before(currentDate)) {
                Toast.makeText(this, "Needs to be a Future Date", Toast.LENGTH_SHORT).show()
            } else {
                val backToAlarmMaker = Intent(this, AlarmMaker::class.java)
                backToAlarmMaker.putExtra("calendarObj", calendarDate)
                backToAlarmMaker.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                startActivity(backToAlarmMaker)
                finish()
            }
        }

        alarmCalendarView.setOnDateChangeListener({ view, year, month, dayOfMonth ->
            onSelectedDayChange(view, year, month, dayOfMonth)
        })
    }

    override fun onSelectedDayChange(view: CalendarView, year: Int, month: Int, dayOfMonth: Int) {
        calendarDate.set(year, month, dayOfMonth)
    }

}