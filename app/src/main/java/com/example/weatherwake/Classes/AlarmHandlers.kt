package com.example.weatherwake.Classes

/*This class has methods which will be used to add alarms to the alarm manager and cancel alarms. AlarmHandler Object
has to be created with the activity passed in as well as the alarm*/


import android.app.Activity
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.content.ContextCompat.startActivity
import com.example.weatherwake.Activities.RingingAlarm
import java.util.*

class AlarmHandlers(activity: Activity) {
    var activity: Activity
    val alarmManager: AlarmManager = activity.getSystemService(Context.ALARM_SERVICE) as AlarmManager

    init {
        this.activity = activity
    }

    public fun addAlarmToAlarmManager(newAlarm: Alarm) {
        val ringAlarmIntent = Intent(activity.applicationContext, RingingAlarm::class.java)
        val pendingIntent: PendingIntent = PendingIntent.getBroadcast(
            activity.applicationContext,
            newAlarm.getAlarmId(),
            ringAlarmIntent,
            0
        )
        alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP,newAlarm.getCalendar().timeInMillis,pendingIntent)
    }

    public fun cancelAlarm(alarmCancelling: Alarm) {
        val ringAlarmIntent: Intent = Intent(activity.applicationContext, RingingAlarm::class.java)
        val pendingIntent: PendingIntent = PendingIntent.getBroadcast(
            activity.applicationContext,
            alarmCancelling.getAlarmId(),
            ringAlarmIntent,
            0
        )

        alarmManager.cancel(pendingIntent)
    }


}