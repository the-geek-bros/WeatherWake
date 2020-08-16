package com.example.weatherwake.Classes

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.weatherwake.Activities.CreateAlarm.alarmCalendarView
import com.example.weatherwake.R
import kotlinx.android.synthetic.main.alarm_row.view.*

class Alarms_Adapter(alarms_list: ArrayList<Alarm>) : RecyclerView.Adapter<Alarm_ViewHolder>() {

    private val alarms_list: ArrayList<Alarm>

    init {
        this.alarms_list = alarms_list
    }

    //number of items
    override fun getItemCount(): Int {
        return alarms_list.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Alarm_ViewHolder {
        //how to create a view
        val layoutInflater: LayoutInflater = LayoutInflater.from(parent.context)
        val cellForRow = layoutInflater.inflate(R.layout.alarm_row, parent, false)

        return Alarm_ViewHolder(cellForRow)
    }

    override fun onBindViewHolder(holder: Alarm_ViewHolder, position: Int) {
        /*Method binds the data to the Recycler View here */
        val iteratingAlarm = alarms_list.get(position)
        holder.view.alarm_template_time.text = iteratingAlarm.getAlarmTime()
        holder.view.alarm_template_date.text = iteratingAlarm.getAlarmDateInfo()
        holder.view.alarm_template_description.text = iteratingAlarm.getAlarmDescription()
        holder.view.alarm_template_switch.isChecked = true


    }


}

//ViewHolder Class
class Alarm_ViewHolder(val view: View) : RecyclerView.ViewHolder(view) {


}