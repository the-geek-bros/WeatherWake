package com.example.weatherwake.Activities

/*AlarmMaker Activity. This is the activity where the user creates a new alarm
* Alarm is made and sent to the Main Activity to add to Recycler view and to the Alarm Manager.
* */

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.example.weatherwake.Activities.CreateAlarm.AlarmCalendarView
import com.example.weatherwake.Activities.CreateAlarm.DaysOfWeek_ViewHolder
import com.example.weatherwake.Activities.CreateAlarm.OnItemClickListenerDayOfWeek
import com.example.weatherwake.Activities.CreateAlarm.RecurringAlarms
import com.example.weatherwake.Classes.Alarm
import com.example.weatherwake.Classes.AlarmHandlers
import com.example.weatherwake.R
import java.lang.StringBuilder
import java.text.SimpleDateFormat
import java.util.*


class AlarmMaker : AppCompatActivity(), AdapterView.OnItemSelectedListener {
    lateinit var alarm_note_text: EditText
    lateinit var cancel_button: Button
    lateinit var save_button: Button
    lateinit var alarmTypesSpinner: Spinner
    lateinit var alarmDetailsTextView: TextView
    lateinit var alarmTimePicker: TimePicker

    /*global variables...might not make some of these global*/
    lateinit var alarmHandler: AlarmHandlers
    var alarmChosenCalendar: Calendar =
        Calendar.getInstance() //object will be calendar object of the time chosen

    //For when there are recurring alarms
    var daysOfWeekChosen: IntArray = IntArray(0)
    val daysOfWeek = listOf("Su", "Mo", "Tu", "We", "Th", "Fr", "Sa")

    //alarm display
    var alarmDateInfo: String = "" //will display today, tomorrow, days of week, or Calendar day
    var alarmChosenPosition: Int = 0 //say which alarm it is


    var tomorrowSet: Boolean = false

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

        //alarm handler
        alarmHandler = AlarmHandlers(this)


        /*Create all of the listeners */

        //listener for the cancel button
        cancel_button.setOnClickListener { view ->
            val back_to_main_intent: Intent = Intent(applicationContext, MainActivity::class.java)
            back_to_main_intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
            startActivity(back_to_main_intent)
        }
        //listener for the save button... creates the Alarm object. Makes sure alarm is in the future
        save_button.setOnClickListener { view ->
            if (alarmChosenPosition != 1 && alarmChosenCalendar.before(Calendar.getInstance())) {
                Toast.makeText(this, "Alarm has to be in future", Toast.LENGTH_SHORT).show()
            } else {
                createNewAlarm()
            }
        }

