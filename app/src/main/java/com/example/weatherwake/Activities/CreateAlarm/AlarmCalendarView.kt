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
                val code: Int = 100
                val formattedDate: String = getFormattedDate(calendarDate)
                val backToAlarmMaker = Intent(this, AlarmMaker::class.java)
                backToAlarmMaker.putExtra("calendarObj", calendarDate)
                backToAlarmMaker.putExtra("calendarDateString", formattedDate)
                backToAlarmMaker.addFlags (Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                startActivityIfNeeded(backToAlarmMaker,0)
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

    private fun getFormattedDate(calendar: Calendar): String {
        val dayOfWeek: String? =
            calendar.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.LONG, Locale.ENGLISH)
        val day = calendar.get(Calendar.DAY_OF_MONTH).toString()
        val month = (calendar.get(Calendar.MONTH)+1).toString()
        val year = calendar.get(Calendar.YEAR).toString()

        return dayOfWeek + " " + month + "/" + day + "/" + year
    }

}