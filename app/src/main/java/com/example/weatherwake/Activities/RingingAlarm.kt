package com.example.weatherwake.Activities

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.example.weatherwake.APIs.WeatherAPI
import com.example.weatherwake.Classes.Utilities
import com.example.weatherwake.R
import java.util.*

class RingingAlarm : AppCompatActivity() {

    val utils = Utilities(this)
    val weatherInformation = WeatherAPI()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        LocalBroadcastManager.getInstance(this)
            .registerReceiver(broadcastReceiver, IntentFilter())
    }

    public fun ringAlarm() {
        val date = Calendar.getInstance()
        val loc: DoubleArray = utils.getLastLocation()
        weatherInformation.executeWeather(loc.get(0),loc.get(1))
        while (weatherInformation.isLocationExecuted()==false);

        println("THE ALARM RANG AT " + date.get(Calendar.HOUR) + ":" + date.get(Calendar.MINUTE))
        Toast.makeText(this,"Current weather is "+weatherInformation.getWeather("main"),Toast.LENGTH_SHORT).show()
    }


    val broadcastReceiver = object : BroadcastReceiver(){
        override fun onReceive(context: Context?, intent: Intent?) {
            println("I made it to here!!!")
            setContentView(R.layout.activity_ringing_alarm)
            ringAlarm()
        }
    }


}