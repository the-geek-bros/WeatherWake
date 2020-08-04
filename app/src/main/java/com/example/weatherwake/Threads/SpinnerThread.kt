package com.example.weatherwake.Threads

/*In this Thread, a spinner is to keep spinning until the weather is loaded in the side bar */

import android.app.Activity
import android.content.Context
import android.view.animation.Animation
import android.view.animation.RotateAnimation
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.MainThread
import com.example.weatherwake.MainActivity
import com.example.weatherwake.R
import com.example.weatherwake.R.drawable
import com.example.weatherwake.WeatherAPI

private val interrupted: Boolean = false;

public class SpinnerThread : Thread() {
    val spinner_icon: ImageView = (Activity() as MainActivity).findViewById(R.id.weather_icon)
    val wi = (Activity() as MainActivity).weatherInfo
    override fun run() {
        while (wi.isLocationExecuted() == false) {

            if (this.isInterrupted) {
                break;
            }

            spinner_icon.animate().rotation(180.0F).start()
            Thread.sleep(100)
        }
    }

}
