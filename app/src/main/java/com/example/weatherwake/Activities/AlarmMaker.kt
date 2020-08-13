package com.example.weatherwake.Activities

/*This is the activity where they will be able to make a new alarm*/

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.example.weatherwake.Activities.CreateAlarm.AlarmCalendarView
import com.example.weatherwake.Activities.CreateAlarm.RecurringAlarms
import com.example.weatherwake.Activities.CreateAlarm.calendarDate
import com.example.weatherwake.Classes.Alarm
import com.example.weatherwake.R
import kotlinx.android.synthetic.main.app_bar_main.*
import java.text.SimpleDateFormat
import java.util.*
import com.example.weatherwake.Activities.MainActivity


class AlarmMaker : AppCompatActivity(), AdapterView.OnItemSelectedListener {
    lateinit var alarm_note_text: EditText
    lateinit var cancel_button: Button
    lateinit var save_button: Button
    lateinit var alarmTypesSpinner: Spinner
    lateinit var alarmDetailsTextView: TextView
    lateinit var alarmTimePicker: TimePicker

    //global variables...might not make some of these global
    var alarmTimePicked: Calendar = Calendar.getInstance()
    var alarmChosenPosition: Int = 0
    var tomorrowSet: Boolean = false
    var dateChosen: String = "Today"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_alarm_maker)

        //Assign alarm variables to their views
        alarm_note_text = findViewById(R.id.alarm_note)
        cancel_button = findViewById(R.id.button_cancel)
        save_button = findViewById(R.id.button_save)
        alarmDetailsTextView = findViewById(R.id.alarmDetails)
        alarmTimePicker = findViewById(R.id.alarm_timePicker)
        alarmTypesSpinner = findViewById(R.id.alarm_type_spinner)

        /*Create all of the listeners */

        //listener for the cancel button
        cancel_button.setOnClickListener { view ->
            val back_to_main_intent: Intent = Intent(applicationContext, MainActivity::class.java)
            back_to_main_intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
            startActivity(back_to_main_intent)
        }
        //listener for the save button... creates the Alarm object
        save_button.setOnClickListener { view ->
            createNewAlarm()
        }

        //listener for when user changes the time of the alarm
        alarmTimePicker.setOnTimeChangedListener { view, hourOfDay, minute ->
            alarmTimePicked = Calendar.getInstance()
            val current = Calendar.getInstance()
            val currentVals = listOf<Int>(
                current.get(Calendar.DATE),
                current.get(Calendar.HOUR_OF_DAY),
                current.get(Calendar.MINUTE)
            )
            //alarm should go next day if below conditions apply
            if (hourOfDay < currentVals[1] || hourOfDay == currentVals[1] && minute < currentVals[2]) {
                if (!tomorrowSet) {
                    alarmTimePicked.add(Calendar.DATE, 1)
                    tomorrowSet = true
                }
            } else if (alarmTimePicked.get(Calendar.DATE) != currentVals[0] && (hourOfDay > currentVals[1]) || (hourOfDay == currentVals[1] && minute > currentVals[2])) {
                if (tomorrowSet) {
                    alarmTimePicked.add(Calendar.DATE, -1)
                    tomorrowSet = false
                }

            }
            alarmTimePicked.set(Calendar.HOUR_OF_DAY, hourOfDay)
            alarmTimePicked.set(Calendar.MINUTE, minute)
            updateAlarmDetails()

            //if not in calendar view, date chosen will just be today or tomorrow.... this is temporary since it will be a date later
            if (alarmChosenPosition != 2) {
                if (timePastToday(alarmTimePicked)) {
                    dateChosen = "Tomorrow"
                } else {
                    dateChosen = "Today"
                }
            }
        }
    }

    override fun onStart() {
        super.onStart()

        updateAlarmDetails()

        //Spinner object
        val alarmTypesSpinnerAdapter = ArrayAdapter.createFromResource(
            this,
            R.array.alarm_types,
            android.R.layout.simple_spinner_item
        )
        alarmTypesSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        alarmTypesSpinner.adapter = alarmTypesSpinnerAdapter
        alarmTypesSpinner.onItemSelectedListener = this
    }// end onStart method

    override fun onResume() {
        super.onResume()
        alarmTypesSpinner.setSelection(alarmChosenPosition)
    }//end onResume method

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        val dateData = intent?.getStringExtra("calendarDateString")
        if (dateData != null) {
            dateChosen = dateData
        }
        updateAlarmDetails()

        val dateObj = intent?.extras?.get("calendarObj")
    }//end onNewIntent

    override fun onNothingSelected(parent: AdapterView<*>?) {
        TODO("Not yet implemented")
    }

    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        //if statement stops setSelection() function from running the rest of the function if option was chosen
        if (alarmChosenPosition == position) {
            return
        }

        alarmChosenPosition = position
        when (position) {
            0 -> updateAlarmDetails()
            1 -> startActivity(Intent(applicationContext, RecurringAlarms::class.java))
            2 -> startActivity(Intent(applicationContext, AlarmCalendarView::class.java))
        }
    }

    /*VARIOUS METHODS FOR THE ALARM MAKER CLASS */

    //adds alarm to recycler view and to alarm manager
    private fun createNewAlarm() {
        val newAlarm: Alarm =
            Alarm(getFormattedTime(alarmTimePicked), dateChosen, alarm_note_text.text.toString(), null,alarmChosenPosition)
        //    TODO("Add the alarm to the list of alarms so that it is displayed on the main page")
        addAlarmToRecyclerView(newAlarm)
        //     TODO("Create a new pending alarm for the specific date and time")
        addAlarmToAlarmManager(newAlarm)
    }

    //formats the time of the calendar to be h:mm a
    private fun getFormattedTime(calendar: Calendar): String {
        val timeDate = Date(calendar.timeInMillis)
        val timeFormatter = SimpleDateFormat("h:mm a", Locale.US)
        return timeFormatter.format(timeDate)
    }

    //checks whether the calendar given was before current time. Returns boolean
    private fun timePastToday(calendar: Calendar): Boolean {
        val current = Calendar.getInstance()
        return current.before(calendar)
    }

    //updates alarm details at bottom of screen based on alarm option chosen
    private fun updateAlarmDetails() {
        val info: String

        val alarmDetailStartString: String = "Setting Alarm for\n"

        when (alarmChosenPosition) {
            2 -> {
                info = alarmDetailStartString + getFormattedTime(alarmTimePicked) + " " + dateChosen
                alarmDetailsTextView.setText(info)
            }
            0 -> {
                val todayOrTomorrow = if (timePastToday(alarmTimePicked)) "Today" else "Tomorrow"
                info = alarmDetailStartString + " " + todayOrTomorrow + " " + getFormattedTime(
                    alarmTimePicked
                )
                alarmDetailsTextView.setText(info)
            }
        }

    }

    //sends alarm object to MainActivity and returns back to MainActivity
    private fun addAlarmToRecyclerView(newAlarm: Alarm) {
        val back_to_main_intent: Intent = Intent(applicationContext, MainActivity::class.java)
        back_to_main_intent.putExtra("newAlarm", newAlarm)
        back_to_main_intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        startActivity(back_to_main_intent)
    }

    private fun addAlarmToAlarmManager(newAlarm: Alarm){

    }
}
