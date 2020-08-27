package com.example.weatherwake.Activities

import android.content.Intent
import android.media.MediaPlayer
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import com.example.weatherwake.APIs.WeatherAPI
import com.example.weatherwake.Classes.Alarm
import com.example.weatherwake.Classes.Utilities
import com.example.weatherwake.R
import kotlinx.android.synthetic.main.activity_ringing_alarm.*
import java.lang.StringBuilder

class RingingAlarm : AppCompatActivity() {
    val weatherInformation = WeatherAPI()
    lateinit var utils: Utilities
    lateinit var ringingAlarm: Alarm


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ringing_alarm)
        utils = Utilities(this)


//        LocalBroadcastManager.getInstance(this)
////            .registerReceiver(broadcastReceiver, IntentFilter())
    }

    override fun onStart() {
        super.onStart()

        if (intent != null) {
            ringingAlarm = intent.extras?.get("newAlarmToRing") as Alarm
        }

        ringAlarm()
    }


    fun ringAlarm() {
        val loc: DoubleArray = utils.getLastLocation()
        weatherInformation.executeWeather(loc.get(0), loc.get(1))
        while (weatherInformation.isLocationExecuted() == false);

//        Toast.makeText(
//            this,
//            "Current weather is " + weatherInformation.getWeather("main"),
//            Toast.LENGTH_SHORT
//        ).show()
        Toast.makeText(this, ringingAlarm.getAlarmDescription(), Toast.LENGTH_LONG).show()

        val alarmDescription: TextView = findViewById(R.id.textViewAlarmRingingDescription)
        val alarmCurrentWeather: TextView = findViewById(R.id.textViewCurrentWeather)
        val weatherDesc = StringBuilder()

        weatherDesc.append("Current Weather in ",weatherInformation.getCity(),": ")
        weatherDesc.append(weatherInformation.getWeather("main"),"...",weatherInformation.getWeather("description"),"\n")
        weatherDesc.append("Current Temperature: ",weatherInformation.getCurrentTemp("temp",'f'),"°F\n")
        weatherDesc.append("Current Wind: ",weatherInformation.getWindSpeed('i'),"mph")

        alarmDescription.setText(ringingAlarm.getAlarmDescription())
        alarmCurrentWeather.setText(weatherDesc)


        val mp: MediaPlayer = MediaPlayer.create(this,R.raw.comessun)
        mp.start()

        btnStopAlarm.setOnClickListener{
            mp.release()
            val backToMainActivity = Intent(this,MainActivity::class.java)
            backToMainActivity.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT)
            startActivity(backToMainActivity)
        }


    }


//    val broadcastReceiver = object : BroadcastReceiver(){
//        override fun onReceive(context: Context?, intent: Intent?) {
//            println("I made it to here!!!")
//            setContentView(R.layout.activity_ringing_alarm)
//            ringAlarm()
//        }
//    }


}