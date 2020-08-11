package com.example.weatherwake.Activities

/*This is the activity where they will be able to make a new alarm*/

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.example.weatherwake.Activities.CreateAlarm.AlarmCalendarView
import com.example.weatherwake.R
import java.util.*

class AlarmMaker : AppCompatActivity(), AdapterView.OnItemSelectedListener {
    lateinit var alarm_note_text: EditText
    lateinit var cancel_button: Button
    lateinit var save_button: Button
    lateinit var alarmTypesSpinner: Spinner
    lateinit var alarmDetailsTextView: TextView
    var alarmChosenPosition: Int = 0

    //extra variables... might not make these global later
    lateinit var dateChosen: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_alarm_maker)

        //All alarm variables
        alarm_note_text = findViewById(R.id.alarm_note)
        cancel_button = findViewById(R.id.button_cancel)
        save_button = findViewById(R.id.button_save)
        alarmDetailsTextView = findViewById(R.id.alarmDetails)
    }

    override fun onStart() {
        super.onStart()

        //listener for the cancel button
        cancel_button.setOnClickListener { view ->
            val back_to_main_intent: Intent = Intent(applicationContext, MainActivity::class.java)
            startActivity(back_to_main_intent)
        }

        //listener for the cancel button
        save_button.setOnClickListener { view ->

//            TODO("Add the alarm to the list of alarms so that it is displayed on the main page")
//


//            TODO("Create a new pending alarm for the specific date and time")
//            createNewAlarm()


            val back_to_main_intent: Intent = Intent(applicationContext, MainActivity::class.java)
            startActivity(back_to_main_intent)
        }

        //Spinner object
        alarmTypesSpinner = findViewById(R.id.alarm_type_spinner)
        val alarmTypesSpinnerAdapter = ArrayAdapter.createFromResource(
            this,
            R.array.alarm_types,
            android.R.layout.simple_spinner_item
        )
        alarmTypesSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        alarmTypesSpinner.adapter = alarmTypesSpinnerAdapter
        alarmTypesSpinner.onItemSelectedListener = this
    }

    override fun onNothingSelected(parent: AdapterView<*>?) {
        TODO("Not yet implemented")
    }

    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        val alarmChosen: String = parent?.getItemAtPosition(position).toString()

        //if statement stops setSelection() function from running the rest of the function if option was chosen
        if (alarmChosenPosition == position) {
            return
        }
        alarmChosenPosition = position
        when (alarmChosen) {
//            "Recurring" -> Intent(applicationContext,)
            "Calendar view" -> {
                startActivity(Intent(applicationContext, AlarmCalendarView::class.java))
            }

        }

    }


    private fun createNewAlarm(date: Calendar): Unit {


    }

    override fun onPause() {
        super.onPause()
        val dateChosen = intent.getStringExtra("calendarDate")
        println("CALENDAR DATE " + dateChosen)
    }

    override fun onResume() {
        super.onResume()
        alarmTypesSpinner.setSelection(alarmChosenPosition)
        when (alarmChosenPosition) {
            2 -> {
               alarmDetailsTextView.setText("Alarm Date Chosen: "+dateChosen)
            }
        }
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        val dateData = intent?.getStringExtra("calendarDateString")
        if (dateData != null) {
            dateChosen = dateData
        }

        val dateObj = intent?.extras?.get("calendarObj")
        onResume()

    }


}
