package com.kreatar.postreality

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

public class SpinnerThread : Thread() {
    val mActivity: MainActivity = MainActivity()
    val spinner_icon: ImageView = mActivity.findViewById(R.id.weather_icon)
    override fun run() {
        while (true) {
            TODO("make the spinner rotate")
        }
    }

}