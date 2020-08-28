package com.example.weatherwake.Activities
/* Main Activity. Is the launcher page of the application. Shows current alarms as well as the button to create a new alarm.
 */

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.isInvisible
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.weatherwake.APIs.WeatherAPI
import com.example.weatherwake.Classes.Alarm
import com.example.weatherwake.Classes.AlarmHandlers
import com.example.weatherwake.Classes.Alarms_Adapter
import com.example.weatherwake.Classes.Utilities
import com.example.weatherwake.R
import com.example.weatherwake.Threads.SpinnerThread
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.navigation.NavigationView
import kotlinx.android.synthetic.main.app_bar_main.* //allows direct access to the element without findViewByID
import java.util.*

class MainActivity : AppCompatActivity() {
    private lateinit var appBarConfiguration: AppBarConfiguration

    //Weather API ... weatherInfo
    var weatherInfo: WeatherAPI = WeatherAPI()

    //Spinner Thread
    val st = Thread(SpinnerThread(this))

    //List of Alarms ... move to Fragment viewModel
    val alarms_list: LinkedList<Alarm> = LinkedList()

//    //For the alarm_recyclerView - testing
//    private lateinit var alarms_recyclerView1: RecyclerView //the actual recyclerView
//    private lateinit var alarms_recyclerViewAdapter1: RecyclerView.Adapter<*> //the adapter
//    private lateinit var alarms_recyclerViewManager1: RecyclerView.LayoutManager //measuring and positioning item views within a RecyclerView


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)

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
            if(intent.hasExtra("alarmToDelete")){
                val alarmToDelete:Alarm = intent.extras?.get("alarmToDelete") as Alarm
                deleteAlarm(alarmToDelete)
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

    //*******OTHER METHODS **///

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
//        val locArray: DoubleArray = getLastLocation()
        val utils = Utilities(this)
        val locArray = utils.getLastLocation()
        weatherInfo = WeatherAPI()
        weatherInfo.executeWeather(locArray[0], locArray[1])
    }

    public fun addAlarmToRecyclerView(newAlarm: Alarm) {
        if(alarms_list.isEmpty()){
            textViewNoAlarms.isInvisible=true
        }
        var alarmOrderAdded = false
        var locationAdded = alarms_list.size
        for (x in 0 until alarms_list.size) {
            if (newAlarm.timeInt() < alarms_list[x].timeInt()) {
                alarms_list.add(x, newAlarm)
                locationAdded = x
                alarmOrderAdded = true
                break
            }
        }
        if (!alarmOrderAdded)
            alarms_list.add(newAlarm)
        alarms_recyclerView.adapter?.notifyItemInserted(locationAdded);
    }

    public fun deleteAlarm(alarmToDelete: Alarm) {
        for (x in 0 until alarms_list.size) {
            if (alarms_list[x].getAlarmId() == alarmToDelete.getAlarmId()) {
                //remove intent of alarm
                val alarmHandler = AlarmHandlers(this)
                alarmHandler.cancelAlarmInAlarmManager(alarms_list[x])
                //remove alarm from recyclerView
                alarms_list.removeAt(x)
                alarms_recyclerView.adapter?.notifyItemRemoved(x)
                break
            }
        }
        if(alarms_list.isEmpty()){
            textViewNoAlarms.isInvisible = false
        }
    }


}
