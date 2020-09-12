package com.example.weatherwake.Classes

/*Alarm Class. Is the Alarm object which holds all alarm information and allows alarm to be passed around */

import java.io.Serializable //allows us to pass alarm objects between activities
import java.sql.Time
import java.text.SimpleDateFormat
import java.util.*

class Alarm(
    calendar: Calendar,
    alarmDateInfo: String,
    description: String,
    daysOfWeek: IntArray,
    alarmType: Int,
    randomId: Int
) : Serializable {

    private var calendar: Calendar
    private var description: String
    private var active: Boolean = true
    private var daysOfWeek: IntArray
    private var alarmType: Int = -1
    private var randomId: Int = 0
    private var alarmDateInfo: String


    init {
        this.calendar = calendar
        this.description = description
        this.daysOfWeek = daysOfWeek
        this.randomId = randomId
        this.alarmType = alarmType
        this.alarmDateInfo = alarmDateInfo
    }

    //Functions
    public fun getAlarmTime(): String {
        return getFormattedTime(calendar)
    }

    public fun getAlarmDate(): String {
        return getFormattedDate(calendar, false)
    }

    //returns whether info about whether alarm is today, tomorrow, days of week, or calendar
    public fun getAlarmDateInfo(): String {
        return alarmDateInfo
    }

    public fun getAlarmDescription(): String {
        return description
    }

    public fun getAlarmId(): Int {
        return randomId
    }

    //compare the times of two different alarms
   fun earlier(comparingAlarm: Alarm): Boolean{
       if(calendar.get(Calendar.HOUR_OF_DAY)==comparingAlarm.getCalendar().get(Calendar.HOUR_OF_DAY)){
           return calendar.get(Calendar.MINUTE) < comparingAlarm.getCalendar().get(Calendar.MINUTE)
       }
       else{
           return calendar.get(Calendar.HOUR_OF_DAY)< comparingAlarm.getCalendar().get(Calendar.HOUR_OF_DAY)
       }
   }

    public fun getCalendar(): Calendar {
        return calendar
    }

    public fun setAlarmTime(hourOfDay: Int, minute: Int) {
        this.calendar.set(Calendar.HOUR_OF_DAY, hourOfDay)
        this.calendar.set(Calendar.MINUTE, minute)
    }

    public fun setAlarmDateInfo(month: Int, date: Int, year: Int) {
        calendar.set(Calendar.MONTH, month)
        calendar.set(Calendar.DATE, date)
        calendar.set(Calendar.YEAR, year)
    }

    public fun setAlarmDescription(newDescription: String) {
        this.description = newDescription
    }

    public fun toggleAlarm() {
        active = !active
    }

    override fun toString(): String {
        return "Alarm Date: " + getFormattedDate(calendar, true) + ". Alarm Time " + getFormattedTime(calendar)
    }

    /*Private methods for formatting */
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

    //formats the time of the calendar to be h:mm a
    private fun getFormattedTime(calendar: Calendar): String {
        val timeDate = Date(calendar.timeInMillis)
        val timeFormatter = SimpleDateFormat("h:mm a", Locale.US)
        return timeFormatter.format(timeDate)
    }
}

