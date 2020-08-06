package com.example.weatherwake.Threads

/*In this Thread, a spinner is to keep spinning until the weather is loaded in the side bar
* Thread keeps spinning until it is interrupted
*  */

import android.graphics.Bitmap
import android.graphics.Matrix
import android.widget.ImageView
import android.widget.TextView
import androidx.core.view.drawToBitmap
import com.example.weatherwake.MainActivity
import com.example.weatherwake.R


public class SpinnerThread(activity: MainActivity) : Thread() {
    var mActivity: MainActivity = activity
    var imgMatrix: Matrix = Matrix()

    override fun run() {
        var spinnerImageView: ImageView = mActivity.findViewById(R.id.weather_icon);
        var imgBitmap: Bitmap = spinnerImageView.drawToBitmap()
        println("THREAD RUNNING!!!!")
        while (true) {
            if (mActivity.weatherInfo.isLocationExecuted() || interrupted()) {
                println("THREAD INTERRUPTED")
                updateWeatherValues()

                return
            } else {
                //rotate spinner 15 degrees
                imgMatrix.postRotate(15.0F)

                var rotatedBitmap: Bitmap = Bitmap.createBitmap(
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
    private fun updateWeatherValues(): Unit {
        val weatherMain: TextView = mActivity.findViewById(R.id.weather_main)
        val weatherDesc: TextView = mActivity.findViewById(R.id.weather_main_description)
        val iconIV: ImageView = mActivity.findViewById(R.id.weather_icon)
        val currentLocation: TextView = mActivity.findViewById(R.id.currentLocation)
        val weatherInfo = mActivity.weatherInfo

        mActivity.runOnUiThread(Runnable {
            weatherMain.setText(
                weatherInfo.getWeather("main") +" "+ weatherInfo.getCurrentTemp(
                    "temp",
                    'f'
                ) + "°F"
            )
            weatherDesc.setText(weatherInfo.getWeather("description"))
            currentLocation.setText(weatherInfo.getCity())
            iconIV.setImageDrawable(weatherInfo.getWeatherIcon())
        })
    }
}