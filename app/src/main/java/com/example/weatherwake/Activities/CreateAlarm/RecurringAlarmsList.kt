package com.example.weatherwake.Activities.CreateAlarm

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isInvisible
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.weatherwake.Activities.AlarmMaker
import com.example.weatherwake.R
import kotlinx.android.synthetic.main.activity_recurring_alarms_list.*
import kotlinx.android.synthetic.main.recurring_alarm_row.view.*
import java.util.*

class RecurringAlarms : AppCompatActivity(), OnItemClickListenerDayOfWeek {

    val daysOfWeekChosen: TreeSet<Int> = TreeSet()
    lateinit var btnSaveDates: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_recurring_alarms_list)

        recyclerView_DaysOfWeek.layoutManager = LinearLayoutManager(this)
        recyclerView_DaysOfWeek.adapter = DaysOfWeekAdapter(this, this)
        btnSaveDates = findViewById(R.id.btnSaveDaysOfWeek)

        btnSaveDates.setOnClickListener { view ->
            if (daysOfWeekChosen.size == 0) {
                Toast.makeText(this, "Need to choose at least one day", Toast.LENGTH_LONG).show()
            } else {
                val backToAlarmMaker = Intent(applicationContext, AlarmMaker::class.java)
                backToAlarmMaker.putExtra("Days Chosen", daysOfWeekChosen.toIntArray())
                backToAlarmMaker.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT)
                startActivity(backToAlarmMaker)
            }

        }


    }

    override fun onItemClick(item: String, position: Int) {
        if (!daysOfWeekChosen.contains(position)) {
            daysOfWeekChosen.add(position) //add day of week (0-6)
        } else {
            daysOfWeekChosen.remove(position)
        }
    }


}
/*ADAPTER*/

class DaysOfWeekAdapter(
    context: Context,
    var onItemClickListenerDayOfWeek: OnItemClickListenerDayOfWeek
) : RecyclerView.Adapter<DaysOfWeek_ViewHolder>() {
    var daysOfWeek: Array<String>
    var context: Context

    init {
        this.context = context
        this.daysOfWeek = context.resources.getStringArray(R.array.daysOfWeek)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DaysOfWeek_ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val cellForRow: View = layoutInflater.inflate(R.layout.recurring_alarm_row, parent, false)

        return DaysOfWeek_ViewHolder(cellForRow)
    }

    override fun getItemCount(): Int {
        return daysOfWeek.size
    }

    override fun onBindViewHolder(holder: DaysOfWeek_ViewHolder, position: Int) {
        holder.initialize(daysOfWeek.get(position), onItemClickListenerDayOfWeek)
    }
}


class DaysOfWeek_ViewHolder(view: View) : RecyclerView.ViewHolder(view) {

    fun initialize(item: String, action: OnItemClickListenerDayOfWeek) {
        itemView.dayOfWeek.text = item
        itemView.checkmark.isInvisible = true

        itemView.setOnClickListener { view ->
            action.onItemClick(item, adapterPosition)
            itemView.checkmark.isInvisible = !itemView.checkmark.isInvisible
        }
    }
}


//interface for the OnClick function
interface OnItemClickListenerDayOfWeek {
    fun onItemClick(item: String, position: Int)


}