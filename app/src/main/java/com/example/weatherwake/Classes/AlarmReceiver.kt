package com.example.weatherwake.Classes

/*Alarm Receiver Class. Receives broadcast from alarm manager and sends alarm and information to Ringing Alarm activity */

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.example.weatherwake.Activities.RingingAlarm
import com.google.gson.Gson
import java.util.*


class AlarmReceiver : BroadcastReceiver() {
    var alarmToRing: Alarm = Alarm(Calendar.getInstance(),"Today","Hello There joe",
        IntArray(0),1,123
    )

    //method called when the alarm is fired
    override fun onReceive(context: Context?, intent: Intent?) {

        val alarmGSON = intent?.action
        if(alarmGSON!=null){
            alarmToRing = Gson().fromJson(alarmGSON, Alarm::class.java)
        }

        if (context != null) {
            val goToRingingAlarm = Intent(context.applicationContext, RingingAlarm::class.java)
            goToRingingAlarm.putExtra("newAlarmToRing", alarmToRing)
            context.startActivity(goToRingingAlarm)
        }
    }
}