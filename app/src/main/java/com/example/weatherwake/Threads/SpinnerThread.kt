package com.example.weatherwake.Threads

/*Spinner Thread. This thread is in charge of the spinner, which keeps spinning loading icon until weather data
is executed in the menu bar. Once executed thread updates weather data and finishes
 */

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.graphics.Matrix
import android.widget.ImageView
import android.widget.TextView
import androidx.core.view.drawToBitmap
import com.example.weatherwake.Activities.MainActivity
import com.example.weatherwake.R


public class SpinnerThread(activity: MainActivity) : Thread() {
    var mActivity: MainActivity = activity
    var imgMatrix: Matrix = Matrix()

    override fun run() {
        val spinnerImageView: ImageView = mActivity.findViewById(R.id.weather_icon);
        val imgBitmap = spinnerImageView.drawToBitmap()
        while (true) {
            if (mActivity.weatherInfo.isLocationExecuted() || interrupted()) {
                updateWeatherValues()
                return
            } else {
                //rotate spinner 15 degrees
                imgMatrix.postRotate(15.0F)

                val rotatedBitmap: Bitmap = Bitmap.createBitmap(
                    imgBitmap,
                    0,
                    0,
                    imgBitmap.width,
                    imgBitmap.height,
                    imgMatrix,
                    true
                )

                mActivity.runOnUiThread(Runnable {
                    val spinnerIcon = mActivity.findViewById<ImageView>(R.id.weather_icon)
                    spinnerIcon.setImageBitmap(rotatedBitmap)
                })
                Thread.sleep(100)
            }
        }

    }

    //updates the weather values once thread is interrupted
    @SuppressLint("SetTextI18n")
    private fun updateWeatherValues(): Unit {
        val weatherMain: TextView = mActivity.findViewById(R.id.weather_main)
        val weatherDesc: TextView = mActivity.findViewById(R.id.weather_main_description)
        val iconIV: ImageView = mActivity.findViewById(R.id.weather_icon)
        val weatherInfo = mActivity.weatherInfo

        mActivity.runOnUiThread(Runnable {
            weatherMain.setText(
                weatherInfo.getWeather("main") + " " + weatherInfo.getCurrentTemp(
                    "temp",
                    'f'
                ) + "°F"
            )
            weatherDesc.setText(weatherInfo.getWeather("description"))
            iconIV.setImageDrawable(weatherInfo.getWeatherIcon())
        })
    }
}