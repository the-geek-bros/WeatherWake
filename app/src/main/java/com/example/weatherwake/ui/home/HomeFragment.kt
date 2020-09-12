package com.example.weatherwake.ui.home

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.RecyclerView
import com.example.weatherwake.APIs.WeatherAPI
import com.example.weatherwake.Activities.AlarmMaker
import com.example.weatherwake.Activities.MainActivity
import com.example.weatherwake.Classes.Alarm
import com.example.weatherwake.R
import com.example.weatherwake.Threads.SpinnerThread
import com.google.android.material.floatingactionbutton.FloatingActionButton

class HomeFragment : Fragment() {


    private lateinit var homeViewModel: HomeViewModel

    //for the ui
    lateinit var alarms_recyclerView: RecyclerView
    lateinit var fab: FloatingActionButton

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        homeViewModel =
                ViewModelProviders.of(this).get(HomeViewModel::class.java)
        val root = inflater.inflate(R.layout.fragment_home, container, false);




        return root
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onStart() {
        super.onStart()


    }

    fun onClick() {
        println("Button clicked")
        val toAlarmMaker = Intent(activity?.applicationContext,AlarmMaker::class.java)
        startActivity(toAlarmMaker)
    }


}