package com.example.weatherwake.Classes
//package utilities

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import android.net.ConnectivityManager
import android.provider.Settings
import android.widget.Toast
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices

class Utilities(activity: Activity) {

    private var activity: Activity

    init {
        this.activity = activity
    }

    //location variable
    val locManager: LocationManager =
        activity.getSystemService(Context.LOCATION_SERVICE) as LocationManager
    val fusedLocationClient: FusedLocationProviderClient =
        LocationServices.getFusedLocationProviderClient(activity)


    /***LOCATION METHODS****/
    private val PERMISSION_ID = 50

    //tell us whether or not the user grant us to access ACCESS_COARSE_LOCATION and ACCESS_FINE_LOCATION
    private fun checkPermissions(): Boolean {
        if (ActivityCompat.checkSelfPermission(
                activity.applicationContext,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(
                activity.applicationContext,
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
                activity,
                android.Manifest.permission.ACCESS_FINE_LOCATION
            )
        ) {
            val alert = AlertDialog.Builder(activity)
            alert.setTitle("Location Required")
            alert.setMessage("This application requires your location to access weather")
            alert.setPositiveButton("OK", DialogInterface.OnClickListener { dialog, which ->
                ActivityCompat.requestPermissions(
                    activity,
                    arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION), PERMISSION_ID
                )
            })
            alert.setNegativeButton("Dismiss", DialogInterface.OnClickListener { dialog, which ->
                dialog.dismiss()
            })
                .create().show()
        } else {
            ActivityCompat.requestPermissions(
                activity,
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
                        activity.applicationContext,
                        Manifest.permission.ACCESS_FINE_LOCATION
                    ) == PackageManager.PERMISSION_GRANTED &&
                    ActivityCompat.checkSelfPermission(
                        activity.applicationContext,
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
                            Toast.makeText(
                                activity.applicationContext,
                                "Location Failed",
                                Toast.LENGTH_SHORT
                            ).show()
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

    //method allows to create alert dialogs
    public fun createAlertDialog(
        title: String,
        message: String,
        btnText: String,
        postAction: String
    ) {
        val myAlert: AlertDialog = AlertDialog.Builder(activity.applicationContext).create()
        myAlert.setCancelable(false)
        myAlert.setTitle(title)
        myAlert.setMessage(message)
        myAlert.setButton(
            Dialog.BUTTON_NEGATIVE,
            btnText,
            DialogInterface.OnClickListener { dialog, which ->
                when (postAction) {
                    "QUIT" -> activity.finishAffinity()
                    "DISMISS" -> myAlert.dismiss()
                    "LOCATIONACCESS" -> activity.startActivity(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS))
                }
            })
//            myAlert.getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE, WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE);
        myAlert.show()
    }


    //assert that the user has their internet connected
    private fun internetEnabled(): Boolean {
        val cm: ConnectivityManager =
            activity.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
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

}