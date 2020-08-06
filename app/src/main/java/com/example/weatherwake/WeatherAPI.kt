package com.example.weatherwake

import android.app.Activity
import android.os.AsyncTask
import androidx.annotation.MainThread
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.io.BufferedInputStream
import java.io.BufferedReader
import java.io.InputStream
import java.io.InputStreamReader
import java.lang.Exception
import java.net.HttpURLConnection
import java.net.URL
import java.util.concurrent.TimeUnit

private var dataJSON: JSONObject? = null
lateinit var loc: DoubleArray

public class WeatherAPI : AsyncTask<Void, Void, String>() {

    init {
        dataJSON = null
        loc = DoubleArray(2)
    }


    //returns the data json
    public fun getCurrentWeather(): JSONObject? {
        return dataJSON
    }

    //used to execute based on user location
    public fun executeWeather(lat: Double, lon: Double): Unit {
        loc.set(0, lat)
        loc.set(1, lon)
        TimeUnit.SECONDS.sleep(15)
        execute()
    }

    override fun doInBackground(vararg params: Void?): String? {
        val appid: String = "37a38145e9f4f2f63769c4998a86ca71"
        val urlString: String =
            "https://api.openweathermap.org/data/2.5/weather?lat=" + loc.get(0) + "&lon=" + loc.get(
                1
            ) + "&appid=" + appid
        println("THE URL IS " + urlString)

        val url: URL = URL(urlString)

        val data: String
        val connection = url.openConnection() as HttpURLConnection
        try {
            connection.connect()
            data = connection.inputStream.use { it.reader().use { reader -> reader.readText() } }

            println("DATA!!!!  " + data)

        } finally {
            connection.disconnect()
        }
        return data
    }

    override fun onPostExecute(result: String?) {
        super.onPostExecute(result)
        handleResult(result)
    }

    fun handleResult(info: String?) {
        dataJSON = JSONObject(info)
        println("DATA JSON  " + dataJSON)
    }
}
