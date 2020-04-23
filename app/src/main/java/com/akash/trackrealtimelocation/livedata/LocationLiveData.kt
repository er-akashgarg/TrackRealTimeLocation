package com.akash.trackrealtimelocation.livedata

import android.annotation.SuppressLint
import android.content.Context
import android.location.Location
import android.util.Log
import androidx.lifecycle.LiveData
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices

/**
 * @author Akash Garg
 * */

class LocationLiveData(context: Context) : LiveData<LocationModel>() {

    private var fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)

    override fun onInactive() {
        super.onInactive()
        fusedLocationClient.removeLocationUpdates(locationCallback)
    }


    @SuppressLint("MissingPermission")
    override fun onActive() {
        super.onActive()
        fusedLocationClient.lastLocation
                .addOnSuccessListener { location: Location? ->
                    location?.also {
                        setLocationData(it)
                    }
                }
        startLocationUpdates()
    }

    @SuppressLint("MissingPermission")
    private fun startLocationUpdates() {
        fusedLocationClient.requestLocationUpdates(
                locationRequest,
                locationCallback,
                null
        )
    }

    private val locationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult?) {
            locationResult ?: return
            for (location in locationResult.locations) {
                setLocationData(location)
            }
        }
    }

    private fun setLocationData(location: Location) {
        value = LocationModel(
                longitude = location.longitude,
                latitude = location.latitude,
                bearing = location.bearing
        )
        Log.e("LocationLiveData", "value-> ${value.toString()}")
    }

    companion object {

        // every 4 seconds update the lat lng..
        val locationRequest: LocationRequest = LocationRequest.create().apply {
            interval = 8000
            fastestInterval = 4000
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        }
    }
}

data class LocationModel(
        val longitude: Double,
        val latitude: Double,
        val bearing: Float
)
