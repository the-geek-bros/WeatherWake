package com.example.weatherwake.APIs

/*Weather API. Retreives weather and weather details of user's coordinates*/

import android.graphics.drawable.Drawable
import android.os.AsyncTask
import org.json.JSONObject
import java.io.InputStream
import java.lang.Exception
import java.net.HttpURLConnection
import java.net.URL
import java.sql.Time
import java.util.concurrent.TimeUnit
import kotlin.collections.HashMap


private var locationExecuted: Boolean = false
val loc: DoubleArray = DoubleArray(2)

//data that will be collected
////for current weather
private var current_main: String = "Did not load"
private var current_description: String = "Did not load"
private lateinit var current_icon_url: String
private var icon_image: Drawable? = null

///current temperatures
private val kelvin_temps: HashMap<String, Double> = HashMap<String, Double>()

///wind
private var current_wind: Double = Double.MIN_VALUE

///city
private var current_city: String = "----"

public class WeatherAPI : AsyncTask<Void, Void, String>() {


////////////////////////////////////////////////////////////
    /*Returns data functions*/

    /*Below are the Return Data Functions for all weather possibilities*/

    //get current temperature info..temp,feels_like,temp_min,temp_max
    // temp system ,c or f
    public fun getCurrentTemp(info: String, temp_system: Char): Double {
        when (temp_system) {
            'c' -> return round(kelvin_temps.getOrDefault(info, 0.0) - 273.15, 1)
            'f' -> return round((kelvin_temps.getOrDefault(info, 0.0) - 273.15) * 9 / 5 + 32, 1)
        }
        return Double.MIN_VALUE
    }

    //get main descriptions (main or description) Ex: Main: Cloudy, Description: Partially Cloudy. Description: overcast clouds
    public fun getWeather(info: String): String {
        when (info) {
            "main" -> return current_main
            "description" -> return current_description
        }
        return ""
    }

    //get wind (s for meters per second, m for metric kmh, i for imperial mph)
    public fun getWindSpeed(temp_system: Char): Double {
        when (temp_system) {
            's' -> return round(current_wind, 3)  //meters per sec
            'm' -> return current_wind * 3.6 //kmh
            'i' -> return current_wind * 2.237 //mph
        }
        return Double.MIN_VALUE
    }

    //get city of location
    public fun getCity(): String {
        return current_city
    }

    //get the icon_image for current weather
    public fun getWeatherIcon(): Drawable? {
        return icon_image
    }

    /////////////////////////////////////////////////////////
    //Lets program know whether the location was execute
    public fun isLocationExecuted(): Boolean {
        return locationExecuted
    }


    /////////////////////////////////////////////////////////
    //used to execute weather based on user location
    public fun executeWeather(lat: Double, lon: Double): Unit {
        locationExecuted = false


        loc.set(0, lat)
        loc.set(1, lon)
        execute()
    }

    override fun doInBackground(vararg params: Void?): String? {
        val appid: String = "37a38145e9f4f2f63769c4998a86ca71"
        val urlString: String =
            "https://api.openweathermap.org/data/2.5/weather?lat=" + loc.get(0) + "&lon=" + loc.get(
                1
            ) + "&appid=" + appid
        val url: URL = URL(urlString)
        val data: String
        val connection = url.openConnection() as HttpURLConnection
        try {
            connection.connect()
            data = connection.inputStream.use { it.reader().use { reader -> reader.readText() } }
        } finally {
            connection.disconnect()
        }
        //getting the image portion now
        val current_icon_id: String =
            JSONObject(data).getJSONArray("weather").getJSONObject(0).getString("icon")
        current_icon_url = "https://openweathermap.org/img/wn/" + current_icon_id + "@2x.png"
        try {
            val input: InputStream = URL(current_icon_url).content as InputStream
            icon_image = Drawable.createFromStream(input, "src name")
        } catch (e: Exception) {
            println("fail  " + e)
        }
        getJsonInfo(data)
        locationExecuted = true
        return data
    }

    private fun getJsonInfo(data: String?) {
        val dataObjectInput: JSONObject = JSONObject(data)
        //get the weather description and icon URL
        val weatherArrayObj: JSONObject =
            dataObjectInput.getJSONArray("weather").get(0) as JSONObject
        current_main = weatherArrayObj.getString("main")
        current_description = weatherArrayObj.getString("description")
        current_icon_url =
            "https://openweathermap.org/img/wn/" + weatherArrayObj.getString("icon") + "@2x.png"


        //get the temperature
        val temperatureData: JSONObject = dataObjectInput.get("main") as JSONObject
        kelvin_temps.put("temp", temperatureData.getDouble("temp"))
        kelvin_temps.put("feels_like", temperatureData.getDouble("feels_like"))
        kelvin_temps.put("temp_min", temperatureData.getDouble("temp_min"))
        kelvin_temps.put("temp_max", temperatureData.getDouble("temp_max"))

        //wind
        current_wind = dataObjectInput.getJSONObject("wind").getDouble("speed")
        //clouds (maybe)

        //sunrise and sunset (maybe)

        //city location
        current_city = dataObjectInput.getString("name")

    }

    //round values
    private fun round(value: Double, after_dot: Int): Double {
        val int_form: Int = (value * (Math.pow(10.0, after_dot.toDouble()))).toInt()
        return (int_form / Math.pow(10.0, after_dot.toDouble()))
    }


}
