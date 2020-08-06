package com.example.weatherwake.Threads

/*In this Thread, a spinner is to keep spinning until the weather is loaded in the side bar
* Thread keeps spinning until it is interrupted
*  */
import android.app.Activity
import android.graphics.Bitmap
import android.graphics.Matrix
import android.view.animation.AnimationUtils
import android.widget.ImageView
import androidx.core.view.drawToBitmap
import com.example.weatherwake.MainActivity
import com.example.weatherwake.R
import java.util.Collections.rotate


public class SpinnerThread(activity: Activity) : Thread() {
    var mActivity: Activity = activity
    var imgMatrix: Matrix = Matrix()

        override fun run() {
            var spinnerImageView: ImageView = mActivity.findViewById(R.id.weather_icon);
            var imgBitmap: Bitmap = spinnerImageView.drawToBitmap()
            println("THREAD RUNNING!!!!")
        while (true) {
            if (Thread.interrupted()) {
                println("THREAD INTERRUPTED")
                return
                TODO("When breaks, add all of the information to the page with weather")
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
//                Thread.sleep(100)
            }
        }

    }
}