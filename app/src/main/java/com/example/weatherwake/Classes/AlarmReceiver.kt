package com.example.weatherwake.Classes

import android.app.Notification
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import java.text.DateFormat
import java.util.*

class AlarmReceiver : BroadcastReceiver() {
    //method called when the alarm is fired
    override fun onReceive(context: Context?, intent: Intent?) {
        val date = Calendar.getInstance()
        println("THE ALARM RANG AT "+ date.get(Calendar.HOUR)+":"+date.get(Calendar.MINUTE))
    }
}