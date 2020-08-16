package com.example.weatherwake.Classes

import android.app.Activity
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent

class AlarmHandlers (activity: Activity) {
    lateinit var activity: Activity
    val alarmManager: AlarmManager = activity.getSystemService(Context.ALARM_SERVICE) as AlarmManager

    init {
        this.activity = activity
    }

    public fun addAlarmToAlarmManager(newAlarm: Alarm){
        val ringAlarmIntent: Intent = Intent(activity.applicationContext,AlarmReceiver::class.java)
        val pendingIntent: PendingIntent = PendingIntent.getBroadcast(activity.applicationContext,newAlarm.getAlarmId(),ringAlarmIntent,0)
        alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP,newAlarm.getCalendar().timeInMillis,pendingIntent)
    }

    public fun cancelAlarm(alarmCancelling: Alarm){
        val ringAlarmIntent: Intent = Intent(activity.applicationContext,AlarmReceiver::class.java)
        val pendingIntent: PendingIntent = PendingIntent.getBroadcast(activity.applicationContext,alarmCancelling.getAlarmId(),ringAlarmIntent,0)

        alarmManager.cancel(pendingIntent)
    }


}