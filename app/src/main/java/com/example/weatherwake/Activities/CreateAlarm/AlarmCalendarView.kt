package com.example.weatherwake.Activities.CreateAlarm

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

lateinit var saveDateButton: Button
lateinit var alarmCalendar: CalendarView

class AlarmCalendarView : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_alarm_calendar_view)

        saveDateButton = findViewById(R.id.saveDateButton)
        alarmCalendar = findViewById(R.id.alarmCalendarView)

//        alarmCalendar.setOnDateChangeListener(CalendarView.OnDateChangeListener(
//            // TODO("add a CalendarView on Date Change Listener so that it knows when the day has been changed")
//        ))

    }

    override fun onStart() {
        super.onStart()

        val currentDate: Date = Calendar.getInstance().time
        val calendarDate: Date = Date(alarmCalendar.date)




        saveDateButton.setOnClickListener { view ->
            println("CURRENT DATE IS  "+currentDate)
            println("CALENDAR DATE IS   "+calendarDate)

            if(calendarDate.before(currentDate)){
                Toast.makeText(this, "Needs to be a Future Date", Toast.LENGTH_SHORT).show()
            }
            else{
                startActivity(Intent(applicationContext,AlarmMaker::class.java))
            }
        }
    }

}