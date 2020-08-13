package com.example.weatherwake.Classes

import java.io.Serializable //allows us to pass alarm objects between activities

class Alarm(time: String,date: String, description: String,daysOfWeek: Array<String>?,alarmType: Int) : Serializable {
    val time: String
    val date: String
    val description: String
    var active: Boolean = true

    init {
        this.time = time
        this.date = date
        this.description = description
    }

    //Functions
    public fun getAlarmTime(): String{
        return time
    }
    public fun getAlarmDate(): String{
        return date
    }
    public fun getAlarmDescription(): String{
        return description
    }

    public fun toggleAlarm(){
        active = !active
    }



}