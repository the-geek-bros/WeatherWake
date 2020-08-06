package com.example.weatherwake

/*This is the activity where they will be able to make a new alarm*/

import android.app.Activity
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.CalendarView
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_alarm_maker.view.*
import java.util.*

class AlarmMaker : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_alarm_maker)

        //get the current calendar time.
        val currentCalendar: Calendar = Calendar.getInstance()
        val current_month: Int = currentCalendar.get(Calendar.MONTH)

        //will see here the time and date the user puts
        val calendarView: CalendarView = findViewById<CalendarView>(R.id.calendarView)

    }

    override fun onStart() {
        super.onStart()

        //listener for the cancel button
        val cancel_button: Button = findViewById<Button>(R.id.button_cancel)
        cancel_button.setOnClickListener { view ->
            val back_to_main_intent: Intent = Intent(applicationContext, MainActivity::class.java)
            startActivity(back_to_main_intent)
        }

        val save_button: Button = findViewById(R.id.button_save)
        save_button.setOnClickListener { view ->
        }


    }


    private fun createNewAlarm(date: Calendar): Unit {
        val currentCalendar: Calendar =  Calendar.getInstance()
        if(date.compareTo(currentCalendar)<0){
            val mActivity: MainActivity = MainActivity()
            mActivity.createAlertDialog("Date behind","Your date is a previous date","OK","DISMISS")

        }

    }

}
