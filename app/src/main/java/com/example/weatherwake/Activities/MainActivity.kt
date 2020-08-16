package com.example.weatherwake.Activities

import android.Manifest
import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.location.Location
import android.location.LocationManager
import android.net.ConnectivityManager
import android.os.Bundle
import android.os.ProxyFileDescriptorCallback
import android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS
import android.view.Menu
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.weatherwake.APIs.WeatherAPI
import com.example.weatherwake.Classes.Alarm
import com.example.weatherwake.Classes.Alarms_Adapter
import com.example.weatherwake.R
import com.example.weatherwake.Threads.SpinnerThread
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.navigation.NavigationView
import kotlinx.android.synthetic.main.app_bar_main.* //allows direct access to the element without findViewByID

class MainActivity : AppCompatActivity() {
    private lateinit var appBarConfiguration: AppBarConfiguration

    //location variable
    lateinit var locManager: LocationManager
    private lateinit var fusedLocationClient: FusedLocationProviderClient

    //Weather API ... weatherInfo
    var weatherInfo: WeatherAPI = WeatherAPI()

    //Spinner Thread
    val st = Thread(SpinnerThread(this))

    //List of Alarms ... move to Fragment viewModel
    val alarms_list: ArrayList<Alarm> = ArrayList()

//    //For the alarm_recyclerView - testing
//    private lateinit var alarms_recyclerView1: RecyclerView //the actual recyclerView
//    private lateinit var alarms_recyclerViewAdapter1: RecyclerView.Adapter<*> //the adapter
//    private lateinit var alarms_recyclerViewManager1: RecyclerView.LayoutManager //measuring and positioning item views within a RecyclerView


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)

        //location manager
        locManager = this.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)


        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        //button to go to the alarm page (might change later so it doesnt go to a new page)
        val fab: FloatingActionButton = findViewById(R.id.new_alarm_button)
        fab.setOnClickListener { view ->
            val toAlarmMaker: Intent = Intent(applicationContext, AlarmMaker::class.java)
            startActivity(toAlarmMaker)
        }
        val drawerLayout: DrawerLayout = findViewById(R.id.drawer_layout)
        val navView: NavigationView = findViewById(R.id.nav_view)
        val navController = findNavController(R.id.nav_host_fragment)


        alarms_recyclerView.layoutManager = LinearLayoutManager(this)
        alarms_recyclerView.adapter = Alarms_Adapter(alarms_list)


        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.nav_home,
                R.id.nav_gallery,
                R.id.nav_slideshow,
                R.id.nav_tools
            ), drawerLayout
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)


    }//end of onCreate ends

    //start method... where the program starts
    override fun onStart() {
        super.onStart()

        updateWeather()


    }//end of onStart method

    override fun onResume() {
        super.onResume()

    }//end of onResume

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        if (intent != null) {
            if (intent.hasExtra("newAlarm")) {
                val newAlarm: Alarm = intent.extras?.get("newAlarm") as Alarm
                addAlarmToRecyclerView(newAlarm)
            }
        }
    }


    @SuppressLint("SetTextI18n")
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.main, menu)

        //start the loading thread. Will stop once weather data is loaded
        st.start()
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
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
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

    //method to see if the user location is enabled...is their location on even though they allowed us to use it
    private fun isLocationEnabled(): Boolean {
        val gps_enabled = locManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        val network_enabled = locManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
        return network_enabled || gps_enabled
    }

    //get the last known location
    fun getLastLocation(): DoubleArray {
        if (checkPermissions()) {
            if (isLocationEnabled()) {
                //checking permissions to use the location (not sure why needed since we check permissions earlier)
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
                        locManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)
                    if (location != null) {
                        return doubleArrayOf(location.latitude, location.longitude)
                    }
                    //might want to make a location listener instead of this
                    else {
                        fusedLocationClient.lastLocation.addOnSuccessListener { location ->

                        }
                        fusedLocationClient.lastLocation.addOnFailureListener {
                            Toast.makeText(this, "Location Failed", Toast.LENGTH_SHORT).show()
                        }
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
        return doubleArrayOf(0.0, 0.0)
    }

    //assert that the user has their internet connected
    private fun internetEnabled(): Boolean {
        val cm: ConnectivityManager =
            getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        var networkInfo: Boolean = cm.isDefaultNetworkActive

        if (!(networkInfo)) {
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

    //update the weather and the weather information
    public fun updateWeather(): Unit {
        val locArray: DoubleArray = getLastLocation()
        weatherInfo = WeatherAPI()
        weatherInfo.executeWeather(locArray[0], locArray[1])
    }

    public fun addAlarmToRecyclerView(newAlarm: Alarm) {
        alarms_list.add(newAlarm)
        alarms_recyclerView.adapter?.notifyItemInserted(alarms_list.size - 1);
    }

}
