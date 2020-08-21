package com.example.weatherwake.Classes

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.content.ContextCompat.startActivity
import com.example.weatherwake.Activities.RingingAlarm
import java.util.*

class AlarmReceiver : BroadcastReceiver() {
    //method called when the alarm is fired
    override fun onReceive(context: Context?, intent: Intent?) {
        val date = Calendar.getInstance()
        if(context!=null){
            val goToAlarm= Intent(context, RingingAlarm::class.java)

        }

        println("THE ALARM RANG AT " + date.get(Calendar.HOUR) + ":" + date.get(Calendar.MINUTE))
    }
}