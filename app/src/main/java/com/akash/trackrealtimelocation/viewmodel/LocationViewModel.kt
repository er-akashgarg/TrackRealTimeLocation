package com.akash.trackrealtimelocation.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.akash.trackrealtimelocation.livedata.LocationLiveData

/**
 * @author Akash Garg
 * */

class LocationViewModel(application: Application) : AndroidViewModel(application) {

    private val locationData = LocationLiveData(application)

    fun getLocationData() = locationData
}