        //listener for when user changes the time of the alarm
        alarmTimePicker.setOnTimeChangedListener { view, hourOfDay, minute ->
            val current = Calendar.getInstance()
            val currentVals = listOf<Int>(
                current.get(Calendar.DATE),
                current.get(Calendar.HOUR_OF_DAY),
                current.get(Calendar.MINUTE)
            )
            //alarm should go next day if below conditions apply
            if (alarmChosenPosition == 0) {
                if (hourOfDay < currentVals[1] || hourOfDay == currentVals[1] && minute < currentVals[2]) {
                    if (!tomorrowSet) {
                        alarmChosenCalendar.add(Calendar.DATE, 1)
                        tomorrowSet = true
                    }
                } else if (alarmChosenCalendar.get(Calendar.DATE) != currentVals[0] && (hourOfDay > currentVals[1]) || (hourOfDay == currentVals[1] && minute > currentVals[2])) {
                    if (tomorrowSet) {
                        alarmChosenCalendar.add(Calendar.DATE, -1)
                        tomorrowSet = false
                    }

                }
            }
            alarmChosenCalendar.set(Calendar.HOUR_OF_DAY, hourOfDay)
            alarmChosenCalendar.set(Calendar.MINUTE, minute)
            updateAlarmDetails()
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
        if (intent != null) {
            if (intent.hasExtra("calendarObj")) {
                val calendarObj: Calendar = intent?.extras?.get("calendarObj") as Calendar
                alarmChosenCalendar.set(Calendar.DATE, calendarObj.get(Calendar.DATE))
                alarmChosenCalendar.set(Calendar.MONTH, calendarObj.get(Calendar.MONTH))
                alarmChosenCalendar.set(Calendar.YEAR, calendarObj.get(Calendar.YEAR))
            } else if (intent.hasExtra("Days Chosen")) {
                daysOfWeekChosen = intent.getIntArrayExtra("Days Chosen")!!
            }
        }

        updateAlarmDetails()

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
            0 -> {
                alarmChosenCalendar.set(Calendar.DATE, Calendar.getInstance().get(Calendar.DATE))
                alarmChosenCalendar.set(Calendar.MONTH, Calendar.getInstance().get(Calendar.MONTH))
                alarmChosenCalendar.set(Calendar.YEAR, Calendar.getInstance().get(Calendar.YEAR))
                updateAlarmDetails()
            }
            1 -> startActivity(Intent(applicationContext, RecurringAlarms::class.java))
            2 -> startActivity(Intent(applicationContext, AlarmCalendarView::class.java))
        }
    }

    /*VARIOUS METHODS FOR THE ALARM MAKER CLASS */

    //Creates alarm object and adds alarm to recycler view and to alarm manager
    private fun createNewAlarm() {
        val randomId:Int = UUID.randomUUID().hashCode()
        alarmChosenCalendar.set(Calendar.SECOND, 0)
        val newAlarm = Alarm(
            alarmChosenCalendar,
            alarmDateInfo,
            alarm_note_text.text.toString(),
            daysOfWeekChosen,
            alarmChosenPosition,
            randomId
        )
        // Adds the alarm to the recycler view of alarms so that it is displayed on the main page
        addAlarmToRecyclerView(newAlarm)
        // Creates a new pending alarm for the specific date and time
        addAlarmToAlarmManager(newAlarm)
    }

    //formats the time of the calendar to be h:mm a
    private fun getFormattedTime(calendar: Calendar): String {
        val timeDate = Date(calendar.timeInMillis)
        val timeFormatter = SimpleDateFormat("h:mm a", Locale.US)
        return timeFormatter.format(timeDate)
    }

    //updates alarm details at bottom of screen based on alarm option chosen
    private fun updateAlarmDetails() {
        val alarmDetailStartString = "Setting Alarm for\n"
        val info = StringBuilder(alarmDetailStartString)
        when (alarmChosenPosition) {
            0 -> {
                val todayOrTomorrow =
                    if (tomorrowSet) "Tomorrow" else "Today"
                alarmDateInfo = todayOrTomorrow
                info.append(todayOrTomorrow, " ", getFormattedTime(alarmChosenCalendar))
            }

            1 -> {
                info.append(getFormattedTime(alarmChosenCalendar), " on ")
                val listOfDates: StringBuilder = StringBuilder()
                for (x in 0 until daysOfWeekChosen.size - 1) {
                    listOfDates.append(daysOfWeek[daysOfWeekChosen[x]], ", ")
                }
                listOfDates.append(daysOfWeek[daysOfWeekChosen[daysOfWeekChosen.size - 1]])
                info.append(listOfDates)
                alarmDateInfo = listOfDates.toString()
            }

            2 -> {
                info.append(
                    getFormattedTime(alarmChosenCalendar),
                    " ",
                    getFormattedDate(alarmChosenCalendar, true)
                )
                alarmDateInfo = getFormattedDate(alarmChosenCalendar, false)
            }
        }
        alarmDetailsTextView.setText(info)
    }

    //takes a calendar objects and formats it to be m/d/yyyy. Adds dayOfWeek if true,
    private fun getFormattedDate(calendar: Calendar, includeDayOfWeek: Boolean): String {
        val day = calendar.get(Calendar.DAY_OF_MONTH).toString()
        val month = (calendar.get(Calendar.MONTH) + 1).toString()
        val year = calendar.get(Calendar.YEAR).toString()
        if (includeDayOfWeek) {
            val dayOfWeek: String? =
                calendar.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.SHORT, Locale.US)
            return dayOfWeek + " " + month + "/" + day + "/" + year
        } else {
            return month + "/" + day + "/" + year
        }

    }

    //sends alarm object to MainActivity and returns back to MainActivity
    private fun addAlarmToRecyclerView(newAlarm: Alarm) {
        val back_to_main_intent: Intent = Intent(applicationContext, MainActivity::class.java)
        back_to_main_intent.putExtra("newAlarm", newAlarm)
        back_to_main_intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT)
        startActivity(back_to_main_intent)
    }

    private fun addAlarmToAlarmManager(newAlarm: Alarm) {
        alarmHandler.addAlarmToAlarmManager(newAlarm)
    }

}