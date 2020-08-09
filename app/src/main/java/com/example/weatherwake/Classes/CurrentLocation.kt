package com.example.weatherwake.Classes

import android.app.Activity
import android.content.Context
import android.location.LocationManager
import com.example.weatherwake.Activities.MainActivity

public class CurrentLocation(mActivity: MainActivity) {

    var locManager: LocationManager = mActivity.getSystemService(Context.LOCATION_SERVICE) as LocationManager


}