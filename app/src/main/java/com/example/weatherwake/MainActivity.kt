package com.example.weatherwake

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.drawable.Drawable
import android.location.Location
import android.location.LocationManager
import android.net.ConnectivityManager
import android.os.Bundle
import android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS
import android.view.LayoutInflater
import android.view.Menu
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.example.weatherwake.APIs.WeatherAPI
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.navigation.NavigationView
import com.google.android.material.snackbar.Snackbar


class MainActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration

    //location variable
    var locManager: LocationManager? = null

    //for the API attempt
    val weatherInfo: WeatherAPI = WeatherAPI()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)

        locManager = this.getSystemService(Context.LOCATION_SERVICE) as LocationManager

        val locArray: DoubleArray = getLastLocation()
        weatherInfo.executeWeather(locArray[0], locArray[1])

        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        val fab: FloatingActionButton = findViewById(R.id.new_alarm_button)
        fab.setOnClickListener { view ->
            val toAlarmMaker: Intent = Intent(applicationContext,AlarmMaker::class.java)
            startActivity(toAlarmMaker)
        }
        val drawerLayout: DrawerLayout = findViewById(R.id.drawer_layout)
        val navView: NavigationView = findViewById(R.id.nav_view)
        val navController = findNavController(R.id.nav_host_fragment)

        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.nav_home, R.id.nav_gallery, R.id.nav_slideshow,
                R.id.nav_tools
            ), drawerLayout
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)

        //delay to give time for Weather API to get information. Delays main thread until info comes in.
        while (!weatherInfo.isLocationExecuted()){
            Thread.sleep(500)
            println("UPDATES??  "+weatherInfo.isLocationExecuted())
        }
        Thread.sleep(500)


    }//end of onCreate ends

    //start method... where the program starts
    @SuppressLint("SetTextI18n")
    override fun onStart() {
        super.onStart()

        println("CURRENT WEATHER  " + weatherInfo.getCurrentTemp("temp", 'f'))

    }//end of onStart method

    override fun onResume() {
        super.onResume()

    }//end of onResume


    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.main, menu)

        //add drawing to the menu header
        var a: Drawable? = weatherInfo.getWeatherIcon()
        if (findViewById<ImageView>(R.id.weather_icon) != null) {
            val weather_icon: ImageView = findViewById(R.id.weather_icon)
            weather_icon.setImageDrawable(a)
        }
        //add the text and the description to the menu header
        if (findViewById<TextView>(R.id.weather_main) != null && findViewById<TextView>(R.id.weather_main_description) != null) {
            val weather_main_text: TextView = findViewById(R.id.weather_main)
            val weather_description_text: TextView = findViewById(R.id.weather_main_description)
            weather_main_text.setText(
                weatherInfo.getWeather("main") + ",  " + weatherInfo.getCurrentTemp("temp",'f') + "°F")
            val desc: String = titleCase(weatherInfo.getWeather("description").toString())
            weather_description_text.setText(desc)
        }

        return true
    }
    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }


    /***LOCATION METHODS****/
    private val PERMISSION_ID = 50

    //tell us whether or not the user grant us to access ACCESS_COARSE_LOCATION and ACCESS_FINE_LOCATION
    private fun checkPermissions(): Boolean {
        if (ContextCompat.checkSelfPermission(
                this,
                android.Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            return true
        }
        return false
    }

    //request permissions from user if they have not given us this access already
    private fun requestPermissions() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(
                this,
                android.Manifest.permission.ACCESS_FINE_LOCATION
            )
        ) {
            val alert = AlertDialog.Builder(this)
            alert.setTitle("Location Required")
            alert.setMessage("This application requires your location to access weather")
            alert.setPositiveButton("OK", DialogInterface.OnClickListener { dialog, which ->
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION), PERMISSION_ID
                )
            })
            alert.setNegativeButton("Dismiss", DialogInterface.OnClickListener { dialog, which ->
                dialog.dismiss()
            })
                .create().show()
        } else {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION), PERMISSION_ID
            )
        }
    }

    //point at which user Allow or Deny our requested permissions
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        if (requestCode == PERMISSION_ID)
            if (grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Location has been allowed", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "PERMISSION NOT GRANTED", Toast.LENGTH_SHORT).show()
            }
    }

    //method to see if the user location is enabled...is their location on even though they allowed us to use it
    private fun isLocationEnabled(): Boolean {
        val locationManager: LocationManager =
            getSystemService(Context.LOCATION_SERVICE) as LocationManager
        val gps_enabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        return locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER) || gps_enabled
    }

    //get the last known location
    fun getLastLocation(): DoubleArray {
        if (checkPermissions()) {
            if (isLocationEnabled()) {
                //checking permissions to use the location (not sure why needed since we check permissions earlier
                if (ActivityCompat.checkSelfPermission(
                        this,
                        Manifest.permission.ACCESS_FINE_LOCATION
                    ) == PackageManager.PERMISSION_GRANTED &&
                    ActivityCompat.checkSelfPermission(
                        this,
                        Manifest.permission.ACCESS_COARSE_LOCATION
                    ) == PackageManager.PERMISSION_GRANTED
                ) {

                    val location: Location? =
                        locManager?.getLastKnownLocation(LocationManager.GPS_PROVIDER)
                    if (location != null) {
                        return doubleArrayOf(location.latitude, location.longitude)
                    }
                    //might want to make a location listener instead of this
                    else {
                        Toast.makeText(this, "Location not used...", Toast.LENGTH_SHORT).show()
                    }
                }
            } else {
                createAlertDialog(
                    "Location not on",
                    "Need to turn on location to use the app",
                    "OK",
                    "LOCATIONACCESS"
                )
            }
        } else {
            requestPermissions()
        }
        return doubleArrayOf(-Double.MIN_VALUE, Double.MAX_VALUE)
    }

    //assert that the user has their internet connected
    private fun internetEnabled(): Boolean {
        val cm: ConnectivityManager =
            getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        var networkInfo: Boolean = cm.isDefaultNetworkActive

        if (!(networkInfo != null && networkInfo == true)) {
            createAlertDialog(
                "No Internet Connection",
                "Internet Connection needed to get weather",
                "Dismiss",
                "DISMISS"
            )
            return false;
        }
        return true
    }

    //*******OTHER METHODS **///
    //method allows to create alert dialogs
    public fun createAlertDialog(
        title: String,
        message: String,
        btnText: String,
        postAction: String
    ) {
        val myAlert: AlertDialog = AlertDialog.Builder(this).create()
        myAlert.setCancelable(false)
        myAlert.setTitle(title)
        myAlert.setMessage(message)
        myAlert.setButton(
            Dialog.BUTTON_NEGATIVE,
            btnText,
            DialogInterface.OnClickListener { dialog, which ->
                when (postAction) {
                    "QUIT" -> finishAffinity()
                    "DISMISS" -> myAlert.dismiss()
                    "LOCATIONACCESS" -> startActivity(Intent(ACTION_LOCATION_SOURCE_SETTINGS))
                }
            })
//            myAlert.getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE, WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE);
        myAlert.show()
    }

    public fun titleCase(s: String): String {
        val listArr: List<String> = s.split(" ")
        for (word in listArr) {
            word[0].toUpperCase()
        }
        val newString: String = listArr.joinToString(" ")
        return newString
    }


}
